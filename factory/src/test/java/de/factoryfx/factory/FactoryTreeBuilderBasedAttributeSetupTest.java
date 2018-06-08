package de.factoryfx.factory;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.storage.JacksonDeSerialisation;
import de.factoryfx.factory.builder.FactoryTreeBuilder;
import de.factoryfx.factory.builder.Scope;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleFactoryB;
import org.junit.Assert;
import org.junit.Test;

import java.util.function.Consumer;

public class FactoryTreeBuilderBasedAttributeSetupTest {


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

        FactoryTreeBuilderBasedAttributeSetup<ExampleFactoryA> factoryTreeBuilderBasedAttributeSetup = new FactoryTreeBuilderBasedAttributeSetup<>(builder);

        root.internal().collectChildrenDeep().forEach(data -> {
            data.internal().visitAttributesFlat((attributeVariableName, attribute) -> factoryTreeBuilderBasedAttributeSetup.accept(attribute));
        });


        Assert.assertFalse(root.referenceAttribute.internal_isUserSelectable());
        Assert.assertFalse(root.referenceAttribute.get().referenceAttribute.internal_isUserSelectable());

    }
}