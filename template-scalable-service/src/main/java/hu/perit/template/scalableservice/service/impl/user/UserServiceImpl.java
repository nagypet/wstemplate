package hu.perit.template.scalableservice.service.impl.user;

import hu.perit.spvitamin.spring.config.MicroserviceCollectionProperties;
import hu.perit.spvitamin.spring.config.MicroserviceProperties;
import hu.perit.spvitamin.spring.exception.ResourceNotFoundException;
import hu.perit.spvitamin.spring.feignclients.ForwardingAuthRequestInterceptor;
import hu.perit.spvitamin.spring.feignclients.SimpleFeignClientBuilder;
import hu.perit.template.authservice.rest.client.TemplateAuthClient;
import hu.perit.template.authservice.rest.model.UserDTO;
import hu.perit.template.scalableservice.service.api.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService
{
    private final MicroserviceCollectionProperties microserviceCollectionProperties;
    private TemplateAuthClient templateAuthClient;

    @PostConstruct
    private void setup()
    {
        MicroserviceProperties authServiceProperties = this.microserviceCollectionProperties.get("auth-service");
        this.templateAuthClient = SimpleFeignClientBuilder.newInstance()
                .requestInterceptor(new ForwardingAuthRequestInterceptor())
                .build(TemplateAuthClient.class, authServiceProperties.getUrl());
    }


    @Override
    public UserDTO getUserById(long userId) throws ResourceNotFoundException
    {
        return this.templateAuthClient.getUserById(null, userId);
    }
}
