package de.factoryfx.data.storage.migration.datamigration;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.testdata.ExampleDataA;
import de.factoryfx.data.merge.testdata.ExampleDataB;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SingletonDataRestoreTest {

    @Test
    public void test() {

        ExampleDataA exampleDataA = new ExampleDataA();
        ExampleDataB value = new ExampleDataB();
        value.stringAttribute.set("1234");
        exampleDataA.referenceAttribute.set(value);

        String input = ObjectMapperBuilder.build().writeValueAsString(exampleDataA);
        input = input.replace(
                "stringAttribute",
                "oldStringAttribute");
        System.out.println(input);



        SingletonDataRestore<ExampleDataA,String> singletonAttributeMove =
                new SingletonDataRestore<>("de.factoryfx.data.merge.testdata.ExampleDataB","oldStringAttribute",String.class,(r, v)->{
                    r.referenceAttribute.get().stringAttribute.set(v);
                } );


        ExampleDataA exampleDataAMigrated = new ExampleDataA();
        ExampleDataB valueMigrated  = new ExampleDataB();
        valueMigrated.stringAttribute.set("");
        exampleDataAMigrated.referenceAttribute.set(valueMigrated);

        singletonAttributeMove.migrate(new JsonDataUtility().readDataList(ObjectMapperBuilder.build().readTree(input)),exampleDataAMigrated);


        Assertions.assertEquals("1234",exampleDataAMigrated.referenceAttribute.get().stringAttribute.get());
    }

}
