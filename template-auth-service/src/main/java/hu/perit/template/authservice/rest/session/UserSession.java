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

package hu.perit.template.authservice.rest.session;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import hu.perit.spvitamin.spring.exception.ResourceNotFoundException;
import hu.perit.template.authservice.rest.api.UserApi;
import hu.perit.template.authservice.rest.model.CreateUserParams;
import hu.perit.template.authservice.rest.model.ResponseUri;
import hu.perit.template.authservice.rest.model.RoleSet;
import hu.perit.template.authservice.rest.model.UpdateUserParams;
import hu.perit.template.authservice.rest.model.UserDTO;
import hu.perit.template.authservice.rest.model.UserDTOFiltered;
import hu.perit.template.authservice.services.UserService;

public class UserSession implements UserApi {

    private final UserService userService;

    public UserSession(UserService userService) {
        this.userService = userService;
    }

    /*
     * ============== getAllUsers ======================================================================================
     */
    @Override
    public List<UserDTOFiltered> getAllUsersUsingGET(String processID) {
        return this.userService.getAll();
    }

    /*
     * ============== getUserById ======================================================================================
     */
    @Override
    public UserDTO getUserByIdUsingGET(String processID, Long id) throws ResourceNotFoundException {
        return this.userService.getUserDTOById(id);
    }

    /*
     * ============== createUser =======================================================================================
     */
    @Override
    public ResponseUri createUserUsingPOST(String processID, @Valid CreateUserParams createUserParams) {
        long newUserId = this.userService.create(createUserParams);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newUserId).toUri();

        return new ResponseUri().location(location.toString());
    }

    /*
     * ============== updateUser =======================================================================================
     */
    @Override
    public void updateUserUsingPUT(String processID, Long id, @Valid UpdateUserParams updateUserParams) throws ResourceNotFoundException {
        this.userService.update(id, updateUserParams);
    }

    /*
     * ============== deleteUser =======================================================================================
     */
    @Override
    public void deleteUserUsingDELETE(String processID, Long id) throws ResourceNotFoundException {
        this.userService.delete(id);
    }

    /*
     * ============== addRole ==========================================================================================
     */
    @Override
    public void addRoleUsingPOST(String processID, Long id, @Valid RoleSet roleSet) throws ResourceNotFoundException {
        this.userService.addRole(id, roleSet);
    }

    /*
     * ============== deleteRole =======================================================================================
     */
    @Override
    public void deleteRoleUsingDELETE(String processID, Long id, @Valid RoleSet roleSet) throws ResourceNotFoundException {
        this.userService.deleteRole(id, roleSet);
    }
}
