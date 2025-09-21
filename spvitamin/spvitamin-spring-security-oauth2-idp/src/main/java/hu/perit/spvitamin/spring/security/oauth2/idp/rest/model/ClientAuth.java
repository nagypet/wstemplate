package hu.perit.spvitamin.spring.security.oauth2.idp.rest.model;

import lombok.Data;

@Data
public final class ClientAuth
{
    private final String clientId;
    private final String clientSecret;
}
