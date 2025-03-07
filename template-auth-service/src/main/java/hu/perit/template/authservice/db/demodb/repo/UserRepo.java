/*
 * Copyright 2020-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package hu.perit.template.authservice.db.demodb.repo;

import hu.perit.template.authservice.db.demodb.table.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.Optional;

/**
 * @author Peter Nagy
 */

public interface UserRepo extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUserName(String userName);

    @Modifying
    @Query("UPDATE UserEntity e SET e.lastLoginTime = :loginTime WHERE e.userId = :userId")
    int updateLastLoginTime(@Param("userId") long userId, @Param("loginTime") OffsetDateTime loginTime);
}
