CREATE TABLE IF NOT EXISTS FACTORY_CURRENT
                        (id VARCHAR(255) not NULL,
                         factory BLOB,
                         factoryMetadata BLOB,
                         PRIMARY KEY ( id );

CREATE TABLE IF NOT EXISTS FACTORY_HISTORY
                        (id VARCHAR(255) not NULL,
                         factory BLOB,
                         factoryMetadata BLOB,
                         PRIMARY KEY ( id ));
