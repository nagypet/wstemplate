/*
 * Copyright 2020-2025 the original author or authors.
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

package hu.perit.template.scalableservice.service.impl.user;

import hu.perit.spvitamin.spring.config.MicroserviceCollectionProperties;
import hu.perit.spvitamin.spring.config.MicroserviceProperties;
import hu.perit.spvitamin.spring.exception.ResourceNotFoundException;
import hu.perit.spvitamin.spring.feignclients.ForwardingAuthRequestInterceptor;
import hu.perit.spvitamin.spring.feignclients.SimpleFeignClientBuilder;
import hu.perit.template.authservice.api.TemplateAuthServiceClient;
import hu.perit.template.authservice.model.UserDTO;
import hu.perit.template.scalableservice.service.api.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService
{
    private final MicroserviceCollectionProperties microserviceCollectionProperties;
    private TemplateAuthServiceClient templateAuthServiceClient;

    @PostConstruct
    private void setup()
    {
        MicroserviceProperties authServiceProperties = this.microserviceCollectionProperties.get("auth-service");
        this.templateAuthServiceClient = SimpleFeignClientBuilder.newInstance()
                .requestInterceptor(new ForwardingAuthRequestInterceptor())
                .build(TemplateAuthServiceClient.class, authServiceProperties.getUrl());
    }


    @Override
    public UserDTO getUserById(long userId) throws ResourceNotFoundException
    {
        return this.templateAuthServiceClient.getUserById(userId);
    }
}
