package de.factoryfx.factory.datastorage;

//does nothing could be used for a consistent migration chain
public class NopFactoryMigration implements FactoryMigration{

    private final int migrateTargetVersion;
    private final int migrateResultVersion;

    public NopFactoryMigration(int migrateTargetVersion, int migrateResultVersion) {
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
