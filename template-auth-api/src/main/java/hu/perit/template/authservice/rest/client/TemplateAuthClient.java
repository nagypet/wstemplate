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

package hu.perit.template.authservice.rest.client;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import hu.perit.spvitamin.spring.auth.AuthorizationToken;
import hu.perit.template.authservice.rest.model.CreateUserParams;
import hu.perit.template.authservice.rest.model.ResponseUri;
import hu.perit.template.authservice.rest.model.RoleSet;
import hu.perit.template.authservice.rest.model.UpdateUserParams;
import hu.perit.template.authservice.rest.model.UserDTO;
import hu.perit.template.authservice.rest.model.UserDTOFiltered;

/**
 * @author Peter Nagy
 */

public interface TemplateAuthClient {

    String BASE_URL_AUTHENTICATE = "/authenticate";
    String BASE_URL_USERS = "/users";
    String PATH_ROLES = "/roles";

    /*
     * ============== authenticate =====================================================================================
     */
    @RequestLine("GET " + BASE_URL_AUTHENTICATE)
    @Headers({
            "Content-Type: application/json",
            "processID: {processID}"
    })
    AuthorizationToken authenticate(
            @Param("processID") String processID
    );


    /*
     * ============== getAllUsers ======================================================================================
     */
    @RequestLine("GET " + BASE_URL_USERS)
    @Headers({
            "Content-Type: application/json",
            "processID: {processID}"
    })
    List<UserDTOFiltered> getAllUsers(
            @Param("processID") String processID
    );


    /*
     * ============== getUserById ======================================================================================
     */
    @RequestLine("GET " + BASE_URL_USERS + "/{id}")
    @Headers({
            "Content-Type: application/json",
            "processID: {processID}"
    })
    UserDTO getUserById(
            @Param("processID") String processID,
            @Param("id") long userId
    );


    /*
     * ============== createUser =======================================================================================
     */
    @RequestLine("POST " + BASE_URL_USERS)
    @Headers({
            "Content-Type: application/json",
            "processID: {processID}"
    })
    ResponseUri createUser(
            @Param("processID") String processID,
            @RequestBody CreateUserParams createUserParams
    );


    /*
     * ============== updateUser =======================================================================================
     */
    @RequestLine("PUT " + BASE_URL_USERS + "/{id}")
    @Headers({
            "Content-Type: application/json",
            "processID: {processID}"
    })
    void updateUser(
            @Param("processID") String processID,
            @Param("id") long userId,
            @RequestBody UpdateUserParams updateUserParams
    );


    /*
     * ============== deleteUser =======================================================================================
     */
    @RequestLine("DELETE " + BASE_URL_USERS + "/{id}")
    @Headers({
            "Content-Type: application/json",
            "processID: {processID}"
    })
    void deleteUser(
            @Param("processID") String processID,
            @Param("id") long userId
    );


    /*
     * ============== addRole ==========================================================================================
     */
    @RequestLine("PUT " + BASE_URL_USERS + "/{id}" + PATH_ROLES)
    @Headers({
            "Content-Type: application/json",
            "processID: {processID}"
    })
    void addRole(
            @Param("processID") String processID,
            @Param("id") long userId,
            @RequestBody RoleSet roleSet
    );


    /*
     * ============== deleteRole =======================================================================================
     */
    @RequestLine("DELETE " + BASE_URL_USERS + "/{id}" + PATH_ROLES)
    @Headers({
            "Content-Type: application/json",
            "processID: {processID}"
    })
    void deleteRole(
            @Param("processID") String processID,
            @Param("id") long userId,
            @RequestBody RoleSet roleSet
    );
}
