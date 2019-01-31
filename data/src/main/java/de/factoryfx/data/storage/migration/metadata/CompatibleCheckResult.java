package de.factoryfx.data.storage.migration.metadata;

public class CompatibleCheckResult {
    boolean compatible;

    public boolean isIncompatible() {
        return !compatible;
    }
}
