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

package hu.perit.template.authservice.documentation;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.*;

/**
 * @author Peter Nagy
 */


@Configuration
@EnableSwagger2
public class SwaggerConfig {

    private static final Contact DEFAULT_CONTACT = new Contact(
            "Peter Nagy", "http://...", "nagy.peter.home@gmail.com");

    private static final ArrayList<VendorExtension> VENDOR_EXTENSIONS = new ArrayList<>();

    private static final ApiInfo DEFAULT_API_INFO = new ApiInfo(
            "Template authentication service",
            "This is an open source attempt to build a production ready web service using the Spring framework.",
            "1.0",
            "urn:tos",
            DEFAULT_CONTACT,
            "Apache License, Version 2.0",
            "https://www.apache.org/licenses/LICENSE-2.0",
            VENDOR_EXTENSIONS);

    private static final Set<String> DEFAULT_PRODUCES_AND_CONSUMES = new HashSet<>(Arrays.asList("application/json"));

    private static final List<SecurityScheme> DEFAULT_SECURITY_SCHEME = new ArrayList<>(Arrays.asList(
            new BasicAuth("basicAuth"),
            new ApiKey("Bearer",
                    "Authorization",
                    "header")
    ));

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(DEFAULT_API_INFO)
                .produces(DEFAULT_PRODUCES_AND_CONSUMES)
                .consumes(DEFAULT_PRODUCES_AND_CONSUMES)
                .securitySchemes(DEFAULT_SECURITY_SCHEME)
                .useDefaultResponseMessages(false)
                .select()
                .paths(Predicates.not(PathSelectors.regex("/error.*"))).build();
    }
}
