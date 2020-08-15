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

package hu.perit.template.scalableservice.auth;

import hu.perit.spvitamin.spring.auth.SimpleHttpSecurityBuilder;
import hu.perit.spvitamin.spring.auth.provider.authservice.AuthServiceAuthenticationProviderWithRestTemplate;
import hu.perit.spvitamin.spring.rest.api.AuthApi;
import hu.perit.template.scalableservice.rest.api.ServiceApi;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
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

        private final AuthenticationProvider authServiceAuthenticationProvider;

        public Order1(AuthServiceAuthenticationProviderWithRestTemplate authServiceAuthenticationProvider) {
            this.authServiceAuthenticationProvider = authServiceAuthenticationProvider;
        }


        @Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
            auth.authenticationProvider(this.authServiceAuthenticationProvider);
        }


        @Override
        protected void configure(HttpSecurity http) throws Exception {
            SimpleHttpSecurityBuilder.newInstance(http)
                    .scope(
                            AuthApi.BASE_URL_AUTHENTICATE
                    )
                    .basicAuth();
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
                            ServiceApi.BASE_URL_SERVICE + "/**"
                    )
                    .jwtAuth();
        }
    }
}