package de.factoryfx.data.storage;

import com.fasterxml.jackson.databind.JsonNode;
import de.factoryfx.data.Data;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * Patch interface for storage
 */
@FunctionalInterface
public interface DataStoragePatcher{
   void patch(JsonNode data, JsonNode metaData);
}
