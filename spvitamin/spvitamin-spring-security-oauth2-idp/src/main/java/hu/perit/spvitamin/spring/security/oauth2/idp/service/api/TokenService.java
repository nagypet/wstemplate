package hu.perit.spvitamin.spring.security.oauth2.idp.service.api;

import hu.perit.spvitamin.spring.auth.AuthorizationToken;
import hu.perit.spvitamin.spring.security.AuthenticatedUser;
import hu.perit.spvitamin.spring.security.oauth2.idp.rest.model.TokenResult;

import java.time.Duration;
import java.util.Set;

public interface TokenService
{
    TokenResult issueAccessTokenForClient(String clientId, Set<String> scopes, Duration ttl);

    TokenResult issueRefreshTokenForClient(String clientId, Set<String> scopes, Duration ttl);

    AuthorizationToken verifyRefreshToken(String refreshToken);

    TokenResult issueAccessTokenForUser(String clientId, AuthenticatedUser authenticatedUser, Set<String> grantedScopes, Duration ttl);

    TokenResult issueRefreshTokenForUser(String clientId, AuthenticatedUser authenticatedUser, Set<String> grantedScopes, Duration ttl);

    TokenResult refreshToken(AuthorizationToken token, Duration ttl);
}
