package de.factoryfx.data;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.CopySemantic;
import de.factoryfx.data.attribute.ReferenceAttribute;
import de.factoryfx.data.attribute.types.ObjectValueAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.jackson.SimpleObjectMapper;
import de.factoryfx.data.merge.testfactories.ExampleFactoryA;
import de.factoryfx.data.merge.testfactories.ExampleFactoryB;
import de.factoryfx.data.merge.testfactories.ExampleFactoryC;
import de.factoryfx.data.merge.testfactories.IdData;
import de.factoryfx.data.util.LanguageText;
import de.factoryfx.data.validation.AttributeValidation;
import de.factoryfx.data.validation.Validation;
import org.junit.Assert;
import org.junit.Test;

public class DataTest {

    @Test
    public void test_visitAttributes(){
        ExampleFactoryA testModel = new ExampleFactoryA();
        testModel.stringAttribute.set("xxxx");
        testModel.referenceAttribute.set(new ExampleFactoryB());

        ArrayList<String> calls = new ArrayList<>();
        testModel.internal().visitAttributesFlat(attribute -> calls.add(attribute.get().toString()));
        Assert.assertEquals(3,calls.size());
        Assert.assertEquals("xxxx",calls.get(0));
    }

    @Test
    public void test_reconstructMetadataDeepRoot(){
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        ExampleFactoryC exampleFactoryC = new ExampleFactoryC();
        exampleFactoryB.referenceAttributeC.set(exampleFactoryC);
        exampleFactoryA.referenceAttribute.set(exampleFactoryB);

        exampleFactoryA.referenceListAttribute.add(new ExampleFactoryB());

        SimpleObjectMapper mapper = ObjectMapperBuilder.build();
        String string = mapper.writeValueAsString(exampleFactoryA);
        ExampleFactoryA readed = null;
        try {
            readed = mapper.getObjectMapper().readValue(string,ExampleFactoryA.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Assert.assertEquals(null,readed.stringAttribute);
        Assert.assertEquals(null,readed.referenceAttribute.metadata);
        Assert.assertEquals(null,readed.referenceListAttribute.metadata);
        Assert.assertEquals(null,readed.referenceAttribute.get().stringAttribute);
        Assert.assertEquals(null,readed.referenceListAttribute.get(0).stringAttribute);

        readed = readed.internal().prepareUsage();

        Assert.assertEquals("ExampleA1",readed.stringAttribute.metadata.labelText.getPreferred(Locale.ENGLISH));
        Assert.assertEquals("ExampleA2",readed.referenceAttribute.metadata.labelText.getPreferred(Locale.ENGLISH));
        Assert.assertEquals("ExampleA3",readed.referenceListAttribute.metadata.labelText.getPreferred(Locale.ENGLISH));
        Assert.assertEquals("ExampleB1",readed.referenceAttribute.get().stringAttribute.metadata.labelText.getPreferred(Locale.ENGLISH));
        Assert.assertEquals("ExampleB1",readed.referenceListAttribute.get(0).stringAttribute.metadata.labelText.getPreferred(Locale.ENGLISH));

        Assert.assertEquals(exampleFactoryA.getId(),readed.getId());
    }

    public static class ExampleFactoryThis extends IdData {
        ArrayList<String> calls=new ArrayList<>();
        public final StringAttribute stringAttribute= new StringAttribute(new AttributeMetadata().labelText("ExampleA1")).validation(new Validation<String>() {
            @Override
            public LanguageText getValidationDescription() {
                return null;
            }

            @Override
            public boolean validate(String value) {
                calls.add((String) ExampleFactoryThis.this.getId());
                return false;
            }
        });

        public ExampleFactoryThis(){
            System.out.println();        }
    }


    @Test
    public void test_reconstructMetadataDeepRoot_this() throws IOException {
        ExampleFactoryThis exampleFactoryThis = new ExampleFactoryThis();

        SimpleObjectMapper mapper = ObjectMapperBuilder.build();
        String string = mapper.writeValueAsString(exampleFactoryThis);
        ExampleFactoryThis readed = mapper.getObjectMapper().readValue(string,ExampleFactoryThis.class);

        Assert.assertEquals(null,readed.stringAttribute);
        Assert.assertEquals(0,readed.calls.size());

        readed = readed.internal().prepareUsage();

        readed.internal().validateFlat();

        Assert.assertNotNull(readed.stringAttribute);
        Assert.assertEquals(1,readed.calls.size());
        Assert.assertEquals(readed.getId(),readed.calls.get(0));
    }


    @Test
    public void test_reconstructMetadataDeepRoot_displaytext() throws IllegalAccessException, NoSuchFieldException {
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        ExampleFactoryC exampleFactoryC = new ExampleFactoryC();
        exampleFactoryB.referenceAttributeC.set(exampleFactoryC);
        exampleFactoryA.referenceAttribute.set(exampleFactoryB);

        exampleFactoryA.referenceListAttribute.add(new ExampleFactoryB());

        SimpleObjectMapper mapper = ObjectMapperBuilder.build();
        String string = mapper.writeValueAsString(exampleFactoryA);
        ExampleFactoryA readed = null;
        try {
            readed = mapper.getObjectMapper().readValue(string,ExampleFactoryA.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        readed = readed.internal().prepareUsage();


        final Field displayTextDependencies = Data.class.getDeclaredField("displayTextDependencies");
        displayTextDependencies.setAccessible(true);
        final List<Attribute<?>> attributes = (List<Attribute<?>>) displayTextDependencies.get(readed);
        Assert.assertEquals(1, attributes.size());
        Assert.assertEquals(readed.stringAttribute, attributes.get(0));


        final Field dataValidations = Data.class.getDeclaredField("dataValidations");
        displayTextDependencies.setAccessible(true);
        final List<AttributeValidation<?>> dataValidationsAttributes = (List<AttributeValidation<?>>) dataValidations.get(readed);
        Assert.assertEquals(1, dataValidationsAttributes.size());
    }


    @Test
    public void test_getPathTo(){
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        exampleFactoryA.referenceAttribute.set(exampleFactoryB);

        List<Data> pathTo = exampleFactoryA.internal().getPathTo(exampleFactoryB);
        Assert.assertEquals(1,pathTo.size());
        Assert.assertEquals(exampleFactoryA.getId(),pathTo.get(0).getId());
    }


    public static class ExampleObjectProperty extends IdData {
        public final StringAttribute stringAttribute= new StringAttribute(new AttributeMetadata().labelText("stringAttribute"));
        public final ObjectValueAttribute<String> objectValueAttribute= new ObjectValueAttribute<>(new AttributeMetadata().labelText("objectValueAttribute"));
    }

    @Test
    public void test_copyOneLevelDeep(){
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        exampleFactoryA.referenceAttribute.set(exampleFactoryB);
        ExampleFactoryC exampleFactoryC = new ExampleFactoryC();
        exampleFactoryB.referenceAttributeC.set(exampleFactoryC);

        ExampleFactoryB factoryB = new ExampleFactoryB();
        factoryB.referenceAttributeC.set(new ExampleFactoryC());
        exampleFactoryA.referenceListAttribute.add(factoryB);

        Assert.assertNotNull(exampleFactoryA.referenceAttribute.get());
        Assert.assertNotNull(exampleFactoryA.referenceAttribute.get().referenceAttributeC.get());

        ExampleFactoryA copy =  exampleFactoryA.internal().copyOneLevelDeep();

        Assert.assertNotEquals(copy,exampleFactoryA);
        Assert.assertNotNull(copy.referenceAttribute.get());
        Assert.assertNull(copy.referenceAttribute.get().referenceAttributeC.get());
        Assert.assertNull(copy.referenceListAttribute.get(0).referenceAttributeC.get());
    }

    @Test
    public void test_copyOneLevelDeep_doubleref(){
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();

        exampleFactoryA.referenceAttribute.set(exampleFactoryB);
        exampleFactoryA.referenceListAttribute.add(exampleFactoryB);

        ExampleFactoryA copy =  exampleFactoryA.internal().copyOneLevelDeep();

        Assert.assertEquals(copy.referenceAttribute.get(),copy.referenceListAttribute.get().get(0));
    }

    @Test
    public void test_copy(){
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        exampleFactoryB.stringAttribute.set("dfssfdsfdsfd");

        exampleFactoryA.referenceAttribute.set(exampleFactoryB);
        exampleFactoryA.referenceListAttribute.add(new ExampleFactoryB());

        ExampleFactoryA copy =  exampleFactoryA.internal().copy();

        SimpleObjectMapper mapper = ObjectMapperBuilder.build();
        String expected = mapper.writeValueAsString(mapper.copy(exampleFactoryA));
        String actual = mapper.writeValueAsString(copy);

        Assert.assertEquals(expected, actual);
    }

    public static class ExampleWithDefaultParent extends IdData {
        public final ReferenceAttribute<ExampleWithDefault> referenceAttribute = new ReferenceAttribute<>(ExampleWithDefault.class,new AttributeMetadata().labelText("ExampleA2"));
    }

    public static class ExampleWithDefault extends IdData {
        public final ReferenceAttribute<ExampleFactoryB> referenceAttribute = new ReferenceAttribute<>(ExampleFactoryB.class,new AttributeMetadata().labelText("ExampleA2")).defaultValue(new ExampleFactoryB());
    }
    @Test
    public void test_editing_nested_add(){
        ExampleWithDefaultParent exampleWithDefaultParent = new ExampleWithDefaultParent();
        exampleWithDefaultParent.internal().prepareUsage();
        Assert.assertTrue(exampleWithDefaultParent.internal().readyForUsage());

        exampleWithDefaultParent.referenceAttribute.addNewFactory();

        Assert.assertTrue(exampleWithDefaultParent.internal().readyForUsage());
        Assert.assertTrue(exampleWithDefaultParent.referenceAttribute.get().internal().readyForUsage());
        Assert.assertTrue(exampleWithDefaultParent.referenceAttribute.get().referenceAttribute.get().internal().readyForUsage());
    }


    @Test
    public void test_zero_copy(){
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleFactoryA.stringAttribute.set("dfssfdsfdsfd");
        exampleFactoryA.referenceAttribute.set(new ExampleFactoryB());
        exampleFactoryA.referenceListAttribute.add(new ExampleFactoryB());

        ExampleFactoryA copy =  exampleFactoryA.internal().copyZeroLevelDeep();


        Assert.assertEquals("dfssfdsfdsfd", copy.stringAttribute.get());
        Assert.assertEquals(null, copy.referenceAttribute.get());
        Assert.assertTrue(copy.referenceListAttribute.get().isEmpty());
    }

    private class ExampleFactoryObservable extends IdData {
        public final StringAttribute stringAttribute= new StringAttribute(new AttributeMetadata().labelText("ExampleA1"));
        public ExampleFactoryObservable(){
            config().setDisplayTextProvider(() -> stringAttribute.get());
            config().setDisplayTextDependencies(stringAttribute);
        }
    }

    @Test
    public void test_displaytext_observable(){
        ExampleFactoryObservable exampleFactory = new ExampleFactoryObservable();
        exampleFactory.stringAttribute.set("1");

        Assert.assertEquals("stable ref",exampleFactory.internal().getDisplayTextObservable(),exampleFactory.internal().getDisplayTextObservable());

        Assert.assertEquals("1",exampleFactory.internal().getDisplayTextObservable().get());
        exampleFactory.stringAttribute.set("2");
        Assert.assertEquals("2",exampleFactory.internal().getDisplayTextObservable().get());
    }

    @Test
    public void test_copy_objectProperty(){
        ExampleObjectProperty exampleObjectProperty = new ExampleObjectProperty();
        final String test = "Test";
        exampleObjectProperty.objectValueAttribute.set(test);
        ExampleObjectProperty copy = exampleObjectProperty.internal().copy();
        Assert.assertTrue(copy.objectValueAttribute.get()==test);
    }


    @Test
    public void test_copy_semantic_self(){
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleFactoryA.stringAttribute.set("dfssfdsfdsfd");
        exampleFactoryA.referenceAttribute.set(new ExampleFactoryB());
        exampleFactoryA.referenceListAttribute.add(new ExampleFactoryB());

        ExampleFactoryA copy =  exampleFactoryA.utility().semanticCopy();

        Assert.assertEquals(exampleFactoryA.stringAttribute.get(), copy.stringAttribute.get());
        Assert.assertEquals(exampleFactoryA.referenceAttribute.get(), copy.referenceAttribute.get());
        Assert.assertEquals(exampleFactoryA.referenceListAttribute.get(), copy.referenceListAttribute.get());
    }

    @Test
    public void test_copy_semantic_copy(){
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleFactoryA.stringAttribute.set("dfssfdsfdsfd");
        exampleFactoryA.referenceAttribute.set(new ExampleFactoryB());
        exampleFactoryA.referenceListAttribute.add(new ExampleFactoryB());

        exampleFactoryA.referenceAttribute.setCopySemantic(CopySemantic.COPY);
        exampleFactoryA.referenceListAttribute.setCopySemantic(CopySemantic.COPY);

        ExampleFactoryA copy =  exampleFactoryA.utility().semanticCopy();

        Assert.assertEquals(exampleFactoryA.stringAttribute.get(), copy.stringAttribute.get());
        Assert.assertNotEquals(exampleFactoryA.referenceAttribute.get(), copy.referenceAttribute.get());
        Assert.assertNotEquals(exampleFactoryA.referenceAttribute.get().getId(), copy.referenceAttribute.get().getId());
        Assert.assertNotEquals(exampleFactoryA.referenceListAttribute.get(), copy.referenceListAttribute.get());
        Assert.assertNotEquals(exampleFactoryA.referenceAttribute.get().getId(), copy.referenceAttribute.get().getId());
    }

}