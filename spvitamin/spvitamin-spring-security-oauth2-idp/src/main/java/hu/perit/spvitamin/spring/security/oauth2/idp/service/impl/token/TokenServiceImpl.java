package hu.perit.spvitamin.spring.security.oauth2.idp.service.impl.token;

import hu.perit.spvitamin.spring.auth.AuthorizationToken;
import hu.perit.spvitamin.spring.security.AuthenticatedUser;
import hu.perit.spvitamin.spring.security.auth.jwt.JwtTokenProvider;
import hu.perit.spvitamin.spring.security.oauth2.idp.rest.model.TokenResult;
import hu.perit.spvitamin.spring.security.oauth2.idp.service.api.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService
{
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public TokenResult issueAccessTokenForClient(String clientId, Set<String> grantedScopes, Duration ttl)
    {
        AuthenticatedUser authenticatedUser = AuthenticatedUser.builder()
                .username(clientId)
                .authorities(grantedScopes.stream().map(SimpleGrantedAuthority::new).toList())
                .build();
        AuthorizationToken authorizationToken = jwtTokenProvider.generateToken(JwtTokenProvider.Type.ACCESS, authenticatedUser, clientId, grantedScopes, Instant.now(), ttl);
        return new TokenResult(authorizationToken.getJwt(), authorizationToken.getExp());
    }


    @Override
    public TokenResult issueRefreshTokenForClient(String clientId, Set<String> grantedScopes, Duration ttl)
    {
        AuthenticatedUser authenticatedUser = AuthenticatedUser.builder()
                .username(clientId)
                .authorities(grantedScopes.stream().map(SimpleGrantedAuthority::new).toList())
                .build();
        AuthorizationToken authorizationToken = jwtTokenProvider.generateToken(JwtTokenProvider.Type.REFRESH, authenticatedUser, clientId, grantedScopes, Instant.now(), ttl);
        return new TokenResult(authorizationToken.getJwt(), authorizationToken.getExp());
    }


    @Override
    public AuthorizationToken verifyRefreshToken(String refreshToken)
    {
        return this.jwtTokenProvider.getAuthorizationTokenFromJwt(refreshToken);
    }


    @Override
    public TokenResult issueAccessTokenForUser(String clientId, AuthenticatedUser authenticatedUser, Set<String> grantedScopes, Duration ttl)
    {
        AuthorizationToken authorizationToken = jwtTokenProvider.generateToken(JwtTokenProvider.Type.ACCESS, authenticatedUser, clientId, grantedScopes, Instant.now(), ttl);
        return new TokenResult(authorizationToken.getJwt(), authorizationToken.getExp());
    }


    @Override
    public TokenResult issueRefreshTokenForUser(String clientId, AuthenticatedUser authenticatedUser, Set<String> grantedScopes, Duration ttl)
    {
        AuthorizationToken authorizationToken = jwtTokenProvider.generateToken(JwtTokenProvider.Type.REFRESH, authenticatedUser, clientId, grantedScopes, Instant.now(), ttl);
        return new TokenResult(authorizationToken.getJwt(), authorizationToken.getExp());
    }


    @Override
    public TokenResult refreshToken(AuthorizationToken token, Duration ttl)
    {
        AuthorizationToken authorizationToken = token.clone();
        authorizationToken.setType(JwtTokenProvider.Type.ACCESS);
        Instant exp = Instant.now().plus(ttl);
        authorizationToken.setExp(exp);
        String jwt = this.jwtTokenProvider.getJwtFromAuthorizationToken(authorizationToken);
        return new TokenResult(jwt, exp);
    }
}
