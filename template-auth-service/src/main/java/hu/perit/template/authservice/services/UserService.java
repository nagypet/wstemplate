/*
 * Copyright 2020-2020 the original author or authors.
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

package hu.perit.template.authservice.services;

import hu.perit.spvitamin.core.exception.ExceptionWrapper;
import hu.perit.spvitamin.spring.exception.CannotProcessException;
import hu.perit.spvitamin.spring.exception.InvalidInputException;
import hu.perit.spvitamin.spring.exception.ResourceAlreadyExistsException;
import hu.perit.spvitamin.spring.exception.ResourceNotFoundException;
import hu.perit.spvitamin.spring.security.AuthenticatedUser;
import hu.perit.template.authservice.config.Constants;
import hu.perit.template.authservice.db.demodb.repo.RoleRepo;
import hu.perit.template.authservice.db.demodb.repo.UserRepo;
import hu.perit.template.authservice.db.demodb.table.RoleEntity;
import hu.perit.template.authservice.db.demodb.table.UserEntity;
import hu.perit.template.authservice.rest.model.CreateUserParams;
import hu.perit.template.authservice.rest.model.RoleSet;
import hu.perit.template.authservice.rest.model.UpdateUserParams;
import hu.perit.template.authservice.rest.model.UserDTO;
import hu.perit.template.authservice.rest.model.UserDTOFiltered;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.LockModeType;
import java.time.LocalDateTime;
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
public class UserService
{
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;

    /*
     * ============== getAll ===========================================================================================
     */
    public List<UserDTOFiltered> getAll()
    {
        List<UserEntity> all = this.userRepo.findAll();

        ModelMapper modelMapper = new ModelMapper();
        return all.stream().map(i -> modelMapper.map(i, UserDTOFiltered.class)).collect(Collectors.toList());
    }


    /*
     * ============== getUserDTOById ===================================================================================
     */
    public UserDTO getUserDTOById(long userId) throws ResourceNotFoundException
    {
        Optional<UserEntity> byId = this.userRepo.findById(userId);
        if (byId.isPresent())
        {
            ModelMapper modelMapper = new ModelMapper();
            return modelMapper.map(byId.get(), UserDTO.class);
        }
        else
        {
            throw new ResourceNotFoundException(String.format("No user found by userId: %d", userId));
        }
    }


    /*
     * ============== create ===========================================================================================
     */
    public long create(CreateUserParams createUserParams)
    {
        return this.create(createUserParams, false);
    }


    public long create(CreateUserParams createUserParams, Boolean external)
    {
        try
        {
            ModelMapper modelMapper = new ModelMapper();
            UserEntity userEntity = modelMapper.map(createUserParams, UserEntity.class);

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
            UserEntity newUser = this.userRepo.save(userEntity);

            return newUser.getUserId();
        }
        catch (RuntimeException ex)
        {
            ExceptionWrapper exception = ExceptionWrapper.of(ex);
            if (exception.causedBy(ConstraintViolationException.class))
            {
                Optional<Throwable> fromCauseChain = exception.getFromCauseChain(ConstraintViolationException.class);
                if (fromCauseChain.isPresent())
                {
                    if (((ConstraintViolationException) fromCauseChain.get()).getConstraintName().equalsIgnoreCase(
                            Constants.INDEXNAME_USERNAME))
                    {
                        throw new ResourceAlreadyExistsException(
                                String.format("A user with username '%s' already exists!", createUserParams.getUserName()), ex);
                    }
                }
            }
            throw ex;
        }
    }


    private boolean resultSetContainsRole(Set<RoleEntity> roles, String role)
    {
        return roles.stream().anyMatch(i -> i.getRole().equalsIgnoreCase(role));
    }


    /*
     * ============== createAtLogin ====================================================================================
     */
    public long createAtLogin(AuthenticatedUser authenticatedUser)
    {
        if (authenticatedUser.getUserId() >= 0)
        {
            return authenticatedUser.getUserId();
        }
        else
        {
            long userId = this.getUserIdByName(authenticatedUser.getUsername());
            if (userId >= 0)
            {
                return userId;
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


    /*
     * ============== update ===========================================================================================
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public void update(long userId, UpdateUserParams updateUserParams) throws ResourceNotFoundException
    {
        Optional<UserEntity> byId = this.userRepo.findById(userId);
        if (byId.isPresent())
        {
            ModelMapper modelMapper = new ModelMapper();
            modelMapper.getConfiguration().setSkipNullEnabled(true);

            UserEntity userEntity = byId.get();
            if (userEntity.getExternal())
            {
                throw new CannotProcessException("External user cannot be updated!");
            }

            modelMapper.map(updateUserParams, userEntity);

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
            this.userRepo.save(userEntity);
        }
        else
        {
            throw new ResourceNotFoundException(String.format("No user found by userId: %d", userId));
        }
    }


    /*
     * ============== delete ===========================================================================================
     */
    public void delete(long userId) throws ResourceNotFoundException
    {
        Optional<UserEntity> byId = this.userRepo.findById(userId);
        if (byId.isPresent())
        {
            UserEntity userEntity = byId.get();
            if (Boolean.TRUE.equals(userEntity.getExternal()))
            {
                throw new CannotProcessException("External user cannot be deleted!");
            }

            this.userRepo.deleteById(userId);
        }
        else
        {
            throw new ResourceNotFoundException(String.format("No user found by userId: %d", userId));
        }
    }


    /*
     * ============== getUserEntity ====================================================================================
     */
    public UserEntity getUserEntity(String userName, boolean filterInternal) throws ResourceNotFoundException
    {
        Optional<UserEntity> byUserName = this.userRepo.findByUserName(userName);
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

    /*
     * ============== getUserIdByName ==================================================================================
     */
    public long getUserIdByName(String userName)
    {
        Optional<UserEntity> byUserName = this.userRepo.findByUserName(userName);
        if (byUserName.isPresent())
        {
            return byUserName.get().getUserId();
        }
        else
        {
            return -1;
        }
    }


    /*
     * ============== updateLoginTime ==================================================================================
     */
    public void updateLoginTime(long userId)
    {
        this.userRepo.updateLastLoginTime(userId, LocalDateTime.now());
    }


    /*
     * ============== addRole ==========================================================================================
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public void addRole(Long userId, RoleSet roleSet) throws ResourceNotFoundException
    {
        Optional<UserEntity> byId = this.userRepo.findById(userId);
        if (byId.isPresent())
        {
            UserEntity userEntity = byId.get();
            if (Boolean.TRUE.equals(userEntity.getExternal()))
            {
                throw new CannotProcessException("External user cannot be updated!");
            }

            // Roles
            if (roleSet.getRoles() != null)
            {
                Set<RoleEntity> roleEntitiesToSet = this.getRoleEntitiesByRoleNames(roleSet.getRoles());
                userEntity.getRoleEntities().addAll(roleEntitiesToSet);

                this.userRepo.save(userEntity);
            }
        }
        else
        {
            throw new ResourceNotFoundException(String.format("No user found by userId: %d", userId));
        }
    }


    private Set<RoleEntity> getRoleEntitiesByRoleNames(Set<String> roleNames)
    {
        Set<RoleEntity> roleEntities = this.roleRepo.findByRoleIn(roleNames);
        // Check each role found
        if (roleEntities.size() != roleNames.size())
        {
            String invalidRoles = roleNames.stream().filter(i -> !resultSetContainsRole(roleEntities, i)).collect(Collectors.joining(", "));
            throw new InvalidInputException(String.format("Invalid roles specified: '%s'", invalidRoles));
        }

        return roleEntities;
    }


    /*
     * ============== deleteRole =======================================================================================
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public void deleteRole(Long userId, RoleSet roleSet) throws ResourceNotFoundException
    {
        Optional<UserEntity> byId = this.userRepo.findById(userId);
        if (byId.isPresent())
        {
            UserEntity userEntity = byId.get();
            if (Boolean.TRUE.equals(userEntity.getExternal()))
            {
                throw new CannotProcessException("External user cannot be updated!");
            }

            // Roles
            if (roleSet.getRoles() != null)
            {
                // checking if input role names are valid
                this.getRoleEntitiesByRoleNames(roleSet.getRoles());

                Set<RoleEntity> roleEntities = userEntity.getRoleEntities();
                roleEntities.removeIf(roleEntity -> roleSet.getRoles().contains(roleEntity.getRole()));

                this.userRepo.save(userEntity);
            }
        }
        else
        {
            throw new ResourceNotFoundException(String.format("No user found by userId: %d", userId));
        }
    }
}
