CREATE TABLE FACTORY_CURRENT
                        (id VARCHAR(255) not NULL,
                         factory BLOB,
                         factoryMetadata BLOB,
                         PRIMARY KEY ( id ));

CREATE TABLE FACTORY_HISTORY
                        (id VARCHAR(255) not NULL,
                         factory BLOB,
                         factoryMetadata BLOB,
                         PRIMARY KEY ( id ));
