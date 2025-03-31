/*
 * Copyright 2020-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package hu.perit.template.authservice.services.impl.user;

import hu.perit.spvitamin.spring.exception.CannotProcessException;
import hu.perit.spvitamin.spring.exception.ResourceAlreadyExistsException;
import hu.perit.spvitamin.spring.exception.ResourceNotFoundException;
import hu.perit.spvitamin.spring.security.AuthenticatedUser;
import hu.perit.template.authservice.db.demodb.table.RoleEntity;
import hu.perit.template.authservice.db.demodb.table.UserEntity;
import hu.perit.template.authservice.mapper.EntityMapper;
import hu.perit.template.authservice.model.CreateUserParams;
import hu.perit.template.authservice.model.RoleSet;
import hu.perit.template.authservice.model.UpdateUserParams;
import hu.perit.template.authservice.model.UserDTO;
import hu.perit.template.authservice.model.UserDTOFiltered;
import hu.perit.template.authservice.services.api.UserService;
import hu.perit.template.authservice.services.api.entity.RoleEntityService;
import hu.perit.template.authservice.services.api.entity.UserEntityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Manages CRUD operations. All the concurrency issues are handled by the persistence layer.
 *
 * @author Peter Nagy
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService
{
    public static final String EXTERNAL_USER_CANNOT_BE_UPDATED = "External user cannot be updated!";

    private final UserEntityService userEntityService;
    private final RoleEntityService roleEntityService;
    private final PasswordEncoder passwordEncoder;
    private final EntityMapper entityMapper;

    //------------------------------------------------------------------------------------------------------------------
    // getAll
    //------------------------------------------------------------------------------------------------------------------
    @Override
    public List<UserDTOFiltered> getAll()
    {
        List<UserEntity> all = this.userEntityService.findAll();

        return this.entityMapper.mapFilteredFromEntity(all);
    }


    //------------------------------------------------------------------------------------------------------------------
    // getUserDTOById
    //------------------------------------------------------------------------------------------------------------------
    @Override
    public UserDTO getUserDTOById(Long userId) throws ResourceNotFoundException
    {
        UserEntity entity = getUserEntityById(userId);
        return this.entityMapper.mapFromEntity(entity);
    }


    public UserEntity getUserEntityById(Long userId) throws ResourceNotFoundException
    {
        UserEntity entity = this.userEntityService.findById(userId).orElse(null);
        if (entity != null)
        {
            return entity;
        }
        else
        {
            throw new ResourceNotFoundException(String.format("No user found by userId: %d", userId));
        }
    }


    //------------------------------------------------------------------------------------------------------------------
    // create
    //------------------------------------------------------------------------------------------------------------------
    @Override
    public long create(CreateUserParams createUserParams)
    {
        return this.create(createUserParams, false);
    }


    @Override
    public long create(CreateUserParams createUserParams, Boolean external)
    {
        UserEntity existingUser = this.userEntityService.findByUserName(createUserParams.getUserName()).orElse(null);
        if (existingUser != null)
        {
            throw new ResourceAlreadyExistsException(
                    String.format("A user with username '%s' already exists!", createUserParams.getUserName()));
        }

        UserEntity userEntity = this.entityMapper.mapFromCreateParams(createUserParams);

        // Encrypting password
        if (StringUtils.isNotBlank(createUserParams.getPassword()))
        {
            userEntity.setEncryptedPassword(passwordEncoder.encode(createUserParams.getPassword()));
        }

        // Roles
        if (createUserParams.getRoles() != null)
        {
            Set<RoleEntity> roles = this.getRoleEntitiesByRoleNames(createUserParams.getRoles());
            userEntity.setRoleEntities(roles);
        }

        // External
        userEntity.setExternal(external);

        // Saving the user
        UserEntity newUser = this.userEntityService.save(userEntity);

        return newUser.getUserId();
    }


    private boolean resultSetContainsRole(Set<RoleEntity> roles, String role)
    {
        return roles.stream().anyMatch(i -> i.getRole().equalsIgnoreCase(role));
    }


    //------------------------------------------------------------------------------------------------------------------
    // createAtLogin
    //------------------------------------------------------------------------------------------------------------------
    @Override
    public long createAtLogin(AuthenticatedUser authenticatedUser)
    {
        if (StringUtils.isNotBlank(authenticatedUser.getUserId()))
        {
            return Long.parseLong(authenticatedUser.getUserId());
        }
        else
        {
            UserEntity userEntity = this.userEntityService.findByUserName(authenticatedUser.getUsername()).orElse(null);
            if (userEntity != null)
            {
                return userEntity.getUserId();
            }
            else
            {
                // The user is not yet saved in our internal user db => lets save it
                log.debug("Authenticated user {} will be saved in the user database.", authenticatedUser.getUsername());
                CreateUserParams createUserParams = CreateUserParams.builder()
                        .userName(authenticatedUser.getUsername())
                        .displayName(authenticatedUser.getUsername())
                        .active(true)
                        .roles(authenticatedUser.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet())).nextLoginChangePwd(false)
                        .build();
                long newUserId = this.create(createUserParams, true);
                this.updateLoginTime(newUserId);
                return newUserId;
            }
        }
    }


    //------------------------------------------------------------------------------------------------------------------
    // update
    //------------------------------------------------------------------------------------------------------------------
    @Override
    public void update(Long userId, UpdateUserParams updateUserParams) throws ResourceNotFoundException
    {
        UserEntity userEntity = getUserEntityById(userId);

        if (BooleanUtils.isTrue(userEntity.getExternal()))
        {
            throw new CannotProcessException(EXTERNAL_USER_CANNOT_BE_UPDATED);
        }

        this.entityMapper.update(userEntity, updateUserParams);

        // Encrypting passwords
        if (StringUtils.isNotBlank(updateUserParams.getPassword()))
        {
            userEntity.setEncryptedPassword(passwordEncoder.encode(updateUserParams.getPassword()));
        }

        // Roles
        if (updateUserParams.getRoles() != null)
        {
            Set<RoleEntity> roles = this.getRoleEntitiesByRoleNames(updateUserParams.getRoles());
            userEntity.setRoleEntities(roles);
        }

        // Saving the user
        this.userEntityService.save(userEntity);
    }


    //------------------------------------------------------------------------------------------------------------------
    // delete
    //------------------------------------------------------------------------------------------------------------------
    public void delete(Long userId) throws ResourceNotFoundException
    {
        UserEntity userEntity = getUserEntityById(userId);
        if (Boolean.TRUE.equals(userEntity.getExternal()))
        {
            throw new CannotProcessException("External user cannot be deleted!");
        }

        this.userEntityService.deleteById(userId);
    }


    //------------------------------------------------------------------------------------------------------------------
    // getUserEntity
    //------------------------------------------------------------------------------------------------------------------
    @Override
    public UserEntity getUserEntity(String userName, boolean filterInternal) throws ResourceNotFoundException
    {
        Optional<UserEntity> byUserName = this.userEntityService.findByUserName(userName);
        if (byUserName.isPresent() && internalFilter(filterInternal, byUserName.get().getExternal()))
        {
            return byUserName.get();
        }
        else
        {
            throw new ResourceNotFoundException(String.format("User '%s' not found", userName));
        }
    }


    private static boolean internalFilter(boolean filterInternal, boolean entityIsExternal)
    {
        if (!filterInternal)
        {
            return true;
        }
        return !entityIsExternal;
    }


    //------------------------------------------------------------------------------------------------------------------
    // updateLoginTime
    //------------------------------------------------------------------------------------------------------------------
    @Override
    public void updateLoginTime(Long userId)
    {
        this.userEntityService.updateLastLoginTime(userId);
    }


    //------------------------------------------------------------------------------------------------------------------
    // addRole
    //------------------------------------------------------------------------------------------------------------------
    @Override
    public void addRole(Long userId, RoleSet roleSet) throws ResourceNotFoundException
    {
        UserEntity userEntity = getUserEntityById(userId);
        if (Boolean.TRUE.equals(userEntity.getExternal()))
        {
            throw new CannotProcessException(EXTERNAL_USER_CANNOT_BE_UPDATED);
        }

        // Roles
        if (roleSet.getRoles() != null)
        {
            Set<RoleEntity> roleEntitiesToSet = this.getRoleEntitiesByRoleNames(roleSet.getRoles());
            userEntity.getRoleEntities().addAll(roleEntitiesToSet);

            this.userEntityService.save(userEntity);
        }
    }


    private Set<RoleEntity> getRoleEntitiesByRoleNames(Set<String> roleNames)
    {
        Set<RoleEntity> roleEntities = this.roleEntityService.findByRoleIn(roleNames);
        // Check each role found
        if (roleEntities.size() != roleNames.size())
        {
            String invalidRoles = roleNames.stream().filter(i -> !resultSetContainsRole(roleEntities, i)).collect(Collectors.joining(", "));
            log.warn("Roles ignored: {}", invalidRoles);
            //throw new InvalidInputException(String.format("Invalid roles specified: '%s'", invalidRoles));
        }

        return roleEntities;
    }


    //------------------------------------------------------------------------------------------------------------------
    // deleteRole
    //------------------------------------------------------------------------------------------------------------------
    @Override
    public void deleteRole(Long userId, RoleSet roleSet) throws ResourceNotFoundException
    {
        UserEntity userEntity = getUserEntityById(userId);
        if (Boolean.TRUE.equals(userEntity.getExternal()))
        {
            throw new CannotProcessException(EXTERNAL_USER_CANNOT_BE_UPDATED);
        }

        // Roles
        if (roleSet.getRoles() != null)
        {
            // checking if input role names are valid
            this.getRoleEntitiesByRoleNames(roleSet.getRoles());

            Set<RoleEntity> roleEntities = userEntity.getRoleEntities();
            roleEntities.removeIf(roleEntity -> roleSet.getRoles().contains(roleEntity.getRole()));

            this.userEntityService.save(userEntity);
        }
    }
}
