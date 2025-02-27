package hu.perit.spvitamin.spring.security.config;

import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Getter
public class AuthenticationRepository
{
    private final List<AuthenticationType> authenticationTypes = new ArrayList<>();

    public void registerAuthenticationType(final AuthenticationType authenticationType)
    {
        this.authenticationTypes.add(authenticationType);
    }
}
