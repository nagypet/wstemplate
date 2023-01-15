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

import hu.perit.spvitamin.core.exception.UnexpectedConditionException;
import hu.perit.spvitamin.spring.auth.AuthorizationToken;
import hu.perit.spvitamin.spring.rest.api.AuthApi;
import hu.perit.spvitamin.spring.restmethodlogger.LoggedRestMethod;
import hu.perit.spvitamin.spring.security.SecurityContextUtil;
import hu.perit.template.scalableservice.config.Constants;
import lombok.extern.log4j.Log4j;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Peter Nagy
 */

@RestController
@Log4j
public class AuthController implements AuthApi
{
    @Override
    @LoggedRestMethod(eventId = 1, subsystem = Constants.SUBSYSTEM_NAME)
    public AuthorizationToken authenticateUsingGET(String processID)
    {
        if (SecurityContextUtil.getToken() instanceof AuthorizationToken)
        {
            AuthorizationToken token = (AuthorizationToken) SecurityContextUtil.getToken();
            return token;
        }
        else
        {
            throw new UnexpectedConditionException("Token is not instance of AuthorizationToken!");
        }
    }
}
