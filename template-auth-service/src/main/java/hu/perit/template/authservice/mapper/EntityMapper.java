package hu.perit.template.authservice.mapper;

import hu.perit.template.authservice.db.demodb.table.UserEntity;
import hu.perit.template.authservice.model.CreateUserParams;
import hu.perit.template.authservice.model.UpdateUserParams;
import hu.perit.template.authservice.model.UserDTO;
import hu.perit.template.authservice.model.UserDTOFiltered;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

@Mapper(componentModel = "spring")
public interface EntityMapper
{
    UserEntity mapFromCreateParams(CreateUserParams params);

    UserDTO mapFromEntity(UserEntity entity);

    UserDTOFiltered mapFilteredFromEntity(UserEntity entity);

    List<UserDTOFiltered> mapFilteredFromEntity(List<UserEntity> entity);

    default OffsetDateTime map(Instant value)
    {
        return value == null ? null : OffsetDateTime.ofInstant(value, ZoneId.systemDefault());
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(@MappingTarget UserEntity userEntity, UpdateUserParams params);
}
