package de.factoryfx.data.storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.testfactories.ExampleDataA;
import org.junit.Assert;
import org.junit.Test;

public class DataSerialisationManagerTest {

    @Test
    public void read_read(){
        JacksonDeSerialisation<ExampleDataA> deSerialisation = new JacksonDeSerialisation<>(ExampleDataA.class, 1);
        JacksonSerialisation<ExampleDataA> serialisation = new JacksonSerialisation<>(1);
        DataSerialisationManager<ExampleDataA> manager = new DataSerialisationManager<>(serialisation,deSerialisation, Collections.emptyList(),1);
        ExampleDataA result = manager.read(ObjectMapperBuilder.build().writeValueAsString(new ExampleDataA()),1);
        Assert.assertNotNull(result);

    }



    @Test
    public void read_read_migration(){
        List<DataMigration> migrations = new ArrayList<>();
        migrations.add(new DataMigration() {
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



        JacksonDeSerialisation<ExampleDataA> deSerialisation = new JacksonDeSerialisation<>(ExampleDataA.class, 2);
        JacksonSerialisation<ExampleDataA> serialisation = new JacksonSerialisation<>(2);
        DataSerialisationManager<ExampleDataA> manager = new DataSerialisationManager<>(serialisation,deSerialisation, migrations,1);
        ExampleDataA result = manager.read(ObjectMapperBuilder.build().writeValueAsString(new ExampleDataA()),1);
        Assert.assertNotNull(result);

    }

    @Test
    public void read_read_migration_nested(){
        List<DataMigration> migrations = new ArrayList<>();
        migrations.add(new DataMigration() {
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
        migrations.add(new DataMigration() {
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



        JacksonDeSerialisation<ExampleDataA> deSerialisation = new JacksonDeSerialisation<>(ExampleDataA.class, 3);
        JacksonSerialisation<ExampleDataA> serialisation = new JacksonSerialisation<>(3);
        DataSerialisationManager<ExampleDataA> manager = new DataSerialisationManager<>(serialisation,deSerialisation, migrations,1);
        ExampleDataA result = manager.read(ObjectMapperBuilder.build().writeValueAsString(new ExampleDataA()),1);
        Assert.assertNotNull(result);

    }

    @Test(expected = IllegalStateException.class)
    public void read_read__no_migration_found(){
        List<DataMigration> migrations = new ArrayList<>();
        migrations.add(new DataMigration() {
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

        JacksonDeSerialisation<ExampleDataA> deSerialisation = new JacksonDeSerialisation<>(ExampleDataA.class, 1);
        JacksonSerialisation<ExampleDataA> serialisation = new JacksonSerialisation<>(1);
        DataSerialisationManager<ExampleDataA> manager = new DataSerialisationManager<>(serialisation,deSerialisation, migrations,1);
        ExampleDataA result = manager.read(ObjectMapperBuilder.build().writeValueAsString(new ExampleDataA()),0);
        Assert.assertNotNull(result);

    }


    public void read_read_migration_missing_migration(){
        List<DataMigration> migrations = new ArrayList<>();
        migrations.add(new DataMigration() {
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
        migrations.add(new NopDataMigration(2,5));


        JacksonDeSerialisation<ExampleDataA> deSerialisation = new JacksonDeSerialisation<>(ExampleDataA.class, 5);
        JacksonSerialisation<ExampleDataA> serialisation = new JacksonSerialisation<>(5);
        DataSerialisationManager<ExampleDataA> manager = new DataSerialisationManager<>(serialisation,deSerialisation, migrations,1);
        ExampleDataA result = manager.read(ObjectMapperBuilder.build().writeValueAsString(new ExampleDataA()),1);
        Assert.assertNotNull(result);

    }

    @Test
    public void read_read_migration_nested_and_dependent(){
        List<DataMigration> migrations = new ArrayList<>();
        migrations.add(new DataMigration() {
            @Override
            public boolean canMigrate(int dataModelVersion) {
                return dataModelVersion==1;
            }

            @Override
            public String migrate(String data) {
                return "1"+data;
            }

            @Override
            public int migrateResultVersion() {
                return 2;
            }
        });
        migrations.add(new DataMigration() {
            @Override
            public boolean canMigrate(int dataModelVersion) {
                return dataModelVersion==2;
            }

            @Override
            public String migrate(String data) {
                if (!data.startsWith("1")){//hack to simulate migration depending on previous migration
                    throw new IllegalStateException();
                }
                return data.substring(1);
            }

            @Override
            public int migrateResultVersion() {
                return 3;
            }
        });



        JacksonDeSerialisation<ExampleDataA> deSerialisation = new JacksonDeSerialisation<>(ExampleDataA.class, 3);
        JacksonSerialisation<ExampleDataA> serialisation = new JacksonSerialisation<>(3);
        DataSerialisationManager<ExampleDataA> manager = new DataSerialisationManager<>(serialisation,deSerialisation, migrations,1);
        ExampleDataA result = manager.read(ObjectMapperBuilder.build().writeValueAsString(new ExampleDataA()),1);
        Assert.assertNotNull(result);

    }
}