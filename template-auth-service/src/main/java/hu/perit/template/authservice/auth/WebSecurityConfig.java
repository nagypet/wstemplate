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

import hu.perit.spvitamin.core.crypto.CryptoUtil;
import hu.perit.spvitamin.spring.auth.SimpleHttpSecurityBuilder;
import hu.perit.spvitamin.spring.config.LdapProperties;
import hu.perit.spvitamin.spring.config.SecurityProperties;
import hu.perit.spvitamin.spring.config.SpringContext;
import hu.perit.spvitamin.spring.config.SysConfig;
import hu.perit.spvitamin.spring.rest.api.AuthApi;
import hu.perit.spvitamin.spring.security.ldap.LdapAuthenticationProviderConfigurer;
import hu.perit.template.authservice.rest.api.UserApi;
import lombok.extern.log4j.Log4j;
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

/**
 * #know-how:simple-httpsecurity-builder
 *
 * @author Peter Nagy
 */

@EnableWebSecurity
@Log4j
public class WebSecurityConfig {

    /*
     * ============== Order(1) =========================================================================================
     */
    @Configuration
    @Order(1)
    public static class Order1 extends WebSecurityConfigurerAdapter {

        private final DbAuthenticationProvider dbAuthenticationProvider;

        public Order1(DbAuthenticationProvider dbAuthenticationProvider) {
            this.dbAuthenticationProvider = dbAuthenticationProvider;
        }

        /**
         * This is a global configuration, will be applied to all oder configurer adapters
         *
         * @param auth
         * @throws Exception
         */
        @Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
            SecurityProperties securityProperties = SysConfig.getSecurityProperties();

            // Admin user
            if (StringUtils.hasText(securityProperties.getAdminUserName()) && !"disabled".equals(securityProperties.getAdminUserName())) {
                CryptoUtil crypto = new CryptoUtil();
                PasswordEncoder passwordEncoder = getApplicationContext().getBean(PasswordEncoder.class);
                auth.inMemoryAuthentication()
                        .withUser(securityProperties.getAdminUserName())
                        .password(passwordEncoder.encode(crypto.decrypt(SysConfig.getCryptoProperties().getSecret(), securityProperties.getAdminUserEncryptedPassword())))
                        .authorities("ROLE_" + Role.ADMIN.name());
            }
            else {
                log.warn("admin user is disabled!");
            }

            // Ldap
            LdapProperties ldapProperties = SpringContext.getBean(LdapProperties.class);
            LdapAuthenticationProviderConfigurer.configure(auth, ldapProperties);

            // Here we have to add the DbAuthenticationProvider
            auth.authenticationProvider(this.dbAuthenticationProvider);
        }


        @Override
        protected void configure(HttpSecurity http) throws Exception {
            SimpleHttpSecurityBuilder.newInstance(http)
                    .scope(
                            AuthApi.BASE_URL_AUTHENTICATE
                    )
                    .basicAuth();

            http.addFilterAfter(new PostAuthenticationFilter(), SessionManagementFilter.class);
        }
    }


    /*
     * ============== Order(2) =========================================================================================
     */
    @Configuration
    @Order(2)
    public static class Order2 extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            SimpleHttpSecurityBuilder.newInstance(http)
                    .scope(
                            UserApi.BASE_URL_USERS + "/**"
                    )
                    .basicAuth(Role.ADMIN.name());

            http.addFilterAfter(new PostAuthenticationFilter(), SessionManagementFilter.class);
        }
    }
}
