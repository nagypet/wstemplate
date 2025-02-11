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

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * @author Peter Nagy
 */

@Embeddable
public class UserXRoleEntityPK implements Serializable, Comparable<UserXRoleEntityPK> {

    @Column(name = "userid", nullable = false)
    @NotNull
    private Long userId;

    @Column(name = "roleid", nullable = false)
    @NotNull
    private Long roleId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof UserXRoleEntityPK)) {
            return false;
        }

        UserXRoleEntityPK that = (UserXRoleEntityPK) o;

        return new EqualsBuilder()
                .append(userId, that.userId)
                .append(roleId, that.roleId)
                .isEquals();
    }


    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(userId)
                .append(roleId)
                .toHashCode();
    }


    @Override
    public int compareTo(UserXRoleEntityPK other) {
        if (other == this) { return 0; }
        return new CompareToBuilder()
                .append(this.userId, other.userId)
                .append(this.roleId, other.roleId)
                .toComparison();
    }
}
