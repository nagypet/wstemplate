package hu.perit.template.authservice.services.api.entity;

import hu.perit.template.authservice.db.demodb.table.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UserEntityService
{
    List<UserEntity> findAll();

    Optional<UserEntity> findById(Long userId);

    UserEntity save(UserEntity userEntity);

    void deleteById(Long userId);

    Optional<UserEntity> findByUserName(String userName);

    int updateLastLoginTime(Long userId);
}
