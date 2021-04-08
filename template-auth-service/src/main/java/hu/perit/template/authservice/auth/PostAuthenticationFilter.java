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

package hu.perit.template.authservice.auth;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import hu.perit.spvitamin.spring.auth.filter.FilterAuthenticationException;
import hu.perit.spvitamin.spring.config.SpringContext;
import hu.perit.spvitamin.spring.security.AuthenticatedUser;
import hu.perit.spvitamin.spring.security.auth.AuthorizationService;
import hu.perit.spvitamin.spring.security.ldap.AdGroupRoleMapper;
import hu.perit.template.authservice.services.UserService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Peter Nagy
 */

@Slf4j
public class PostAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            AuthorizationService authorizationService = SpringContext.getBean(AuthorizationService.class);

            AuthenticatedUser authenticatedUser = authorizationService.getAuthenticatedUser();
            log.debug(String.format("Authentication succeeded for user: '%s'", authenticatedUser.toString()));

            if (authenticatedUser.getUserId() < 0) {
                // authenticated from external source
                AdGroupRoleMapper roleMapper = SpringContext.getBean(AdGroupRoleMapper.class);
                AuthenticatedUser mappedUser = roleMapper.mapGrantedAuthorities(authenticatedUser);

                UserService userService = SpringContext.getBean(UserService.class);
                mappedUser.setUserId(userService.createAtLogin(mappedUser));

                // store in the security context
                authorizationService.setAuthenticatedUser(mappedUser);
            }
            filterChain.doFilter(request, response);
        }
        catch (AuthenticationException ex) {
            SecurityContextHolder.clearContext();
            HandlerExceptionResolver resolver = SpringContext.getBean("handlerExceptionResolver", HandlerExceptionResolver.class);
            if (resolver.resolveException(request, response, null, ex) == null) {
                throw ex;
            }
        }
        catch (Exception ex) {
            SecurityContextHolder.clearContext();
            HandlerExceptionResolver resolver = SpringContext.getBean("handlerExceptionResolver", HandlerExceptionResolver.class);
            if (resolver.resolveException(request, response, null, new FilterAuthenticationException("Authentication failed!", ex)) == null) {
                throw ex;
            }
        }
    }
}
