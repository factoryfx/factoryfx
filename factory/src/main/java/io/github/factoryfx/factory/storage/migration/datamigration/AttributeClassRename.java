package io.github.factoryfx.factory.storage.migration.datamigration;

import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadataDictionary;

import java.util.List;

/**
 * migration for a renamed or moved custom attribute class whose serialized form is unchanged
 * (e.g. the attribute class was moved to another package).<br>
 * The stored data tree does not contain the attribute class but the stored metadata dictionary does
 * ({@code AttributeStorageMetadata.attributeClassName}); a stale name there marks the attribute as
 * retyped on load, which clears the stored values. This migration rewrites the recorded attribute
 * class name so the attribute is no longer detected as retyped and keeps its values.
 */
public class AttributeClassRename implements DataMigration {
    private final String previousAttributeClassNameFullQualified;
    private final String newAttributeClassNameFullQualified;

    public AttributeClassRename(String previousAttributeClassNameFullQualified, String newAttributeClassNameFullQualified) {
        this.previousAttributeClassNameFullQualified = previousAttributeClassNameFullQualified;
        this.newAttributeClassNameFullQualified = newAttributeClassNameFullQualified;
    }

    @Override
    public boolean canMigrate(DataStorageMetadataDictionary pastDataStorageMetadataDictionary) {
        return true;//retypeAttributeClass is an idempotent no-op if no attribute recorded the previous class
    }

    @Override
    public void migrate(List<DataJsonNode> dataJsonNodes) {
        //the attribute class is not part of the stored data tree, only the metadata dictionary is rewritten
    }

    @Override
    public void updateDataStorageMetadataDictionary(DataStorageMetadataDictionary dataStorageMetadataDictionary) {
        dataStorageMetadataDictionary.retypeAttributeClass(previousAttributeClassNameFullQualified,newAttributeClassNameFullQualified);
    }
}
