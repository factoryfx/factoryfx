module io.github.factoryfx.data {
    //automatic module that should be transitive but can't until they are real modules
    requires com.google.common;
    requires jackson.annotations;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jdk8;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires com.fasterxml.jackson.core;

    requires transitive java.sql;//for jackson DateDeserializer

    exports io.github.factoryfx.data.attribute;
    exports io.github.factoryfx.data.validation;
    exports io.github.factoryfx.data.attribute.types;
    exports io.github.factoryfx.data.util;
    exports io.github.factoryfx.data;
    exports io.github.factoryfx.data.attribute.primitive;
    exports io.github.factoryfx.data.attribute.primitive.list;
    exports io.github.factoryfx.data.attribute.time;
    exports io.github.factoryfx.data.merge;
    exports io.github.factoryfx.data.storage;
    exports io.github.factoryfx.data.jackson;
    exports io.github.factoryfx.data.storage.inmemory;
    exports io.github.factoryfx.data.storage.filesystem;
    exports io.github.factoryfx.data.storage.migration;
    exports io.github.factoryfx.data.storage.migration.metadata;
    exports io.github.factoryfx.data.storage.migration.datamigration;

    opens io.github.factoryfx.data.attribute;//open for Jackson
    opens io.github.factoryfx.data.attribute.primitive;
    opens io.github.factoryfx.data.attribute.time;
    opens io.github.factoryfx.data.attribute.types;
    opens io.github.factoryfx.data;
    opens io.github.factoryfx.data.merge;
    opens io.github.factoryfx.data.storage.migration;
    opens io.github.factoryfx.data.storage.migration.metadata;

}