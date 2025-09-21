package hu.perit.spvitamin.spring.security.oauth2.idp.registry;

import hu.perit.spvitamin.spring.security.oauth2.idp.config.Constants;
import hu.perit.spvitamin.spring.security.oauth2.idp.config.SpvitaminOAuth2Properties;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SpvitaminClientRegistry
{
    private final SpvitaminOAuth2Properties props;


    public Optional<SpvitaminOAuth2Properties.ClientProps> authenticate(String clientId, String clientSecret)
    {
        if (clientId == null || clientSecret == null)
        {
            return Optional.empty();
        }
        return find(clientId).filter(c -> matches(c.getClientSecret(), clientSecret));
    }


    public Optional<SpvitaminOAuth2Properties.ClientProps> find(String clientId)
    {
        if (props.getClients() == null)
        {
            return Optional.empty();
        }
        return props.getClients().stream()
                .filter(c -> Objects.equals(c.getClientId(), clientId))
                .findFirst();
    }


    public Set<String> validateScopes(String grantType, Set<String> requested, Set<String> allowed)
    {
        if (requested == null || requested.isEmpty())
        {
            return allowed != null ? allowed : Collections.emptySet();
        }
        if (allowed == null)
        {
            return Collections.emptySet();
        }
        if (StringUtils.equalsIgnoreCase(grantType, Constants.CLIENT_CREDENTIALS) && requested.contains(Constants.OFFLINE_ACCESS))
        {
            throw new IllegalArgumentException("invalid_scope");
        }
        Set<String> req = requested.stream().filter(Objects::nonNull).collect(Collectors.toSet());
        if (!allowed.containsAll(req))
        {
            throw new IllegalArgumentException("invalid_scope");
        }
        return req;
    }


    private boolean matches(String stored, String raw)
    {
        if (stored == null)
        {
            return false;
        }
        return Objects.equals(stored, raw);
    }
}
