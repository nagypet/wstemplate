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

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RestController;

import hu.perit.spvitamin.core.took.Took;
import hu.perit.spvitamin.spring.auth.jwt.JwtTokenProvider;
import hu.perit.spvitamin.spring.auth.jwt.TokenClaims;
import hu.perit.spvitamin.spring.logging.AbstractInterfaceLogger;
import hu.perit.spvitamin.spring.rest.api.AuthApi;
import hu.perit.spvitamin.spring.rest.model.AuthorizationToken;
import hu.perit.spvitamin.spring.security.AuthenticatedUser;
import hu.perit.spvitamin.spring.security.auth.AuthorizationService;
import hu.perit.template.authservice.config.Constants;

/**
 * @author Peter Nagy
 */

@RestController
public class AuthApiController extends AbstractInterfaceLogger implements AuthApi {

    private final JwtTokenProvider tokenProvider;
    private final AuthorizationService authorizationService;

    public AuthApiController(JwtTokenProvider tokenProvider, AuthorizationService authorizationService, HttpServletRequest httpRequest) {
        super(httpRequest);
        this.tokenProvider = tokenProvider;
        this.authorizationService = authorizationService;
    }


    @Override
    public AuthorizationToken authenticateUsingGET(String processID) {
        AuthenticatedUser authenticatedUser = this.authorizationService.getAuthenticatedUser();
        this.traceIn(processID, authenticatedUser.getUsername(), this.getMyMethodName(), 1);

        try (Took took = new Took(processID)) {
            AuthorizationToken token = tokenProvider.generateToken(
                    authenticatedUser.getUsername(),
                    new TokenClaims(authenticatedUser.getUserId(), authenticatedUser.getAuthorities())
            );
            this.traceOut(processID, authenticatedUser.getUsername(), this.getMyMethodName(), 1);
            return token;
        }
        catch (Throwable ex)
        {
            this.traceOut(processID, authenticatedUser.getUsername(), this.getMyMethodName(), 1, ex);
            throw ex;
        }
    }

    @Override
    protected String getSubsystemName() {
        return Constants.SUBSYSTEM_NAME;
    }
}
