module de.factoryfx.data {
    //automatic module that should be transitive but can't until they are real modules
    requires com.google.common;
    requires jackson.annotations;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jdk8;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires com.fasterxml.jackson.core;

    requires transitive java.sql;//for jackson DateDeserializer

    exports de.factoryfx.data.attribute;
    exports de.factoryfx.data.validation;
    exports de.factoryfx.data.attribute.types;
    exports de.factoryfx.data.util;
    exports de.factoryfx.data;
    exports de.factoryfx.data.attribute.primitive;
    exports de.factoryfx.data.attribute.primitive.list;
    exports de.factoryfx.data.attribute.time;
    exports de.factoryfx.data.merge;
    exports de.factoryfx.data.storage;
    exports de.factoryfx.data.jackson;
    exports de.factoryfx.data.storage.inmemory;
    exports de.factoryfx.data.storage.filesystem;
    exports de.factoryfx.data.storage.migration;
    exports de.factoryfx.data.storage.migration.metadata;

    opens de.factoryfx.data.attribute;//open for Jackson
    opens de.factoryfx.data.attribute.primitive;
    opens de.factoryfx.data.attribute.time;
    opens de.factoryfx.data.attribute.types;
    opens de.factoryfx.data;
    opens de.factoryfx.data.merge;
    opens de.factoryfx.data.storage.migration;
    opens de.factoryfx.data.storage.migration.metadata;

}