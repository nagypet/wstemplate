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

package hu.perit.template.authservice;

import hu.perit.spvitamin.spring.SpvitaminApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.retry.annotation.EnableRetry;

/**
 * @author Peter Nagy
 */

@Profile("!unittest")
@SpringBootApplication
@ComponentScan(basePackages = {"hu.perit.spvitamin", "hu.perit.template.authservice"})
@EnableRetry
@EnableFeignClients(basePackages = {"hu.perit.template.authservice"})
public class AuthServiceApplication
{
    public static void main(String[] args)
    { // NOSONAR
        SpvitaminApplication.run(AuthServiceApplication.class, args);
    }
}
