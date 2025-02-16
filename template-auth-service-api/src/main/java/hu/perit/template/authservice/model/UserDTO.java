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

package hu.perit.template.authservice.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Set;

/**
 * @author Peter Nagy
 */

@Data
@NoArgsConstructor
public class UserDTO
{

    // Properties for /users GET
    private Long userId;
    private String userName;
    private String displayName;
    private Set<String> roles;
    private Boolean external;
    private Boolean active;

    // Additional properties for /users{id} GET
    private String address;
    private String email;
    private String phone;
    private Boolean nextLoginChangePwd;

    // Auditing
    private Long createdById;
    private OffsetDateTime createdAt;
    private Long updatedById;
    private OffsetDateTime updatedAt;
    private Long deletedById;
    private OffsetDateTime deletedAt;

    private Boolean deletedFlag;

    private OffsetDateTime lastLoginTime;
    private Boolean passwordExpired;
    private Long recVersion;
}
