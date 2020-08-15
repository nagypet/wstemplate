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

/*
 * Copyright (c) 2020. Innodox Technologies Zrt.
 * All rights reserved.
 */

package hu.perit.template.eureka.admin;

import hu.perit.spvitamin.spring.admin.serverparameter.ServerParameter;
import hu.perit.spvitamin.spring.admin.serverparameter.ServerParameterList;
import hu.perit.spvitamin.spring.config.ServerProperties;
import lombok.AllArgsConstructor;
import org.springframework.cloud.netflix.eureka.server.EurekaDashboardProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
class SpecialServerParameters
{
    private final ServerProperties serverProperties;
    private final EurekaDashboardProperties eurekaDashboardProperties;

    @Bean(name = "SpecialServerParameters")
    public ServerParameterList getParameterList()
    {
        ServerParameterList params = new ServerParameterList();

        params.add(new ServerParameter("(4) GUI", this.serverProperties.getServiceUrl() + this.eurekaDashboardProperties.getPath(), true));
        params.add(ServerParameterList.of(this.eurekaDashboardProperties));

        return params;
    }
}
