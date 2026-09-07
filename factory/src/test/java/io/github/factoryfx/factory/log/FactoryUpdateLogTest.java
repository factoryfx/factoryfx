package io.github.factoryfx.factory.log;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.merge.DataMerger;
import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class FactoryUpdateLogTest {

    @Test
    public void test_json(){
        DataMerger<ExampleFactoryA> dataMerger = new DataMerger<>(new ExampleFactoryA(),new ExampleFactoryA(),new ExampleFactoryA());

        FactoryUpdateLog<ExampleFactoryA> factoryUpdateLog = new FactoryUpdateLog<>("log",
                        dataMerger.mergeIntoCurrent(p->true),0,null);
        ObjectMapperBuilder.build().copy(factoryUpdateLog);
    }

    @Test
    public void test_json_validationErrors(){
        FactoryUpdateLog<ExampleFactoryA> factoryUpdateLog = FactoryUpdateLog.validationFailed(List.of("error1","error2"));
        Assertions.assertTrue(factoryUpdateLog.failedValidation());
        Assertions.assertFalse(factoryUpdateLog.failedUpdate());
        Assertions.assertFalse(factoryUpdateLog.successfullyMerged());

        FactoryUpdateLog<ExampleFactoryA> copy = ObjectMapperBuilder.build().copy(factoryUpdateLog);
        Assertions.assertEquals(List.of("error1","error2"), copy.validationErrors);
    }

    @Test
    public void test_json_withoutValidationErrorsField_yieldsEmptyList(){
        DataMerger<ExampleFactoryA> dataMerger = new DataMerger<>(new ExampleFactoryA(),new ExampleFactoryA(),new ExampleFactoryA());
        FactoryUpdateLog<ExampleFactoryA> factoryUpdateLog = new FactoryUpdateLog<>("log",
                dataMerger.mergeIntoCurrent(p->true),0,null);

        ObjectNode json = (ObjectNode) ObjectMapperBuilder.build().valueToTree(factoryUpdateLog);
        json.remove("validationErrors");//payload from a peer without the field

        FactoryUpdateLog<?> copy = ObjectMapperBuilder.build().treeToValue(json, FactoryUpdateLog.class);
        Assertions.assertTrue(copy.validationErrors.isEmpty());
    }

}