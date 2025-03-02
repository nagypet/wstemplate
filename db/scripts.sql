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

-- Role: dbo
-- DROP ROLE IF EXISTS dbo;

CREATE ROLE dbo WITH
    LOGIN
    NOSUPERUSER
    INHERIT
    CREATEDB
    CREATEROLE
    NOREPLICATION
    NOBYPASSRLS
    ENCRYPTED PASSWORD 'SCRAM-SHA-256$4096:EtGDbgGxqeS5UPFCgSFu5A==$teLoyXC9oDsiqPJmmXeuuvrXcZRUwEsuHjEOLpIJaeE=:elb7H7nlk9Vueg0TbQnqhTwGaZWKNTTvlO6jp/SdVds=';

-- Database: testdb

-- DROP DATABASE IF EXISTS testdb;

CREATE DATABASE testdb
    WITH
    OWNER = dbo
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.utf8'
    LC_CTYPE = 'en_US.utf8'
    LOCALE_PROVIDER = 'libc'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;


-- SCHEMA: dbo

-- DROP SCHEMA IF EXISTS dbo ;

CREATE SCHEMA IF NOT EXISTS dbo
    AUTHORIZATION dbo;


-- Start the application now and insert the following roles

INSERT INTO dbo.role(roleid, role) VALUES (1, 'ROLE_ADMIN'), (2, 'ROLE_PUBLIC'), (3, 'ROLE_EMPTY');


