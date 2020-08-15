/*
 * Copyright (c) 2020. Innodox Technologies Zrt.
 * All rights reserved.
 */

package hu.perit.template.authservice.admin;

import hu.perit.spvitamin.spring.admin.serverparameter.ServerParameter;
import hu.perit.spvitamin.spring.admin.serverparameter.ServerParameterList;
import hu.perit.template.authservice.config.AppDatasources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class SpecialServerParameters
{
    @Autowired
    private AppDatasources appDatasources;

    @Bean(name = "SpecialServerParameters")
    public ServerParameterList getParameterList()
    {
        ServerParameterList parameterList = new ServerParameterList();
        List<ServerParameter> props = parameterList.getParameter();

        props.addAll(ServerParameterList.of(this.appDatasources).getParameter());

        return parameterList;
    }
}
