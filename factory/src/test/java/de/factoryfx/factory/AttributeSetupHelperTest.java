package de.factoryfx.factory;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.storage.JacksonDeSerialisation;
import de.factoryfx.factory.builder.FactoryTreeBuilder;
import de.factoryfx.factory.builder.Scope;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleFactoryB;
import org.junit.Assert;
import org.junit.Test;

public class AttributeSetupHelperTest {


    @Test
    public void test_use_in_DeSerialisation() {

        FactoryTreeBuilder<ExampleFactoryA> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class);
        builder.addFactory(ExampleFactoryA.class, Scope.SINGLETON, context -> {
            ExampleFactoryA factoryBases = new ExampleFactoryA();
            factoryBases.referenceAttribute.set(context.get(ExampleFactoryB.class));
            return factoryBases;
        });
        builder.addFactory(ExampleFactoryB.class, Scope.PROTOTYPE, context -> {
            ExampleFactoryB factoryBases = new ExampleFactoryB();
            return factoryBases;
        });


        ExampleFactoryA root = builder.buildTreeUnvalidated();


        {//without AttributeSetupHelper
            JacksonDeSerialisation<ExampleFactoryA, Void> jacksonDeSerialisation = new JacksonDeSerialisation<>(ExampleFactoryA.class, 0);
            ExampleFactoryA read = jacksonDeSerialisation.read(ObjectMapperBuilder.build().writeValueAsString(root));

            Assert.assertTrue(read.referenceAttribute.internal_isUserSelectable());
            Assert.assertTrue(read.referenceAttribute.get().referenceAttribute.internal_isUserSelectable());
        }

        {//with AttributeSetupHelper
            JacksonDeSerialisation<ExampleFactoryA, Void> jacksonDeSerialisation = new JacksonDeSerialisation<>(ExampleFactoryA.class, 0, new AttributeSetupHelper<>(builder));
            ExampleFactoryA read = jacksonDeSerialisation.read(ObjectMapperBuilder.build().writeValueAsString(root));

            Assert.assertFalse(read.referenceAttribute.internal_isUserSelectable());
            Assert.assertFalse(read.referenceAttribute.get().referenceAttribute.internal_isUserSelectable());
        }

    }
}