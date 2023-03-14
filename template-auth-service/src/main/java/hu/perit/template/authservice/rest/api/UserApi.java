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

package hu.perit.template.authservice.rest.api;

import hu.perit.spvitamin.spring.exception.ResourceNotFoundException;
import hu.perit.spvitamin.spring.exceptionhandler.RestExceptionResponse;
import hu.perit.spvitamin.spring.logging.EventLogId;
import hu.perit.template.authservice.rest.model.CreateUserParams;
import hu.perit.template.authservice.rest.model.ResponseUri;
import hu.perit.template.authservice.rest.model.RoleSet;
import hu.perit.template.authservice.rest.model.UpdateUserParams;
import hu.perit.template.authservice.rest.model.UserDTO;
import hu.perit.template.authservice.rest.model.UserDTOFiltered;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.Valid;
import java.util.List;

/**
 * @author Peter Nagy
 */

@Tag(name = "user-api-controller", description = "User management")
public interface UserApi
{

    String BASE_URL_USERS = "/api/users";
    String PATH_ROLES = "/roles";

    /*
     * ============== getAllUsers ======================================================================================
     */
    @GetMapping(BASE_URL_USERS)
    @Secured("USER_GET_ALL_USERS")
    @Operation(summary = "getAllUsers() - Retrieves all users",
            security = {@SecurityRequirement(name = "bearer")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content(schema = @Schema(implementation = RestExceptionResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = RestExceptionResponse.class)))
            }
    )
    @ResponseStatus(value = HttpStatus.OK)
    @EventLogId(eventId = 1)
    List<UserDTOFiltered> getAllUsersUsingGET(@RequestHeader(value = "processID", required = false) String processID);


    /*
     * ============== getUserById ======================================================================================
     */
    @GetMapping(BASE_URL_USERS + "/{id}")
    @Secured("USER_GET_USER")
    @Operation(summary = "getUserById() - Retrieves a user by ID",
            security = {@SecurityRequirement(name = "bearer")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content(schema = @Schema(implementation = RestExceptionResponse.class))),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = RestExceptionResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = RestExceptionResponse.class))),
            }
    )
    @ResponseStatus(value = HttpStatus.OK)
    @EventLogId(eventId = 2)
    UserDTO getUserByIdUsingGET(
            @Parameter(name = "ProcessID", required = false) @RequestHeader(value = "processID", required = false) String processID,
            @Parameter(name = "User ID", required = true) @PathVariable("id") Long id) throws ResourceNotFoundException;


    /*
     * ============== createUser =======================================================================================
     */
    @PostMapping(BASE_URL_USERS)
    @Secured("USER_CREATE_USER")
    @Operation(summary = "createUser() - creates a new user",
            security = {@SecurityRequirement(name = "bearer")},
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created"),
                    @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = RestExceptionResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content(schema = @Schema(implementation = RestExceptionResponse.class))),
                    @ApiResponse(responseCode = "409", description = "User already exists", content = @Content(schema = @Schema(implementation = RestExceptionResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = RestExceptionResponse.class))),
            }
    )
    @ResponseStatus(value = HttpStatus.CREATED)
    @EventLogId(eventId = 3)
    ResponseUri createUserUsingPOST(
            @Parameter(name = "ProcessID", required = false) @RequestHeader(value = "processID", required = false) String processID,
            @Parameter(name = "User properties", required = true) @Valid @RequestBody CreateUserParams createUserParams);


    /*
     * ============== updateUser =======================================================================================
     */
    @PutMapping(BASE_URL_USERS + "/{id}")
    @Secured("USER_UPDATE_USER")
    @Operation(summary = "updateUser() - Updates a user by ID",
            security = {@SecurityRequirement(name = "bearer")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = RestExceptionResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content(schema = @Schema(implementation = RestExceptionResponse.class))),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = RestExceptionResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = RestExceptionResponse.class)))
            }
    )
    @ResponseStatus(value = HttpStatus.OK)
    @EventLogId(eventId = 4)
    void updateUserUsingPUT(
            @Parameter(name = "ProcessID", required = false) @RequestHeader(value = "processID", required = false) String processID,
            @Parameter(name = "User ID", required = true) @PathVariable("id") Long id,
            @Parameter(name = "User properties", required = true) @Valid @RequestBody UpdateUserParams updateUserParams)
            throws ResourceNotFoundException;


    /*
     * ============== deleteUser =======================================================================================
     */
    @DeleteMapping(BASE_URL_USERS + "/{id}")
    @Secured("USER_DELETE_USER")
    @Operation(summary = "deleteUser() - removes a user by ID",
            security = {@SecurityRequirement(name = "bearer")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = RestExceptionResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content(schema = @Schema(implementation = RestExceptionResponse.class))),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = RestExceptionResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = RestExceptionResponse.class)))
            }
    )
    @ResponseStatus(value = HttpStatus.OK)
    @EventLogId(eventId = 5)
    void deleteUserUsingDELETE(
            @Parameter(name = "ProcessID", required = false) @RequestHeader(value = "processID", required = false) String processID,
            @Parameter(name = "User ID", required = true) @PathVariable("id") Long id) throws ResourceNotFoundException;


    /*
     * ============== addRole ==========================================================================================
     */
    @PutMapping(BASE_URL_USERS + "/{id}" + PATH_ROLES)
    @Secured("USER_ADD_ROLE")
    @Operation(summary = "addRole() - adds a new role to the user",
            security = {@SecurityRequirement(name = "bearer")},
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created"),
                    @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = RestExceptionResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content(schema = @Schema(implementation = RestExceptionResponse.class))),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = RestExceptionResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = RestExceptionResponse.class)))
            }
    )
    @ResponseStatus(value = HttpStatus.CREATED)
    @EventLogId(eventId = 6)
    void addRoleUsingPOST(
            @Parameter(name = "ProcessID", required = false) @RequestHeader(value = "processID", required = false) String processID,
            @Parameter(name = "User ID", required = true) @PathVariable("id") Long id,
            @Parameter(name = "Set of roles", required = true) @Valid @RequestBody RoleSet roleSet) throws ResourceNotFoundException;


    /*
     * ============== deleteRole =======================================================================================
     */
    @DeleteMapping(BASE_URL_USERS + "/{id}" + PATH_ROLES)
    @Secured("USER_DELETE_ROLE")
    @Operation(summary = "deleteRole() - removes a user role",
            security = {@SecurityRequirement(name = "bearer")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = RestExceptionResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content(schema = @Schema(implementation = RestExceptionResponse.class))),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = RestExceptionResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = RestExceptionResponse.class)))
            }
    )
    @ResponseStatus(value = HttpStatus.OK)
    @EventLogId(eventId = 7)
    void deleteRoleUsingDELETE(
            @Parameter(name = "ProcessID", required = false) @RequestHeader(value = "processID", required = false) String processID,
            @Parameter(name = "User ID", required = true) @PathVariable("id") Long id,
            @Parameter(name = "Set of roles", required = true) @Valid @RequestBody RoleSet roleSet) throws ResourceNotFoundException;
}
