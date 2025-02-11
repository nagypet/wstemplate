package hu.perit.template.scalableservice.service.api;

import hu.perit.spvitamin.spring.exception.ResourceNotFoundException;
import hu.perit.template.authservice.rest.model.UserDTO;

public interface UserService
{
    UserDTO getUserById(long userId) throws ResourceNotFoundException;
}
