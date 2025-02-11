package hu.perit.template.authservice.services.api;

import hu.perit.spvitamin.spring.exception.ResourceNotFoundException;
import hu.perit.spvitamin.spring.security.AuthenticatedUser;
import hu.perit.template.authservice.db.demodb.table.UserEntity;
import hu.perit.template.authservice.model.*;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;

public interface UserService
{
    /*
     * ============== getAll ===========================================================================================
     */
    List<UserDTOFiltered> getAll();

    /*
     * ============== getUserDTOById ===================================================================================
     */
    UserDTO getUserDTOById(long userId) throws ResourceNotFoundException;

    /*
     * ============== create ===========================================================================================
     */
    long create(CreateUserParams createUserParams);

    long create(CreateUserParams createUserParams, Boolean external);

    /*
     * ============== createAtLogin ====================================================================================
     */
    long createAtLogin(AuthenticatedUser authenticatedUser);

    /*
     * ============== update ===========================================================================================
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    void update(long userId, UpdateUserParams updateUserParams) throws ResourceNotFoundException;

    /*
     * ============== getUserEntity ====================================================================================
     */
    UserEntity getUserEntity(String userName, boolean filterInternal) throws ResourceNotFoundException;

    /*
     * ============== getUserIdByName ==================================================================================
     */
    long getUserIdByName(String userName);

    /*
     * ============== updateLoginTime ==================================================================================
     */
    void updateLoginTime(long userId);

    /*
     * ============== addRole ==========================================================================================
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    void addRole(Long userId, RoleSet roleSet) throws ResourceNotFoundException;

    /*
     * ============== deleteRole =======================================================================================
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    void deleteRole(Long userId, RoleSet roleSet) throws ResourceNotFoundException;
}
