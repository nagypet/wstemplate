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
import hu.perit.template.authservice.api.TemplateAuthServiceApi;
import hu.perit.template.authservice.model.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Peter Nagy
 */

@Tag(name = "user-api-controller", description = "User management")
public interface TemplateAuthServiceControllerApi extends TemplateAuthServiceApi
{

    String BASE_URL_USERS = "/api/users";
    String PATH_ROLES = "/roles";

    //------------------------------------------------------------------------------------------------------------------
    // getAllUsers
    //------------------------------------------------------------------------------------------------------------------
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
    @Override
    List<UserDTOFiltered> getAllUsers();


    //------------------------------------------------------------------------------------------------------------------
    // getUserById
    //------------------------------------------------------------------------------------------------------------------
    @GetMapping(BASE_URL_USERS + "/{userId}")
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
    @Override
    UserDTO getUserById(
            @Parameter(name = "User ID", required = true) @PathVariable Long userId
    ) throws ResourceNotFoundException;


    //------------------------------------------------------------------------------------------------------------------
    // createUser
    //------------------------------------------------------------------------------------------------------------------
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
    @Override
    ResponseUri createUser(
            @Parameter(name = "User properties", required = true) @Valid @RequestBody CreateUserParams createUserParams
    );


    //------------------------------------------------------------------------------------------------------------------
    // updateUser
    //------------------------------------------------------------------------------------------------------------------
    @PutMapping(BASE_URL_USERS + "/{userId}")
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
    @Override
    void updateUser(
            @Parameter(name = "User ID", required = true) @PathVariable Long userId,
            @Parameter(name = "User properties", required = true) @Valid @RequestBody UpdateUserParams updateUserParams
    ) throws ResourceNotFoundException;


    //------------------------------------------------------------------------------------------------------------------
    // deleteUser
    //------------------------------------------------------------------------------------------------------------------
    @DeleteMapping(BASE_URL_USERS + "/{userId}")
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
    @Override
    void deleteUser(
            @Parameter(name = "User ID", required = true) @PathVariable Long userId
    ) throws ResourceNotFoundException;


    //------------------------------------------------------------------------------------------------------------------
    // addRole
    //------------------------------------------------------------------------------------------------------------------
    @PutMapping(BASE_URL_USERS + "/{userId}" + PATH_ROLES)
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
    @Override
    void addRole(
            @Parameter(name = "User ID", required = true) @PathVariable Long userId,
            @Parameter(name = "Set of roles", required = true) @Valid @RequestBody RoleSet roleSet
    ) throws ResourceNotFoundException;


    //------------------------------------------------------------------------------------------------------------------
    // deleteRole
    //------------------------------------------------------------------------------------------------------------------
    @DeleteMapping(BASE_URL_USERS + "/{userId}" + PATH_ROLES)
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
    @Override
    void deleteRole(
            @Parameter(name = "User ID", required = true) @PathVariable Long userId,
            @Parameter(name = "Set of roles", required = true) @Valid @RequestBody RoleSet roleSet) throws ResourceNotFoundException;
}
