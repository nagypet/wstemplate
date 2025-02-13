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

package hu.perit.template.authservice.api;

import hu.perit.spvitamin.spring.exception.ResourceNotFoundException;
import hu.perit.template.authservice.model.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Peter Nagy
 */

public interface TemplateAuthServiceClient extends TemplateAuthServiceApi
{

    String BASE_URL_USERS = "/api/users";
    String PATH_ROLES = "/roles";


    //------------------------------------------------------------------------------------------------------------------
    // getAllUsers
    //------------------------------------------------------------------------------------------------------------------
    @GetMapping(BASE_URL_USERS)
    @Override
    List<UserDTOFiltered> getAllUsers();


    //------------------------------------------------------------------------------------------------------------------
    // getUserById
    //------------------------------------------------------------------------------------------------------------------
    @GetMapping(BASE_URL_USERS + "/{userId}")
    @Override
    UserDTO getUserById(
            @PathVariable Long userId
    ) throws ResourceNotFoundException;


    //------------------------------------------------------------------------------------------------------------------
    // createUser
    //------------------------------------------------------------------------------------------------------------------
    @PostMapping(BASE_URL_USERS)
    @Override
    ResponseUri createUser(
            @RequestBody CreateUserParams createUserParams
    );


    //------------------------------------------------------------------------------------------------------------------
    // updateUser
    //------------------------------------------------------------------------------------------------------------------
    @PutMapping(BASE_URL_USERS + "/{userId}")
    @Override
    void updateUser(
            @PathVariable Long userId,
            @RequestBody UpdateUserParams updateUserParams
    ) throws ResourceNotFoundException;


    //------------------------------------------------------------------------------------------------------------------
    // deleteUser
    //------------------------------------------------------------------------------------------------------------------
    @DeleteMapping(BASE_URL_USERS + "/{userId}")
    @Override
    void deleteUser(
            @PathVariable Long userId
    ) throws ResourceNotFoundException;


    //------------------------------------------------------------------------------------------------------------------
    // addRole
    //------------------------------------------------------------------------------------------------------------------
    @PutMapping(BASE_URL_USERS + "/{userId}" + PATH_ROLES)
    @Override
    void addRole(
            @PathVariable Long userId,
            @RequestBody RoleSet roleSet
    ) throws ResourceNotFoundException;


    //------------------------------------------------------------------------------------------------------------------
    // deleteRole
    //------------------------------------------------------------------------------------------------------------------
    @DeleteMapping(BASE_URL_USERS + "/{userId}" + PATH_ROLES)
    @Override
    void deleteRole(
            @PathVariable Long userId,
            @RequestBody RoleSet roleSet
    ) throws ResourceNotFoundException;
}
