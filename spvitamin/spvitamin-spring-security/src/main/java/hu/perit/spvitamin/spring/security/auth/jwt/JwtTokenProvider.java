/*
 * Copyright 2020-2025 the original author or authors.
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

package hu.perit.spvitamin.spring.security.auth.jwt;

import hu.perit.spvitamin.core.domainuser.DomainUser;
import hu.perit.spvitamin.spring.auth.AuthorizationToken;
import hu.perit.spvitamin.spring.config.JwtProperties;
import hu.perit.spvitamin.spring.keystore.KeystoreUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author Peter Nagy
 */

@Slf4j
@AllArgsConstructor
@Component
public class JwtTokenProvider
{

    private final JwtProperties jwtProperties;


    public AuthorizationToken generateToken(String subject, Claims additionalClaims)
    {
        try
        {
            LocalDateTime issuedAt = LocalDateTime.now();
            LocalDateTime expiryDate = issuedAt.plusMinutes(jwtProperties.getExpirationInMinutes());
            Key privateKey = KeystoreUtils.getPrivateKey();

            Date iat = Date.from(issuedAt.atZone(ZoneId.systemDefault()).toInstant());
            Date exp = Date.from(expiryDate.atZone(ZoneId.systemDefault()).toInstant());

            DomainUser domainUser = DomainUser.newInstance(subject);

            String jwt = Jwts.builder()
                .subject(domainUser.getCanonicalName())
                .issuedAt(iat)
                .expiration(exp)
                .claims(additionalClaims)
                .signWith(privateKey, SignatureAlgorithm.RS512)
                .compact();

            return new AuthorizationToken(domainUser.getCanonicalName(), jwt, issuedAt, expiryDate);
        }
        catch (Exception e)
        {
            throw new JwtException("Token creation failed!", e);
        }
    }


    public Claims getClaims(String jwt)
    {
        try
        {
            PublicKey publicKey = KeystoreUtils.getPublicKey();
            return new DefaultClaims(Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(jwt)
                .getPayload());
        }
        catch (Exception ex)
        {
            throw new JwtException("JWT token parse failed!", ex);
        }
    }
}
