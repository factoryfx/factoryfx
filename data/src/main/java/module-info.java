module de.factoryfx.data {
    requires jackson.annotations;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jdk8;
    requires com.fasterxml.jackson.datatype.jsr310;

    exports de.factoryfx.data.attribute;
    exports de.factoryfx.data.validation;
    exports de.factoryfx.data.attribute.types;
    exports de.factoryfx.data.util;
    exports de.factoryfx.data;
    exports de.factoryfx.data.attribute.primitive;
    exports de.factoryfx.data.attribute.time;
    exports de.factoryfx.data.merge;
    exports de.factoryfx.data.storage;
    exports de.factoryfx.data.jackson;
    exports de.factoryfx.data.storage.inmemory;
    exports de.factoryfx.data.storage.filesystem;
}