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
