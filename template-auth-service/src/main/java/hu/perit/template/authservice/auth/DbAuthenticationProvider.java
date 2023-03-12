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

import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import hu.perit.spvitamin.spring.exception.ResourceNotFoundException;
import hu.perit.spvitamin.spring.security.AuthenticatedUser;
import hu.perit.template.authservice.db.demodb.table.UserEntity;
import hu.perit.template.authservice.services.UserService;
import lombok.extern.slf4j.Slf4j;

/**
 * #know-how:custom-authentication-provider
 *
 * @author Peter Nagy
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class DbAuthenticationProvider implements AuthenticationProvider
{
    private final ApplicationContext applicationContext;
    private final UserService userService;

    private AuthenticatedUser loadUserByUsernameAndPassword(String userName, String password)
    {
        try
        {
            UserEntity userEntity = this.userService.getUserEntity(userName, true);

            PasswordEncoder passwordEncoder = this.applicationContext.getBean(PasswordEncoder.class);
            if (!passwordEncoder.matches(password, userEntity.getEncryptedPassword()))
            {
                throw new DbAuthenticationException("Invalid user credentials!");
            }

            return AuthenticatedUser.builder().username(userEntity.getUserName()).userId(userEntity.getUserId()).authorities(
                userEntity.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())).build();
        }
        catch (RuntimeException | ResourceNotFoundException ex)
        {
            log.debug("Authentication failed: " + ex.getMessage());
            throw new DbAuthenticationException("Authentication failed!", ex);
        }
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException
    {
        if (!(authentication instanceof UsernamePasswordAuthenticationToken))
        {
            throw new UnsupportedOperationException("Only UsernamePasswordAuthenticationToken supported!");
        }

        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;

        AuthenticatedUser authenticatedUser = this.loadUserByUsernameAndPassword((String) token.getPrincipal(),
            (String) token.getCredentials());

        if (authenticatedUser.getUserId() != 0)
        {
            this.userService.updateLoginTime(authenticatedUser.getUserId());
        }

        return new UsernamePasswordAuthenticationToken(authenticatedUser, null, authenticatedUser.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> aClass)
    {
        return aClass.equals(UsernamePasswordAuthenticationToken.class);
    }
}
