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

package hu.perit.template.authservice.rest.client;

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
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author Peter Nagy
 */

public interface TemplateAuthClient
{

    String BASE_URL_AUTHENTICATE = "/api/spvitamin/authenticate";
    String BASE_URL_USERS = "/api/users";
    String PATH_ROLES = "/roles";

    /*
     * ============== authenticate =====================================================================================
     */
    @RequestLine("GET " + BASE_URL_AUTHENTICATE)
    @Headers({
            "Content-Type: application/json",
            "traceId: {traceId}"
    })
    AuthorizationToken authenticate(
            @Param("traceId") String traceId
    );


    /*
     * ============== getAllUsers ======================================================================================
     */
    @RequestLine("GET " + BASE_URL_USERS)
    @Headers({
            "Content-Type: application/json",
            "traceId: {traceId}"
    })
    List<UserDTOFiltered> getAllUsers(
            @Param("traceId") String traceId
    );


    /*
     * ============== getUserById ======================================================================================
     */
    @RequestLine("GET " + BASE_URL_USERS + "/{id}")
    @Headers({
            "Content-Type: application/json",
            "traceId: {traceId}"
    })
    UserDTO getUserById(
            @Param("traceId") String traceId,
            @Param("id") long userId
    );


    /*
     * ============== createUser =======================================================================================
     */
    @RequestLine("POST " + BASE_URL_USERS)
    @Headers({
            "Content-Type: application/json",
            "traceId: {traceId}"
    })
    ResponseUri createUser(
            @Param("traceId") String traceId,
            @RequestBody CreateUserParams createUserParams
    );


    /*
     * ============== updateUser =======================================================================================
     */
    @RequestLine("PUT " + BASE_URL_USERS + "/{id}")
    @Headers({
            "Content-Type: application/json",
            "traceId: {traceId}"
    })
    void updateUser(
            @Param("traceId") String traceId,
            @Param("id") long userId,
            @RequestBody UpdateUserParams updateUserParams
    );


    /*
     * ============== deleteUser =======================================================================================
     */
    @RequestLine("DELETE " + BASE_URL_USERS + "/{id}")
    @Headers({
            "Content-Type: application/json",
            "traceId: {traceId}"
    })
    void deleteUser(
            @Param("traceId") String traceId,
            @Param("id") long userId
    );


    /*
     * ============== addRole ==========================================================================================
     */
    @RequestLine("PUT " + BASE_URL_USERS + "/{id}" + PATH_ROLES)
    @Headers({
            "Content-Type: application/json",
            "traceId: {traceId}"
    })
    void addRole(
            @Param("traceId") String traceId,
            @Param("id") long userId,
            @RequestBody RoleSet roleSet
    );


    /*
     * ============== deleteRole =======================================================================================
     */
    @RequestLine("DELETE " + BASE_URL_USERS + "/{id}" + PATH_ROLES)
    @Headers({
            "Content-Type: application/json",
            "traceId: {traceId}"
    })
    void deleteRole(
            @Param("traceId") String traceId,
            @Param("id") long userId,
            @RequestBody RoleSet roleSet
    );
}
