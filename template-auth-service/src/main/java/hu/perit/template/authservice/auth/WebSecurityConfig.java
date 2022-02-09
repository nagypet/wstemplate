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

import hu.perit.spvitamin.spring.config.LocalUserProperties;
import hu.perit.spvitamin.spring.security.auth.filter.Role2PermissionMapperFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.util.StringUtils;

import hu.perit.spvitamin.core.crypto.CryptoUtil;
import hu.perit.spvitamin.spring.config.SecurityProperties;
import hu.perit.spvitamin.spring.config.SysConfig;
import hu.perit.spvitamin.spring.rest.api.AuthApi;
import hu.perit.spvitamin.spring.security.auth.SimpleHttpSecurityBuilder;
import hu.perit.spvitamin.spring.security.ldap.LdapAuthenticationProviderConfigurer;
import hu.perit.template.authservice.rest.api.UserApi;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * #know-how:simple-httpsecurity-builder
 *
 * @author Peter Nagy
 */

@EnableWebSecurity
@Slf4j
public class WebSecurityConfig
{

    /*
     * ============== Order(1) =========================================================================================
     */
    @Configuration
    @Order(1)
    @RequiredArgsConstructor
    public static class Order1 extends WebSecurityConfigurerAdapter
    {

        private final DbAuthenticationProvider dbAuthenticationProvider;
        private final LdapAuthenticationProviderConfigurer ldapAuthenticationProviderConfigurer;
        private final LocalUserProperties localUserProperties;
        private final PasswordEncoder passwordEncoder;

        /**
         * This is a global configuration, will be applied to all oder configurer adapters
         *
         * @param auth
         * @throws Exception
         */
        @Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception
        {
            SecurityProperties securityProperties = SysConfig.getSecurityProperties();

            // Local users for test reasons
            for (Map.Entry<String, LocalUserProperties.User> userEntry : localUserProperties.getLocaluser().entrySet())
            {

                log.warn(String.format("local user name: '%s'", userEntry.getKey()));

                String password = null;
                if (userEntry.getValue().getEncryptedPassword() != null)
                {
                    CryptoUtil crypto = new CryptoUtil();

                    password = crypto.decrypt(SysConfig.getCryptoProperties().getSecret(), userEntry.getValue().getEncryptedPassword());
                }
                else
                {
                    password = userEntry.getValue().getPassword();
                }

                auth.inMemoryAuthentication() //
                        .withUser(userEntry.getKey()) //
                        .password(passwordEncoder.encode(password)) //
                        .authorities("ROLE_" + Role.EMPTY.name());
            }

            // Ldap
            this.ldapAuthenticationProviderConfigurer.configure(auth);

            // Here we have to add the DbAuthenticationProvider
            auth.authenticationProvider(this.dbAuthenticationProvider);
        }


        @Override
        protected void configure(HttpSecurity http) throws Exception
        {
            SimpleHttpSecurityBuilder.newInstance(http) //
                .scope(AuthApi.BASE_URL_AUTHENTICATE + "/**") //
                .basicAuthWithSession();

            http.addFilterAfter(new Role2PermissionMapperFilter(), SessionManagementFilter.class);
            http.addFilterAfter(new PostAuthenticationFilter(), Role2PermissionMapperFilter.class);
        }
    }


    /*
     * ============== Order(2) =========================================================================================
     */
    @Configuration
    @Order(2)
    public static class Order2 extends WebSecurityConfigurerAdapter
    {

        @Override
        protected void configure(HttpSecurity http) throws Exception
        {
            SimpleHttpSecurityBuilder.newInstance(http) //
                .scope(UserApi.BASE_URL_USERS + "/**") //
                .basicAuth(Role.ADMIN.name());

            http.addFilterAfter(new Role2PermissionMapperFilter(), SessionManagementFilter.class);
            http.addFilterAfter(new PostAuthenticationFilter(), Role2PermissionMapperFilter.class);
        }
    }
}
