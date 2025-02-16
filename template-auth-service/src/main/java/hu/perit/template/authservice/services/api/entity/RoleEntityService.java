package hu.perit.template.authservice.services.api.entity;

import hu.perit.template.authservice.db.demodb.table.RoleEntity;

import java.util.Set;

public interface RoleEntityService
{
    Set<RoleEntity> findByRoleIn(Set<String> roleNames);
}
