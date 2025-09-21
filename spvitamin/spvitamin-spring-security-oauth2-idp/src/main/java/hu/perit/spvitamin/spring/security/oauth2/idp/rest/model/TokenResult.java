package hu.perit.spvitamin.spring.security.oauth2.idp.rest.model;

import lombok.Data;

import java.time.Instant;

@Data
public final class TokenResult
{
    private final String token;
    private final Instant expiresAt;


    public long expiresInSeconds()
    {
        return Math.max(0, expiresAt.getEpochSecond() - Instant.now().getEpochSecond());
    }
}
