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

package hu.perit.template.authservice.rest.api;

import hu.perit.spvitamin.spring.logging.EventLogId;
import hu.perit.spvitamin.spring.rest.model.*;
import hu.perit.template.authservice.rest.model.*;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author Peter Nagy
 */

@Api(value = "user-api-controller", description = "User management", tags = "user-api-controller")
public interface UserApi {

    String BASE_URL_USERS = "/users";
    String PATH_ROLES = "/roles";

    /*
     * ============== getAllUsers ======================================================================================
     */
    @GetMapping(BASE_URL_USERS)
    @ApiOperation(value = "getAllUsers() - Retrieves all users",
            authorizations = {@Authorization(value = "basicAuth")}
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Invalid credentials"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @ResponseStatus(value = HttpStatus.OK)
    @EventLogId(eventId = 1)
    List<UserDTOFiltered> getAllUsersUsingGET(
            @ApiParam(value = "ProcessID", required=false) @RequestHeader(value="processID", required=false) String processID
    );


    /*
     * ============== getUserById ======================================================================================
     */
    @GetMapping(BASE_URL_USERS + "/{id}")
    @ApiOperation(value = "getUserById() - Retrieves a user by ID",
            authorizations = {@Authorization(value = "basicAuth")}
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Invalid credentials"),
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @ResponseStatus(value = HttpStatus.OK)
    @EventLogId(eventId = 2)
    UserDTO getUserByIdUsingGET(
            @ApiParam(value = "ProcessID", required=false) @RequestHeader(value="processID", required=false) String processID,
            @ApiParam(value = "User ID",required=true) @PathVariable("id") Long id
    );


    /*
     * ============== createUser =======================================================================================
     */
    @PostMapping(BASE_URL_USERS)
    @ApiOperation(value = "createUser() - creates a new user",
            authorizations = {@Authorization(value = "basicAuth")}
    )
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 401, message = "Invalid credentials"),
            @ApiResponse(code = 409, message = "User already exists"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @ResponseStatus(value = HttpStatus.CREATED)
    @EventLogId(eventId = 3)
    ResponseUri createUserUsingPOST(
            @ApiParam(value = "ProcessID", required=false) @RequestHeader(value="processID", required=false) String processID,
            @ApiParam(value = "User properties", required = true) @Valid @RequestBody CreateUserParams createUserParams
    );


    /*
     * ============== updateUser =======================================================================================
     */
    @PutMapping(BASE_URL_USERS + "/{id}")
    @ApiOperation(value = "updateUser() - Updates a user by ID",
            authorizations = {@Authorization(value = "basicAuth")}
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Invalid credentials"),
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @ResponseStatus(value = HttpStatus.OK)
    @EventLogId(eventId = 4)
    void updateUserUsingPUT(
            @ApiParam(value = "ProcessID", required=false) @RequestHeader(value="processID", required=false) String processID,
            @ApiParam(value = "User ID",required=true) @PathVariable("id") Long id,
            @ApiParam(value = "User properties", required = true) @Valid @RequestBody UpdateUserParams updateUserParams
    );


    /*
     * ============== deleteUser =======================================================================================
     */
    @DeleteMapping(BASE_URL_USERS + "/{id}")
    @ApiOperation(value = "deleteUser() - removes a user by ID",
            authorizations = {@Authorization(value = "basicAuth")}
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 401, message = "Invalid credentials"),
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @ResponseStatus(value = HttpStatus.OK)
    @EventLogId(eventId = 5)
    void deleteUserUsingDELETE(
            @ApiParam(value = "ProcessID", required=false) @RequestHeader(value="processID", required=false) String processID,
            @ApiParam(value = "User ID",required=true) @PathVariable("id") Long id
    );


    /*
     * ============== addRole ==========================================================================================
     */
    @PutMapping(BASE_URL_USERS + "/{id}" + PATH_ROLES)
    @ApiOperation(value = "addRole() - adds a new role to the user",
            authorizations = {@Authorization(value = "basicAuth")}
    )
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 401, message = "Invalid credentials"),
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @ResponseStatus(value = HttpStatus.OK)
    @EventLogId(eventId = 6)
    void addRoleUsingPOST(
            @ApiParam(value = "ProcessID", required=false) @RequestHeader(value="processID", required=false) String processID,
            @ApiParam(value = "User ID",required=true) @PathVariable("id") Long id,
            @ApiParam(value = "Set of roles", required = true) @Valid @RequestBody RoleSet roleSet
    );


    /*
     * ============== deleteRole =======================================================================================
     */
    @DeleteMapping(BASE_URL_USERS + "/{id}" + PATH_ROLES)
    @ApiOperation(value = "deleteRole() - removes a user role",
            authorizations = {@Authorization(value = "basicAuth")}
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 401, message = "Invalid credentials"),
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @ResponseStatus(value = HttpStatus.OK)
    @EventLogId(eventId = 7)
    void deleteRoleUsingDELETE(
            @ApiParam(value = "ProcessID", required=false) @RequestHeader(value="processID", required=false) String processID,
            @ApiParam(value = "User ID",required=true) @PathVariable("id") Long id,
            @ApiParam(value = "Set of roles", required = true) @Valid @RequestBody RoleSet roleSet
    );

}
