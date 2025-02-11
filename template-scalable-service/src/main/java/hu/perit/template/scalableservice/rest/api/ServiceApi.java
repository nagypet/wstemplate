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

package hu.perit.template.scalableservice.rest.api;

import feign.Param;
import hu.perit.spvitamin.spring.exception.ResourceNotFoundException;
import hu.perit.spvitamin.spring.exceptionhandler.RestExceptionResponse;
import hu.perit.spvitamin.spring.logging.EventLogId;
import hu.perit.template.authservice.rest.model.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Peter Nagy
 */

@Tag(name = "service-api-controller", description = "Some service")
public interface ServiceApi
{

    String BASE_URL_SERVICE = "/api/service";

    //------------------------------------------------------------------------------------------------------------------
    // makeSomeLongCalculation()
    //------------------------------------------------------------------------------------------------------------------
    @GetMapping(BASE_URL_SERVICE)
    @Operation(summary = "makeSomeLongCalculation() - Makes some time consuming calculation",
            security = {@SecurityRequirement(name = "bearer")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content(schema = @Schema(implementation = RestExceptionResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = RestExceptionResponse.class)))
            }
    )
    Integer makeSomeLongCalculation() throws InterruptedException;


    //------------------------------------------------------------------------------------------------------------------
    // getUserById
    //------------------------------------------------------------------------------------------------------------------
    @GetMapping(BASE_URL_SERVICE + "/users/{userId}")
    @Operation(summary = "getUserById() - Returns a user by id",
            security = {@SecurityRequirement(name = "bearer")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content(schema = @Schema(implementation = RestExceptionResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = RestExceptionResponse.class)))
            }
    )
    UserDTO getUserById(
            @PathVariable("userId") long userId
    ) throws ResourceNotFoundException;
}
