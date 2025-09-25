/*
 * Copyright 2020-2025 the original author or authors.
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
