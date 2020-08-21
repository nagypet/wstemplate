/*
 * Copyright (c) 2020. Innodox Technologies Zrt.
 * All rights reserved.
 */

package hu.perit.template.authservice.admin;

import hu.perit.spvitamin.spring.admin.serverparameter.ServerParameterList;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
class SpecialServerParameters
{
    @Bean(name = "SpecialServerParameters")
    public ServerParameterList getParameterList()
    {
        ServerParameterList params = new ServerParameterList();

        // add sepcial server parameters

        return params;
    }
}
