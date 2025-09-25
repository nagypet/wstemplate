package hu.perit.spvitamin.spring.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables configuration of the following endpoints:
 * - http://localhost:8410/api/spvitamin/oauth2/token
 * - http://localhost:8410/api/spvitamin/oauth2/refresh
 * - http://localhost:8410/.well-known/openid-configuration
 * - http://localhost:8410/.well-known/jwks.json
 * - http://localhost:8410/.well-known/userinfo
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface EnableSpvitaminOAuth2Idp
{
}
