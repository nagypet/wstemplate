package hu.perit.spvitamin.spring.security.oauth2;

import hu.perit.spvitamin.spring.config.SecurityProperties;
import hu.perit.spvitamin.spring.security.auth.AuthorizationService;
import hu.perit.spvitamin.spring.security.config.AuthenticationRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ClientRegistrations;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

import java.util.*;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class OAuth2Configurer
{
    public static final String IGNORED = "ignored";

    private final AuthorizationService authorizationService;
    private final AuthenticationRepository authenticationRepository;
    private final SecurityProperties securityProperties;

    @PostConstruct
    private void setUp()
    {
        this.authorizationService.registerAuthenticatedUserFactory(new AuthenticatedUserFactoryForOidcUser());
        this.authorizationService.registerAuthenticatedUserFactory(new AuthenticatedUserFactoryForDefaultOAuth2User());
        Map<String, SecurityProperties.OAuth2Provider> providerMap = Optional.ofNullable(this.securityProperties.getOauth2()).map(i -> i.getProviders()).orElse(Collections.emptyMap());
        if (!providerMap.isEmpty())
        {
            providerMap.forEach((key, value) -> {
                log.info("Configuring OAuth2 provider '{}'.", key);
                this.authenticationRepository.registerAuthenticationType(new OAuth2AuthType(key, value.getDisplayName()));
            });
        }
    }


    @Bean
    public ClientRegistrationRepository clientRegistrationRepository()
    {
        Map<String, SecurityProperties.OAuth2Provider> providerMap = Optional.ofNullable(this.securityProperties.getOauth2()).map(i -> i.getProviders()).orElse(Collections.emptyMap());
        if (providerMap.isEmpty())
        {
            return new InMemoryClientRegistrationRepository(getDummyClientRegistration());
        }

        List<ClientRegistration> registrations = new ArrayList<>();
        for (Map.Entry<String, SecurityProperties.OAuth2Provider> entry : providerMap.entrySet())
        {
            SecurityProperties.OAuth2Provider provider = entry.getValue();
            String registrationId = entry.getKey();

            ClientRegistration clientRegistration = null;
            if ("facebook".equals(registrationId)) {
                clientRegistration = ClientRegistration.withRegistrationId(entry.getKey())
                        .clientId(provider.getClientId())
                        .clientSecret(provider.getClientSecret())
                        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                        .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                        .scope(provider.getScopes())
                        .authorizationUri("https://www.facebook.com/v12.0/dialog/oauth")
                        .tokenUri("https://graph.facebook.com/v12.0/oauth/access_token")
                        .userInfoUri("https://graph.facebook.com/me?fields=id,name,email,picture")
                        .userNameAttributeName("id")
                        .clientName(registrationId)
                        .build();
            }
            else
            {
                clientRegistration = ClientRegistrations.fromIssuerLocation(provider.getIssuerUri())
                        .registrationId(registrationId)
                        .clientId(provider.getClientId())
                        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                        .clientName(registrationId)
                        .clientSecret(provider.getClientSecret())
                        .scope(provider.getScopes())
                        .build();
            }
            registrations.add(clientRegistration);
        }
        return new InMemoryClientRegistrationRepository(registrations);
    }


    // This is needed for the server to start when the oauth dependencies are present
    public ClientRegistration getDummyClientRegistration()
    {
        return ClientRegistration.withRegistrationId("dummy")
                .clientId(IGNORED)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationUri(IGNORED)
                .redirectUri(IGNORED)
                .tokenUri(IGNORED)
                .build();
    }


    @Bean
    public OAuth2AuthorizedClientRepository authorizedClientRepository()
    {
        return new HttpSessionOAuth2AuthorizedClientRepository();
    }

}
