/*
 * Copyright 2020-2020 the original author or authors.
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

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This class would not be necessary, because the @Jointable annotation creates the join table. There is a bug in H2 database
 * stating that Hibernate tries to create a second primary key. Alternatively the DB could be created by a script.
 * There is no problem with PostgreSQL.
 *
 * Caused by: org.h2.jdbc.JdbcSQLSyntaxErrorException: Attempt to define a second primary key; SQL statement:
 * create table dbo.userrole (userid bigserial not null, roleid bigserial not null, primary key (userid, roleid)) [90017-200]
 * 	at org.h2.message.DbException.getJdbcSQLException(DbException.java:576)
 * 	at org.h2.message.DbException.getJdbcSQLException(DbException.java:429)
 * 	at org.h2.message.DbException.get(DbException.java:205)
 * 	at org.h2.message.DbException.get(DbException.java:181)
 * 	at org.h2.message.DbException.get(DbException.java:170)
 *
 * @author Peter Nagy
 */

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "userrole", schema = "dbo")
public class UserRoleEntity {

    @EmbeddedId
    private UserRoleEntityPK pk;
}
