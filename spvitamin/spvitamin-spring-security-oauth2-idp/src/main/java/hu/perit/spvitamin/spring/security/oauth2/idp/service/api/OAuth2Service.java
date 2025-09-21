package hu.perit.spvitamin.spring.security.oauth2.idp.service.api;

import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import java.util.Map;

public interface OAuth2Service
{
    ResponseEntity<Map<String, Object>> token(MultiValueMap<String, String> form);

    ResponseEntity<Map<String, Object>> refresh(MultiValueMap<String, String> form);

    ResponseEntity<Map<String, Object>> openidConfiguration();
}
