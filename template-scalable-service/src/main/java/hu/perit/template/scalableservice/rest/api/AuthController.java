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

package hu.perit.template.scalableservice.rest.api;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RestController;

import hu.perit.spvitamin.core.exception.UnexpectedConditionException;
import hu.perit.spvitamin.core.took.Took;
import hu.perit.spvitamin.spring.auth.AuthorizationToken;
import hu.perit.spvitamin.spring.auth.SecurityContextUtil;
import hu.perit.spvitamin.spring.logging.AbstractInterfaceLogger;
import hu.perit.spvitamin.spring.rest.api.AuthApi;
import hu.perit.spvitamin.spring.security.AuthenticatedUser;
import hu.perit.spvitamin.spring.security.auth.AuthorizationService;
import hu.perit.template.scalableservice.config.Constants;
import lombok.extern.log4j.Log4j;

/**
 * @author Peter Nagy
 */

@RestController
@Log4j
public class AuthController extends AbstractInterfaceLogger implements AuthApi {

    private final AuthorizationService authorizationService;

    public AuthController(AuthorizationService authorizationService, HttpServletRequest httpRequest) {
        super(httpRequest);
        this.authorizationService = authorizationService;
    }


    @Override
    public AuthorizationToken authenticateUsingGET(String processID) {
        AuthenticatedUser authenticatedUser = this.authorizationService.getAuthenticatedUser();
        this.traceIn(processID, authenticatedUser.getUsername(), this.getMyMethodName(), 1);

        try (Took took = new Took(processID)) {
            if (SecurityContextUtil.getToken() instanceof AuthorizationToken) {
                AuthorizationToken token = (AuthorizationToken) SecurityContextUtil.getToken();
                this.traceOut(processID, authenticatedUser.getUsername(), this.getMyMethodName(), 1);
                return token;
            }
            else {
                throw new UnexpectedConditionException("Token is not instance of AuthorizationToken!");
            }
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
