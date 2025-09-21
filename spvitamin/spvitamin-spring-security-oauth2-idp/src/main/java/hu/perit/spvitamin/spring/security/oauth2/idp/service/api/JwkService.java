package hu.perit.spvitamin.spring.security.oauth2.idp.service.api;

import java.util.Map;

public interface JwkService
{
    Map<String, Object> getJwks() throws Exception;
}
