package hu.perit.spvitamin.spring.security.oauth2.idp.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode
{
    INVALID_CLIENT("invalid_client", HttpStatus.UNAUTHORIZED),
    INVALID_REQUEST("invalid_request", HttpStatus.BAD_REQUEST),
    INVALID_GRANT("invalid_grant", HttpStatus.BAD_REQUEST),
    UNSUPPORTED_GRANT_TYPE("unsupported_grant_type", HttpStatus.BAD_REQUEST),
    ;

    private final String text;
    private final HttpStatus httpStatus;
}
