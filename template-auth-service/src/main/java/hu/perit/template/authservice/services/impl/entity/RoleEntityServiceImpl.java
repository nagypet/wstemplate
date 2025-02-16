package hu.perit.template.authservice.services.impl.entity;

import hu.perit.template.authservice.db.demodb.repo.RoleRepo;
import hu.perit.template.authservice.db.demodb.table.RoleEntity;
import hu.perit.template.authservice.services.api.entity.RoleEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoleEntityServiceImpl implements RoleEntityService
{
    private final RoleRepo repo;

    @Override
    public Set<RoleEntity> findByRoleIn(Set<String> roleNames)
    {
        return this.repo.findByRoleIn(roleNames);
    }
}
