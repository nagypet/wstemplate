package hu.perit.template.authservice.api;

import hu.perit.spvitamin.spring.exception.ResourceNotFoundException;
import hu.perit.template.authservice.model.*;

import java.util.List;

public interface TemplateAuthServiceApi
{
    List<UserDTOFiltered> getAllUsers();

    UserDTO getUserById(Long userId) throws ResourceNotFoundException;

    ResponseUri createUser(CreateUserParams createUserParams);

    void updateUser(Long userId, UpdateUserParams updateUserParams) throws ResourceNotFoundException;

    void deleteUser(Long userId) throws ResourceNotFoundException;

    void addRole(Long userId, RoleSet roleSet) throws ResourceNotFoundException;

    void deleteRole(Long userId, RoleSet roleSet) throws ResourceNotFoundException;
}
