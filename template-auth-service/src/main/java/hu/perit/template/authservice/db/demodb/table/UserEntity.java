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

package hu.perit.template.authservice.db.demodb.table;

import hu.perit.template.authservice.config.Constants;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * #know-how:jpa-auditing
 *
 * @author Peter Nagy
 */

@Getter
@Setter
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = UserEntity.TABLE_NAME, schema = Constants.SCHEMA, indexes = {
        @Index(columnList = "username", name = UserEntity.IX_USERNAME, unique = true)
})
public class UserEntity
{
    public static final String TABLE_NAME = "user";
    public static final String IX_USERNAME = "ix_user_01";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_generator")
    @SequenceGenerator(name = "user_generator", sequenceName = "user_seq", schema = Constants.SCHEMA, allocationSize = 200)
    @Column(name = "userid", nullable = false)
    private Long userId;

    @Column(name = "username")
    @Length(min = 3, max = 25)
    private String userName;

    @Column(name = "displayname")
    @Length(max = 100)
    private String displayName;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = UserXRoleEntity.TABLE_NAME, schema = Constants.SCHEMA,
            joinColumns = {@JoinColumn(name = "userid")},
            inverseJoinColumns = {@JoinColumn(name = "roleid")}
    )
    // Important to have a Set here! Hibernate will generate a composite primary key only when using a Set.
    private Set<RoleEntity> roleEntities;

    public Set<String> getRoles()
    {
        if (roleEntities != null)
        {
            return this.roleEntities.stream().map(RoleEntity::getRole).collect(Collectors.toSet());
        }
        else
        {
            return Collections.emptySet();
        }
    }

    @Column(name = "external", nullable = false)
    @NotNull
    private Boolean external;

    @Column(name = "active", nullable = false)
    @NotNull
    private Boolean active;

    @Column(name = "address")
    @Length(max = 250)
    private String address;

    @Column(name = "email")
    @Length(max = 100)
    private String email;

    @Column(name = "phone")
    @Length(max = 50)
    private String phone;

    @Column(name = "next_login_change_pwd", nullable = false)
    @NotNull
    private Boolean nextLoginChangePwd;

    // Auditing
    @CreatedBy
    @Column(name = "createdbyid", nullable = false, updatable = false)
    private Long createdById;
    @Column(name = "createdat", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedBy
    @Column(name = "updatedbyid", nullable = false, updatable = true)
    private Long updatedById;
    @Column(name = "updatedat", nullable = false, updatable = true)
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(name = "deletedbyid")
    private Long deletedById;
    @Column(name = "deletedat")
    private LocalDateTime deletedAt;


    @Column(name = "deletedflag")
    private Boolean deletedFlag;


    @Column(name = "lastlogintime")
    private LocalDateTime lastLoginTime;

    @Column(name = "encryptedpassword")
    @Length(max = 250)
    private String encryptedPassword;

    @Column(name = "passwordexpired")
    private Boolean passwordExpired;
}
