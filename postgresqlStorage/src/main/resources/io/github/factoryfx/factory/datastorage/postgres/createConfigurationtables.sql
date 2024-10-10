CREATE TABLE currentconfiguration (
    root JSON NOT NULL,
    metadata JSON NOT NULL,
    createdAt TIMESTAMP NOT NULL,
    id varchar(1024) NOT NULL
);

CREATE TABLE configuration (
    root JSON NOT NULL,
    metadata JSON NOT NULL,
    createdAt TIMESTAMP NOT NULL,
    id varchar(1024) PRIMARY KEY
);

CREATE TABLE futureconfiguration (
    root JSON NOT NULL,
    metadata JSON NOT NULL,
    createdAt TIMESTAMP NOT NULL,
    id varchar(1024) PRIMARY KEY
);

CREATE INDEX ix_configuration_createdat on configuration (createdAt);

CREATE INDEX ix_futureconfiguration_createdat on futureconfiguration (createdAt);
