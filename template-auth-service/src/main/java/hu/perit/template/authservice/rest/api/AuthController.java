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

import hu.perit.spvitamin.spring.auth.AuthorizationToken;
import hu.perit.spvitamin.spring.rest.api.AuthApi;
import hu.perit.spvitamin.spring.restmethodlogger.LoggedRestMethod;
import hu.perit.spvitamin.spring.security.AuthenticatedUser;
import hu.perit.spvitamin.spring.security.auth.AuthorizationService;
import hu.perit.spvitamin.spring.security.auth.jwt.JwtTokenProvider;
import hu.perit.spvitamin.spring.security.auth.jwt.TokenClaims;
import hu.perit.template.authservice.config.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Peter Nagy
 */

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final JwtTokenProvider tokenProvider;
    private final AuthorizationService authorizationService;


    @Override
    @LoggedRestMethod(eventId = 1, subsystem = Constants.SUBSYSTEM_NAME)
    public AuthorizationToken authenticateUsingGET(String traceId) {
        AuthenticatedUser authenticatedUser = this.authorizationService.getAuthenticatedUser();

        return tokenProvider.generateToken(authenticatedUser.getUsername(),
                new TokenClaims(authenticatedUser.getUserId(), authenticatedUser.getAuthorities(), authenticatedUser.getLdapUrl()));
    }
}
