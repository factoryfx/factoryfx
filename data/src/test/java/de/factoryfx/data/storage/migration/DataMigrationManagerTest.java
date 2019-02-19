package de.factoryfx.data.storage.migration;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.testdata.ExampleDataA;
import de.factoryfx.data.merge.testdata.ExampleDataB;
import de.factoryfx.data.merge.testdata.ExampleDataC;
import de.factoryfx.data.storage.migration.datamigration.DataJsonNode;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class DataMigrationManagerTest {

    @Test
    public void test_read_list(){
        ExampleDataA root= new ExampleDataA();
        ExampleDataB exampleDataB = new ExampleDataB();
        exampleDataB.referenceAttributeC.set(new ExampleDataC());
        root.referenceListAttribute.add(exampleDataB);

        DataMigrationManager dataMigrationManager = new DataMigrationManager();
        List<DataJsonNode> jsonNodes = dataMigrationManager.readDataList(ObjectMapperBuilder.build().readTree(ObjectMapperBuilder.build().writeValueAsString(root)));
        Assert.assertEquals(3,jsonNodes.size());
        Assert.assertEquals(ExampleDataA.class.getName(),jsonNodes.get(0).getDataClassName());
        Assert.assertEquals(ExampleDataB.class.getName(),jsonNodes.get(1).getDataClassName());
        Assert.assertEquals(ExampleDataC.class.getName(),jsonNodes.get(2).getDataClassName());
    }

    @Test
    public void test_read_list_more_data(){
        ExampleDataA root= new ExampleDataA();
        ExampleDataB exampleDataB = new ExampleDataB();
        exampleDataB.referenceAttributeC.set(new ExampleDataC());
        root.referenceListAttribute.add(exampleDataB);
        root.referenceListAttribute.add(new ExampleDataB());
        root.referenceListAttribute.add(new ExampleDataB());

        DataMigrationManager dataMigrationManager = new DataMigrationManager();
        List<DataJsonNode> jsonNodes = dataMigrationManager.readDataList(ObjectMapperBuilder.build().readTree(ObjectMapperBuilder.build().writeValueAsString(root)));
        Assert.assertEquals(5,jsonNodes.size());
    }

    @Test
    public void test_read_list_ref(){
        ExampleDataA root= new ExampleDataA();
        ExampleDataB exampleDataB = new ExampleDataB();
        exampleDataB.referenceAttributeC.set(new ExampleDataC());
        root.referenceListAttribute.add(exampleDataB);
        root.referenceAttribute.set(exampleDataB);

        DataMigrationManager dataMigrationManager = new DataMigrationManager();
        List<DataJsonNode> jsonNodes = dataMigrationManager.readDataList(ObjectMapperBuilder.build().readTree(ObjectMapperBuilder.build().writeValueAsString(root)));
        Assert.assertEquals(3,jsonNodes.size());
    }

}