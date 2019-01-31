package de.factoryfx.data.storage.migration.metadata;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface CompatibleCheck {

    boolean isCompatible(ObjectNode object);
}
