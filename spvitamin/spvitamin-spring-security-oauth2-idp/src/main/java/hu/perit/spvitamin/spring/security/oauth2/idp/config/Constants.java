package hu.perit.spvitamin.spring.security.oauth2.idp.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants
{
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String CLIENT_CREDENTIALS = "client_credentials";
    public static final String SCOPE = "scope";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String TOKEN_TYPE = "token_type";
    public static final String BEARER = "bearer";
    public static final String EXPIRES_IN = "expires_in";

    public static final String OFFLINE_ACCESS = "offline_access";

    public static final String REFRESH_TOKEN_COOKIE_NAME = "d0fbf0dd-1bc5-45c6-9061-834ed3a6e3b1";
}
