/*
 * Copyright 2020-2022 the original author or authors.
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

package hu.perit.template.scalableservice.documentation;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Peter Nagy
 */


@Configuration
public class SwaggerConfig
{
	@Bean
	public OpenAPI openApi()
	{
		return new OpenAPI()
				.info(new Info()
						.title("template-scalable-service")
						.description("template-scalable-service")
						.version("1.0")
						.license(new License()
								.name("Apache License, Version 2.0")
								.url("https://www.apache.org/licenses/LICENSE-2.0"))
						.contact(new Contact()
								.name("Peter Nagy")
								.url("https://github.com/nagypet/wstemplate")
								.email("nagy.peter.home@gmail.com")))
				.components(new Components()
						.addSecuritySchemes("basic", new io.swagger.v3.oas.models.security.SecurityScheme().type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP).scheme("basic"))
						.addSecuritySchemes("bearer", new io.swagger.v3.oas.models.security.SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer"))
				);
	}
}
