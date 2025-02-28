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

package hu.perit.template.authservice.auth;

import hu.perit.spvitamin.spring.config.SpringContext;
import hu.perit.spvitamin.spring.security.AuthenticatedUser;
import hu.perit.spvitamin.spring.security.auth.AuthorizationService;
import hu.perit.template.authservice.services.api.UserService;
import hu.perit.template.authservice.services.impl.user.UserServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @author Peter Nagy
 */

@Slf4j
public class PostAuthenticationFilter extends OncePerRequestFilter
{

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException
    {
        log.debug("{} called", this.getClass().getName());

        AuthorizationService authorizationService = SpringContext.getBean(AuthorizationService.class);

        AuthenticatedUser authenticatedUser = authorizationService.getAuthenticatedUser();

        if (!authenticatedUser.isAnonymous() && authenticatedUser.getUserId() < 0)
        {
            UserService userService = SpringContext.getBean(UserServiceImpl.class);
            authenticatedUser.setUserId(userService.createAtLogin(authenticatedUser));

            // store in the security context
            authorizationService.setAuthenticatedUser(authenticatedUser);
        }
        filterChain.doFilter(request, response);
    }
}
