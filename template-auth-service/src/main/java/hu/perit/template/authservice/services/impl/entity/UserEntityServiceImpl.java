package hu.perit.template.authservice.services.impl.entity;

import hu.perit.template.authservice.db.demodb.repo.UserRepo;
import hu.perit.template.authservice.db.demodb.table.UserEntity;
import hu.perit.template.authservice.services.api.entity.UserEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserEntityServiceImpl implements UserEntityService
{
    private final UserRepo repo;

    @Override
    public List<UserEntity> findAll()
    {
        return this.repo.findAll();
    }

    @Override
    public Optional<UserEntity> findById(Long userId)
    {
        return this.repo.findById(userId);
    }

    @Override
    public UserEntity save(UserEntity userEntity)
    {
        return this.repo.save(userEntity);
    }

    @Override
    public void deleteById(Long userId)
    {
        this.repo.deleteById(userId);
    }

    @Override
    public Optional<UserEntity> findByUserName(String userName)
    {
        return this.repo.findByUserName(userName);
    }

    @Override
    @Transactional
    public int updateLastLoginTime(Long userId)
    {
        return this.repo.updateLastLoginTime(userId, OffsetDateTime.now());
    }
}
