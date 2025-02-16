package hu.perit.template.authservice.services.api;

import hu.perit.spvitamin.spring.exception.ResourceNotFoundException;
import hu.perit.spvitamin.spring.security.AuthenticatedUser;
import hu.perit.template.authservice.db.demodb.table.UserEntity;
import hu.perit.template.authservice.model.CreateUserParams;
import hu.perit.template.authservice.model.RoleSet;
import hu.perit.template.authservice.model.UpdateUserParams;
import hu.perit.template.authservice.model.UserDTO;
import hu.perit.template.authservice.model.UserDTOFiltered;

import java.util.List;

public interface UserService
{
    List<UserDTOFiltered> getAll();

    UserDTO getUserDTOById(Long userId) throws ResourceNotFoundException;

    long create(CreateUserParams createUserParams);

    long create(CreateUserParams createUserParams, Boolean external);

    long createAtLogin(AuthenticatedUser authenticatedUser);

    void update(Long userId, UpdateUserParams updateUserParams) throws ResourceNotFoundException;

    UserEntity getUserEntity(String userName, boolean filterInternal) throws ResourceNotFoundException;

    void updateLoginTime(Long userId);

    void addRole(Long userId, RoleSet roleSet) throws ResourceNotFoundException;

    void deleteRole(Long userId, RoleSet roleSet) throws ResourceNotFoundException;
}
