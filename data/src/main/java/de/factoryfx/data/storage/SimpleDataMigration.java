package de.factoryfx.data.storage;

import java.util.function.Function;

public class SimpleDataMigration implements DataMigration {

    private final int migrateTargetVersion;
    private final int migrateResultVersion;
    private final Function<String,String> migration;

    public SimpleDataMigration(int migrateTargetVersion, int migrateResultVersion, Function<String, String> migration) {
        this.migrateTargetVersion = migrateTargetVersion;
        this.migrateResultVersion = migrateResultVersion;
        this.migration = migration;
    }

    @Override
    public boolean canMigrate(int dataModelVersion) {
        return dataModelVersion==migrateTargetVersion;
    }

    @Override
    public String migrate(String data) {
        return migration.apply(data);
    }

    @Override
    public int migrateResultVersion() {
        return migrateResultVersion;
    }
}
