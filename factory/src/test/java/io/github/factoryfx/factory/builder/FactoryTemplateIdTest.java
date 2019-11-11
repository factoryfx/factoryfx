package io.github.factoryfx.factory.builder;

import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class FactoryTemplateIdTest {

    @Test
    public void test_templateIdPersistence_1_1(){
        FactoryTemplateId<ExampleFactoryA> templateId = new FactoryTemplateId<>("test", ExampleFactoryA.class);
        ExampleFactoryA factory = new ExampleFactoryA();
        templateId.serializeTo(factory);

        FactoryTemplateId<ExampleFactoryA> reRead = new FactoryTemplateId<>(factory);

        Assertions.assertEquals("test",reRead.name);
        Assertions.assertEquals(ExampleFactoryA.class,reRead.clazz);
    }

    @Test
    public void test_templateIdPersistence_0_1(){
        FactoryTemplateId<ExampleFactoryA> templateId = new FactoryTemplateId<>(null, ExampleFactoryA.class);
        ExampleFactoryA factory = new ExampleFactoryA();
        templateId.serializeTo(factory);

        FactoryTemplateId<ExampleFactoryA> reRead = new FactoryTemplateId<>(factory);

        Assertions.assertEquals(null,reRead.name);
        Assertions.assertEquals(ExampleFactoryA.class,reRead.clazz);
    }

    @Test
    public void test_templateIdPersistence_1_0(){
        FactoryTemplateId<ExampleFactoryA> templateId = new FactoryTemplateId<>("test", null);
        ExampleFactoryA factory = new ExampleFactoryA();
        templateId.serializeTo(factory);

        FactoryTemplateId<ExampleFactoryA> reRead = new FactoryTemplateId<>(factory);

        Assertions.assertEquals("test",reRead.name);
        Assertions.assertEquals(null,reRead.clazz);
    }

    @Test
    public void test_templateIdPersistence_0_0(){
        Assertions.assertThrows(IllegalArgumentException.class,()-> {
            new FactoryTemplateId<>((String)null, null);
        });

    }
}