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

package hu.perit.template.authservice.rest.controller;

import hu.perit.spvitamin.spring.exception.ResourceNotFoundException;
import hu.perit.spvitamin.spring.restmethodlogger.LoggedRestMethod;
import hu.perit.template.authservice.config.Constants;
import hu.perit.template.authservice.model.*;
import hu.perit.template.authservice.rest.api.TemplateAuthServiceControllerApi;
import hu.perit.template.authservice.services.impl.user.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * @author Peter Nagy
 */

@RestController
@RequiredArgsConstructor
public class TemplateAuthServiceController implements TemplateAuthServiceControllerApi
{
    private final UserServiceImpl userService;

    /*
     * ============== getAllUsers ======================================================================================
     */
    @Override
    @LoggedRestMethod(eventId = Constants.USER_API_GET_ALL, subsystem = Constants.SUBSYSTEM_NAME)
    public List<UserDTOFiltered> getAllUsers()
    {
        return this.userService.getAll();
    }


    /*
     * ============== getUserById ======================================================================================
     */
    @Override
    @LoggedRestMethod(eventId = Constants.USER_API_GET_BY_ID, subsystem = Constants.SUBSYSTEM_NAME)
    public UserDTO getUserById(Long userId) throws ResourceNotFoundException
    {
        return this.userService.getUserDTOById(userId);
    }


    /*
     * ============== createUser =======================================================================================
     */
    @Override
    @LoggedRestMethod(eventId = Constants.USER_API_CREATE, subsystem = Constants.SUBSYSTEM_NAME)
    public ResponseUri createUser(CreateUserParams createUserParams)
    {
        long newUserId = this.userService.create(createUserParams);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(newUserId).toUri();
        return new ResponseUri().location(location.toString());
    }


    /*
     * ============== updateUser =======================================================================================
     */
    @Override
    @LoggedRestMethod(eventId = Constants.USER_API_UPDATE, subsystem = Constants.SUBSYSTEM_NAME)
    public void updateUser(Long userId, UpdateUserParams updateUserParams) throws ResourceNotFoundException
    {
        this.userService.update(userId, updateUserParams);
    }


    /*
     * ============== deleteUser =======================================================================================
     */
    @Override
    @LoggedRestMethod(eventId = Constants.USER_API_DELETE, subsystem = Constants.SUBSYSTEM_NAME)
    public void deleteUser(Long userId) throws ResourceNotFoundException
    {
        this.userService.delete(userId);
    }


    /*
     * ============== addRole ==========================================================================================
     */
    @Override
    @LoggedRestMethod(eventId = Constants.USER_API_ADD_ROLE, subsystem = Constants.SUBSYSTEM_NAME)
    public void addRole(Long userId, RoleSet roleSet) throws ResourceNotFoundException
    {
        this.userService.addRole(userId, roleSet);
    }


    /*
     * ============== deleteRole =======================================================================================
     */
    @Override
    @LoggedRestMethod(eventId = Constants.USER_API_DELETE_ROLE, subsystem = Constants.SUBSYSTEM_NAME)
    public void deleteRole(Long userId, RoleSet roleSet) throws ResourceNotFoundException
    {
        this.userService.deleteRole(userId, roleSet);
    }
}
