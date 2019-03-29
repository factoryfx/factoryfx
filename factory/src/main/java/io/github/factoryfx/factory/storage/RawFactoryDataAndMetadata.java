package io.github.factoryfx.factory.storage;

import com.fasterxml.jackson.databind.JsonNode;

public class RawFactoryDataAndMetadata<S>{
    public JsonNode root;
    public StoredDataMetadata<S> metadata;
}