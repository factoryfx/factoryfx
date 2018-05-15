package de.factoryfx.data.storage;

/**does nothing could be used for a consistent migration chain*/
public class NopDataMigration implements DataMigration {

    private final int migrateTargetVersion;
    private final int migrateResultVersion;

    public NopDataMigration(int migrateTargetVersion, int migrateResultVersion) {
        this.migrateTargetVersion = migrateTargetVersion;
        this.migrateResultVersion = migrateResultVersion;
    }

    @Override
    public boolean canMigrate(int dataModelVersion) {
        return dataModelVersion==migrateTargetVersion;
    }

    @Override
    public String migrate(String data) {
        return data;
    }

    @Override
    public int migrateResultVersion() {
        return migrateResultVersion;
    }
}
