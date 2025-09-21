package hu.perit.spvitamin.spring.security.oauth2.idp.rest.api;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

public interface SpvitaminOAuth2Api
{
    @PostMapping(path = "/api/spvitamin/oauth2/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Map<String, Object>> token(
            @RequestParam MultiValueMap<String, String> form
    );


    @PostMapping(path = "/api/spvitamin/oauth2/refresh", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Map<String, Object>> refresh(
            @RequestParam MultiValueMap<String, String> form
    );

    @GetMapping(path = "/.well-known/openid-configuration", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Map<String, Object>> openidConfiguration();


    @GetMapping(path = "/.well-known/jwks.json", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Map<String, Object>> jwks() throws Exception;
}
