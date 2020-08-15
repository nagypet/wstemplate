CREATE SCHEMA IF NOT EXISTS dbo AUTHORIZATION postgres;

SET default_tablespace = 'pg_default';

CREATE TABLE IF NOT EXISTS dbo.role (
    roleid bigint NOT NULL,
    role character varying(255),
	CONSTRAINT role_pkey PRIMARY KEY (roleid)
);

ALTER TABLE dbo.role OWNER to postgres;

INSERT INTO dbo.role(roleid, role) VALUES (1, 'ROLE_ADMIN'), (2, 'ROLE_PUBLIC');


	