package de.factoryfx.factory.datastorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import org.junit.Assert;
import org.junit.Test;

public class FactorySerialisationManagerTest {

    @Test
    public void read_read(){
        JacksonDeSerialisation<ExampleFactoryA> deSerialisation = new JacksonDeSerialisation<>(ExampleFactoryA.class, 1);
        JacksonSerialisation<ExampleFactoryA> serialisation = new JacksonSerialisation<>(1);
        FactorySerialisationManager<ExampleFactoryA> manager = new FactorySerialisationManager<>(serialisation,deSerialisation, Collections.emptyList());
        ExampleFactoryA result = manager.read(ObjectMapperBuilder.build().writeValueAsString(new ExampleFactoryA()),1);
        Assert.assertNotNull(result);

    }



    @Test
    public void read_read_migration(){
        List<FactoryMigration> migrations = new ArrayList<>();
        migrations.add(new FactoryMigration() {
            @Override
            public boolean canMigrate(int dataModelVersion) {
                return dataModelVersion==1;
            }

            @Override
            public String migrate(String data) {
                return data;
            }

            @Override
            public int migrateResultVersion() {
                return 2;
            }
        });



        JacksonDeSerialisation<ExampleFactoryA> deSerialisation = new JacksonDeSerialisation<>(ExampleFactoryA.class, 2);
        JacksonSerialisation<ExampleFactoryA> serialisation = new JacksonSerialisation<>(2);
        FactorySerialisationManager<ExampleFactoryA> manager = new FactorySerialisationManager<>(serialisation,deSerialisation, migrations);
        ExampleFactoryA result = manager.read(ObjectMapperBuilder.build().writeValueAsString(new ExampleFactoryA()),1);
        Assert.assertNotNull(result);

    }

    @Test
    public void read_read_migration_nested(){
        List<FactoryMigration> migrations = new ArrayList<>();
        migrations.add(new FactoryMigration() {
            @Override
            public boolean canMigrate(int dataModelVersion) {
                return dataModelVersion==1;
            }

            @Override
            public String migrate(String data) {
                return data;
            }

            @Override
            public int migrateResultVersion() {
                return 2;
            }
        });
        migrations.add(new FactoryMigration() {
            @Override
            public boolean canMigrate(int dataModelVersion) {
                return dataModelVersion==2;
            }

            @Override
            public String migrate(String data) {
                return data;
            }

            @Override
            public int migrateResultVersion() {
                return 3;
            }
        });



        JacksonDeSerialisation<ExampleFactoryA> deSerialisation = new JacksonDeSerialisation<>(ExampleFactoryA.class, 3);
        JacksonSerialisation<ExampleFactoryA> serialisation = new JacksonSerialisation<>(3);
        FactorySerialisationManager<ExampleFactoryA> manager = new FactorySerialisationManager<>(serialisation,deSerialisation, migrations);
        ExampleFactoryA result = manager.read(ObjectMapperBuilder.build().writeValueAsString(new ExampleFactoryA()),1);
        Assert.assertNotNull(result);

    }

    @Test(expected = IllegalStateException.class)
    public void read_read__no_migration_found(){
        List<FactoryMigration> migrations = new ArrayList<>();
        migrations.add(new FactoryMigration() {
            @Override
            public boolean canMigrate(int dataModelVersion) {
                return dataModelVersion==1;
            }

            @Override
            public String migrate(String data) {
                return data;
            }

            @Override
            public int migrateResultVersion() {
                return 2;
            }
        });

        JacksonDeSerialisation<ExampleFactoryA> deSerialisation = new JacksonDeSerialisation<>(ExampleFactoryA.class, 1);
        JacksonSerialisation<ExampleFactoryA> serialisation = new JacksonSerialisation<>(1);
        FactorySerialisationManager<ExampleFactoryA> manager = new FactorySerialisationManager<>(serialisation,deSerialisation, Collections.emptyList());
        ExampleFactoryA result = manager.read(ObjectMapperBuilder.build().writeValueAsString(new ExampleFactoryA()),0);
        Assert.assertNotNull(result);

    }


    @Test
    public void read_read_migration_missing_migration(){
        List<FactoryMigration> migrations = new ArrayList<>();
        migrations.add(new FactoryMigration() {
            @Override
            public boolean canMigrate(int dataModelVersion) {
                return dataModelVersion==1;
            }

            @Override
            public String migrate(String data) {
                return data;
            }

            @Override
            public int migrateResultVersion() {
                return 2;
            }
        });



        JacksonDeSerialisation<ExampleFactoryA> deSerialisation = new JacksonDeSerialisation<>(ExampleFactoryA.class, 5);
        JacksonSerialisation<ExampleFactoryA> serialisation = new JacksonSerialisation<>(5);
        FactorySerialisationManager<ExampleFactoryA> manager = new FactorySerialisationManager<>(serialisation,deSerialisation, migrations);
        ExampleFactoryA result = manager.read(ObjectMapperBuilder.build().writeValueAsString(new ExampleFactoryA()),1);
        Assert.assertNotNull(result);

    }
}