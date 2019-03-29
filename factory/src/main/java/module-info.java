module io.github.factoryfx.factory {
    //automatic module that should be transitive but can't until they are real modules
    requires jackson.annotations;
    requires com.google.common;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jdk8;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires com.fasterxml.jackson.core;

    requires transitive java.sql;//for jackson DateDeserializer
    requires transitive org.slf4j;

    exports io.github.factoryfx.factory;
    exports io.github.factoryfx.factory.exception;
    exports io.github.factoryfx.factory.log;
    exports io.github.factoryfx.server;
    exports io.github.factoryfx.server.user;
    exports io.github.factoryfx.server.user.nop;
    exports io.github.factoryfx.server.user.persistent;
    exports io.github.factoryfx.factory.util;
    exports io.github.factoryfx.factory.parametrized;
    exports io.github.factoryfx.factory.builder;


    exports io.github.factoryfx.factory.attribute;
    exports io.github.factoryfx.factory.validation;
    exports io.github.factoryfx.factory.attribute.types;
    exports io.github.factoryfx.factory.attribute.primitive;
    exports io.github.factoryfx.factory.attribute.primitive.list;
    exports io.github.factoryfx.factory.attribute.time;
    exports io.github.factoryfx.factory.merge;
    exports io.github.factoryfx.factory.storage;
    exports io.github.factoryfx.factory.jackson;
    exports io.github.factoryfx.factory.storage.inmemory;
    exports io.github.factoryfx.factory.storage.filesystem;
    exports io.github.factoryfx.factory.storage.migration;
    exports io.github.factoryfx.factory.storage.migration.metadata;
    exports io.github.factoryfx.factory.storage.migration.datamigration;
    exports io.github.factoryfx.factory.attribute.dependency;

    opens io.github.factoryfx.factory.attribute;//open for Jackson
    opens io.github.factoryfx.factory.attribute.primitive;
    opens io.github.factoryfx.factory.attribute.time;
    opens io.github.factoryfx.factory.attribute.types;
    opens io.github.factoryfx.factory.merge;
    opens io.github.factoryfx.factory.storage.migration;
    opens io.github.factoryfx.factory.storage.migration.metadata;
    opens io.github.factoryfx.factory.attribute.dependency;
    opens io.github.factoryfx.factory;//jackson
    opens io.github.factoryfx.factory.parametrized;
    opens io.github.factoryfx.factory.log;
    exports io.github.factoryfx.factory.metadata;


}