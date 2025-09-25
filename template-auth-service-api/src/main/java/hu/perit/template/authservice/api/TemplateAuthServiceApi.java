/*
 * Copyright 2020-2025 the original author or authors.
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

import java.util.List;

public interface TemplateAuthServiceApi
{
    List<UserDTOFiltered> getAllUsers();

    UserDTO getUserById(Long userId) throws ResourceNotFoundException;

    ResponseUri createUser(CreateUserParams createUserParams);

    void updateUser(Long userId, UpdateUserParams updateUserParams) throws ResourceNotFoundException;

    void deleteUser(Long userId) throws ResourceNotFoundException;

    void addRole(Long userId, RoleSet roleSet) throws ResourceNotFoundException;

    void deleteRole(Long userId, RoleSet roleSet) throws ResourceNotFoundException;
}
