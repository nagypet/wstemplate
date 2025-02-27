package hu.perit.spvitamin.spring.security.oauth2;

import hu.perit.spvitamin.spring.security.auth.AuthorizationService;
import hu.perit.spvitamin.spring.security.config.AuthenticationRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class OAuth2Configurer
{
    private final AuthorizationService authorizationService;
    private final AuthenticationRepository authenticationRepository;

    @PostConstruct
    private void setUp()
    {
        this.authorizationService.registerAuthenticatedUserFactory(new AuthenticatedUserFactoryForOidcUser());
        this.authenticationRepository.registerAuthenticationType(new OAuth2AuthType("microsoft"));
    }
}
