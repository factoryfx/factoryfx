package de.factoryfx.data;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.factoryfx.data.attribute.*;
import de.factoryfx.data.attribute.types.ObjectValueAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.jackson.SimpleObjectMapper;
import de.factoryfx.data.merge.DataMerger;
import de.factoryfx.data.merge.testdata.ExampleDataA;
import de.factoryfx.data.merge.testdata.ExampleDataB;
import de.factoryfx.data.merge.testdata.ExampleDataC;
import de.factoryfx.data.util.LanguageText;
import de.factoryfx.data.validation.AttributeValidation;
import de.factoryfx.data.validation.ValidationResult;
import org.junit.Assert;
import org.junit.Test;

public class DataTest {

    @Test
    public void test_visitAttributes(){
        ExampleDataA testModel = new ExampleDataA();
        testModel.stringAttribute.set("xxxx");
        testModel.referenceAttribute.set(new ExampleDataB());

        ArrayList<String> calls = new ArrayList<>();
        testModel.internal().visitAttributesFlat((attributeVariableName, attribute) -> calls.add(attribute.get().toString()));
        Assert.assertEquals(3,calls.size());
        Assert.assertEquals("xxxx",calls.get(0));
    }


    public static class ExampleFactoryThis extends Data {
        ArrayList<String> calls=new ArrayList<>();
        public final StringAttribute stringAttribute= new StringAttribute().labelText("ExampleA1").validation(value -> {
            calls.add(ExampleFactoryThis.this.getId());
            return new ValidationResult(false,new LanguageText());
        });

        public ExampleFactoryThis(){
            System.out.println();        }
    }


    @Test
    public void test_reconstructMetadataDeepRoot_this() throws IOException {
        ExampleFactoryThis exampleFactoryThis = new ExampleFactoryThis();

        SimpleObjectMapper mapper = ObjectMapperBuilder.build();
        String string = mapper.writeValueAsString(exampleFactoryThis);
        ExampleFactoryThis readed = ObjectMapperBuilder.buildNewObjectMapper().readValue(string,ExampleFactoryThis.class);

//        Assert.assertEquals(null,readed.stringAttribute);
        Assert.assertEquals(0,readed.calls.size());

        readed = readed.internal().addBackReferences();

        readed.internal().validateFlat();

        Assert.assertNotNull(readed.stringAttribute);
        Assert.assertEquals(1,readed.calls.size());
        Assert.assertEquals(readed.getId(),readed.calls.get(0));
    }


    @Test
    @SuppressWarnings("unchecked")
    public void test_reconstructMetadataDeepRoot_displaytext() throws Exception {
        ExampleDataA exampleFactoryA = new ExampleDataA();
        ExampleDataB exampleFactoryB = new ExampleDataB();
        ExampleDataC exampleFactoryC = new ExampleDataC();
        exampleFactoryB.referenceAttributeC.set(exampleFactoryC);
        exampleFactoryA.referenceAttribute.set(exampleFactoryB);

        exampleFactoryA.referenceListAttribute.add(new ExampleDataB());

        SimpleObjectMapper mapper = ObjectMapperBuilder.build();
        String string = mapper.writeValueAsString(exampleFactoryA);
        ExampleDataA readed = ObjectMapperBuilder.buildNewObjectMapper().readValue(string,ExampleDataA.class);

        readed = readed.internal().addBackReferences();


        final Field displayTextDependencies = Data.class.getDeclaredField("displayTextDependencies");
        displayTextDependencies.setAccessible(true);
        final List<Attribute<?,?>> attributes = (List<Attribute<?,?>>) displayTextDependencies.get(readed);
        Assert.assertEquals(1, attributes.size());
        Assert.assertEquals(readed.stringAttribute, attributes.get(0));


        final Field dataValidations = Data.class.getDeclaredField("dataValidations");
        dataValidations.setAccessible(true);
        displayTextDependencies.setAccessible(true);
        final List<AttributeValidation<?>> dataValidationsAttributes = (List<AttributeValidation<?>>) dataValidations.get(readed);
        Assert.assertEquals(1, dataValidationsAttributes.size());
    }


    @Test
    public void test_getPathFromRoot(){
        ExampleDataA root = new ExampleDataA();
        ExampleDataB exampleFactoryB = new ExampleDataB();
        root.referenceAttribute.set(exampleFactoryB);

        root.internal().addBackReferences();
        Assert.assertNotNull(root.internal().getRoot());

        List<Data> pathTo = root.referenceAttribute.get().internal().getPathFromRoot();
        Assert.assertEquals(1,pathTo.size());
        Assert.assertEquals(root.getId(),pathTo.get(0).getId());
    }


    public static class ExampleObjectProperty extends Data {
        public final StringAttribute stringAttribute= new StringAttribute().labelText("stringAttribute");
        public final ObjectValueAttribute<String> objectValueAttribute= new ObjectValueAttribute<String>().labelText("objectValueAttribute");
    }

    @Test
    public void test_copy_width_cycle(){
        ExampleDataA exampleFactoryA = new ExampleDataA();
        ExampleDataB exampleFactoryB = new ExampleDataB();

        exampleFactoryA.referenceAttribute.set(exampleFactoryB);
        exampleFactoryB.referenceAttribute.set(exampleFactoryA);

        ExampleDataA copy =  exampleFactoryA.internal().copy();

        //Assert no Stackoverflow
    }

    @Test
    public void test_copyOneLevelDeep(){
        ExampleDataA exampleFactoryA = new ExampleDataA();
        ExampleDataB exampleFactoryB = new ExampleDataB();
        exampleFactoryA.referenceAttribute.set(exampleFactoryB);
        ExampleDataC exampleFactoryC = new ExampleDataC();
        exampleFactoryB.referenceAttributeC.set(exampleFactoryC);

        ExampleDataB factoryB = new ExampleDataB();
        factoryB.referenceAttributeC.set(new ExampleDataC());
        exampleFactoryA.referenceListAttribute.add(factoryB);

        Assert.assertNotNull(exampleFactoryA.referenceAttribute.get());
        Assert.assertNotNull(exampleFactoryA.referenceAttribute.get().referenceAttributeC.get());

        ExampleDataA copy =  exampleFactoryA.internal().copyOneLevelDeep();

        Assert.assertNotEquals(copy,exampleFactoryA);
        Assert.assertNotNull(copy.referenceAttribute.get());
        Assert.assertNull(copy.referenceAttribute.get().referenceAttributeC.get());
        Assert.assertNull(copy.referenceListAttribute.get(0).referenceAttributeC.get());
    }

    @Test
    public void test_copyOneLevelDeep_doubleref(){
        ExampleDataA exampleFactoryA = new ExampleDataA();
        ExampleDataB exampleFactoryB = new ExampleDataB();

        exampleFactoryA.referenceAttribute.set(exampleFactoryB);
        exampleFactoryA.referenceListAttribute.add(exampleFactoryB);

        ExampleDataA copy =  exampleFactoryA.internal().copyOneLevelDeep();

        Assert.assertEquals(copy.referenceAttribute.get(),copy.referenceListAttribute.get().get(0));
    }

    private static class EmptyExampleDataA extends Data {

    }

    @Test
    public void test_copy_consumer_root_empty_data(){
        EmptyExampleDataA emptyExampleDataA = new EmptyExampleDataA();
        EmptyExampleDataA copy =  emptyExampleDataA.internal().addBackReferences();
        Assert.assertEquals(copy,copy.internal().getRoot());
    }


    @Test
    public void test_copy(){
        ExampleDataA exampleFactoryA = new ExampleDataA();
        ExampleDataB exampleFactoryB = new ExampleDataB();
        exampleFactoryB.stringAttribute.set("dfssfdsfdsfd");

        exampleFactoryA.referenceAttribute.set(exampleFactoryB);
        exampleFactoryA.referenceListAttribute.add(new ExampleDataB());

        exampleFactoryA = exampleFactoryA.internal().addBackReferences();

        ExampleDataA copy =  exampleFactoryA.internal().copy();

        SimpleObjectMapper mapper = ObjectMapperBuilder.build();
        System.out.println(mapper.writeValueAsString(exampleFactoryA));

        final Data expectedData = mapper.copy(exampleFactoryA);
        String expected = mapper.writeValueAsString(expectedData);
        String actual = mapper.writeValueAsString(copy);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test_copy_root_after_jackson(){
        ExampleDataA exampleFactoryA = new ExampleDataA();
        ExampleDataB exampleFactoryB = new ExampleDataB();
        exampleFactoryB.stringAttribute.set("dfssfdsfdsfd");

        exampleFactoryA.referenceAttribute.set(null);
        exampleFactoryA.referenceListAttribute.add(new ExampleDataB());
        exampleFactoryA = exampleFactoryA.internal().addBackReferences();


        ExampleDataA copy  =ObjectMapperBuilder.build().copy(exampleFactoryA);
        //also include jackson cause into copy test,(strange jackson behaviour for final fields)
        //ObjectMapperBuilder internally call internal().copy

        Assert.assertEquals(copy, copy.internal().getRoot());
        copy.internal().visitAttributesFlat((attributeVariableName, attribute) -> {
            Field root = getField(attribute.getClass(),"root");
            if (root!=null){
                try {
                    root.setAccessible(true);
                    Assert.assertEquals(copy, root.get(attribute));
                } catch ( IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }

    private static Field getField(Class<?> cls, String fieldName) {
        for (Class<?> c = cls; c != null; c = c.getSuperclass()) {
            try {
                final Field field = c.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field;
            } catch (Exception e) {
                //do nothing
            }
        }
        return null;
    }

    public static class ExampleWithDefaultParent extends Data {
        public final DataReferenceAttribute<ExampleWithDefault> referenceAttribute = new DataReferenceAttribute<>(ExampleWithDefault.class).labelText("ExampleA2");
    }

    public static class ExampleWithDefault extends Data {
        public final DataReferenceAttribute<ExampleDataB> referenceAttribute = new DataReferenceAttribute<>(ExampleDataB.class).labelText("ExampleA2").defaultValue(new ExampleDataB());
    }
    @Test
    public void test_editing_nested_add(){
        ExampleWithDefaultParent exampleWithDefaultParent = new ExampleWithDefaultParent();
        exampleWithDefaultParent.internal().addBackReferences();

        Assert.assertTrue(exampleWithDefaultParent.internal().readyForUsage());
        exampleWithDefaultParent.referenceAttribute.get();

        exampleWithDefaultParent.referenceAttribute.set(new ExampleWithDefault());

        Assert.assertTrue(exampleWithDefaultParent.internal().readyForUsage());
        Assert.assertTrue(exampleWithDefaultParent.referenceAttribute.get().internal().readyForUsage());
        Assert.assertTrue(exampleWithDefaultParent.referenceAttribute.get().referenceAttribute.get().internal().readyForUsage());
    }


    @Test
    public void test_zero_copy(){
        ExampleDataA exampleFactoryA = new ExampleDataA();
        exampleFactoryA.stringAttribute.set("dfssfdsfdsfd");
        exampleFactoryA.referenceAttribute.set(new ExampleDataB());
        exampleFactoryA.referenceListAttribute.add(new ExampleDataB());

        ExampleDataA copy =  exampleFactoryA.internal().copyZeroLevelDeep();


        Assert.assertEquals("dfssfdsfdsfd", copy.stringAttribute.get());
        Assert.assertEquals(null, copy.referenceAttribute.get());
        Assert.assertTrue(copy.referenceListAttribute.get().isEmpty());
    }

    @Test
    public void test_copy_objectProperty(){
        ExampleObjectProperty exampleObjectProperty = new ExampleObjectProperty();
        final String test = "Test";
        exampleObjectProperty.objectValueAttribute.set(test);
        exampleObjectProperty=exampleObjectProperty.internal().addBackReferences();
        ExampleObjectProperty copy = exampleObjectProperty.internal().copy();
        //that comparision ist correct (no equals)
        Assert.assertTrue(copy.objectValueAttribute.get()==test);
    }


    @Test
    public void test_copy_semantic_self(){
        ExampleDataA exampleFactoryA = new ExampleDataA();
        exampleFactoryA.stringAttribute.set("dfssfdsfdsfd");
        exampleFactoryA.referenceAttribute.set(new ExampleDataB());
        exampleFactoryA.referenceListAttribute.add(new ExampleDataB());

        ExampleDataA copy =  exampleFactoryA.utility().semanticCopy();

        Assert.assertEquals(exampleFactoryA.stringAttribute.get(), copy.stringAttribute.get());
        Assert.assertEquals(exampleFactoryA.referenceAttribute.get(), copy.referenceAttribute.get());
        Assert.assertEquals(exampleFactoryA.referenceListAttribute.get(0), copy.referenceListAttribute.get(0));
    }

    @Test
    public void test_copy_semantic_copy(){
        ExampleDataA exampleFactoryA = new ExampleDataA();
        exampleFactoryA.stringAttribute.set("dfssfdsfdsfd");
        exampleFactoryA.referenceAttribute.set(new ExampleDataB());
        exampleFactoryA.referenceListAttribute.add(new ExampleDataB());

        exampleFactoryA.referenceAttribute.setCopySemantic(CopySemantic.COPY);
        exampleFactoryA.referenceListAttribute.setCopySemantic(CopySemantic.COPY);

        ExampleDataA copy =  exampleFactoryA.utility().semanticCopy();

        Assert.assertEquals(exampleFactoryA.stringAttribute.get(), copy.stringAttribute.get());
        Assert.assertNotEquals(exampleFactoryA.referenceAttribute.get(), copy.referenceAttribute.get());
        Assert.assertNotEquals(exampleFactoryA.referenceAttribute.get().getId(), copy.referenceAttribute.get().getId());
        Assert.assertNotEquals(exampleFactoryA.referenceListAttribute.get(), copy.referenceListAttribute.get());
        Assert.assertNotEquals(exampleFactoryA.referenceAttribute.get().getId(), copy.referenceAttribute.get().getId());
    }

    @Test
    public void test_copy_semantic_copy_json_id_unique(){
        ExampleDataA exampleFactoryA = new ExampleDataA();
        exampleFactoryA.referenceListAttribute.add(new ExampleDataB());

        exampleFactoryA.referenceAttribute.setCopySemantic(CopySemantic.COPY);
        exampleFactoryA.referenceListAttribute.setCopySemantic(CopySemantic.COPY);

        ExampleDataB copy =  exampleFactoryA.referenceListAttribute.get(0).utility().semanticCopy();
        exampleFactoryA.referenceListAttribute.add(copy);

        ObjectMapperBuilder.build().copy(exampleFactoryA);
    }

    @Test
    public void test_copy_semantic_copy_json_id_unique_2(){
        ExampleDataA factoryA = new ExampleDataA();
        final ExampleDataB factoryB = new ExampleDataB();
        factoryB.referenceAttributeC.set(new ExampleDataC());
        factoryA.referenceListAttribute.add(factoryB);

        factoryB.referenceAttributeC.setCopySemantic(CopySemantic.SELF);
        factoryA.referenceAttribute.setCopySemantic(CopySemantic.SELF);
        factoryA.referenceListAttribute.setCopySemantic(CopySemantic.SELF);

        ExampleDataB copy =  factoryA.referenceListAttribute.get(0).utility().semanticCopy();
        factoryA.referenceListAttribute.add(copy);

        Assert.assertEquals(2, factoryA.referenceListAttribute.size());
        ObjectMapperBuilder.build().copy(factoryA);
    }

    @Test
    public void test_parent_navigation(){
        ExampleDataA dataA = new ExampleDataA();
        ExampleDataB dataB = new ExampleDataB();
        ExampleDataC dataC = new ExampleDataC();

        dataA.referenceAttribute.set(dataB);
        dataB.referenceAttributeC.set(dataC);

        dataA = dataA.internal().addBackReferences();
        dataB = dataA.referenceAttribute.get();
        dataC = dataB.referenceAttributeC.get();

        Assert.assertEquals(dataB,dataC.internal().getParents().iterator().next());
        Assert.assertEquals(dataA,dataB.internal().getParents().iterator().next());

    }


    public static class ExampleDataCustomId extends Data {
        public final StringAttribute stringAttribute= new StringAttribute().labelText("ExampleA1");

        public ExampleDataCustomId(){
            config().attributeId(() -> stringAttribute.get());
        }

    }

    @Test
    public void test_customId(){
        ExampleDataCustomId exampleDataCustomId = new ExampleDataCustomId();
        exampleDataCustomId.stringAttribute.set("blabla");
        Assert.assertEquals("blabla",exampleDataCustomId.getId());
    }

    @Test
    public void test_customId_after_json(){
        ExampleDataCustomId exampleDataCustomId = new ExampleDataCustomId();
        exampleDataCustomId.stringAttribute.set("blabla");
        ExampleDataCustomId copy = ObjectMapperBuilder.build().copy(exampleDataCustomId);
        Assert.assertEquals("blabla",copy.getId());
    }

    public static class ExampleWithId extends Data {
        public final StringAttribute id = new StringAttribute();
    }

    @Test
    public void test_json_and_id_attributename(){
        ExampleWithId exampleWithId = new ExampleWithId();
        exampleWithId.id.set("blabla");
        ExampleWithId copy = ObjectMapperBuilder.build().copy(exampleWithId);
        Assert.assertNotEquals("blabla",copy.getId());
    }


    public static class ExampleParentsA extends Data {
        public final DataReferenceAttribute<ExampleParentsB> exampleParentsB = new DataReferenceAttribute<>();
        public final DataReferenceAttribute<ExampleParentsC> exampleParentsC = new DataReferenceAttribute<>();
    }

    public static class ExampleParentsB extends Data {
        public final DataReferenceAttribute<ExampleParentsC> exampleParentsC = new DataReferenceAttribute<>();
    }

    public static class ExampleParentsC extends Data {
        public final StringAttribute any = new StringAttribute();
    }

    @Test
    public void test_multiple_parents(){
        ExampleParentsA exampleParentsA = new ExampleParentsA();
        ExampleParentsB exampleParentsB = new ExampleParentsB();
        exampleParentsA.exampleParentsB.set(exampleParentsB);

        ExampleParentsC exampleParentsC = new ExampleParentsC();
        exampleParentsB.exampleParentsC.set(exampleParentsC);
        exampleParentsA.exampleParentsC.set(exampleParentsC);

        //Assert.assertEquals(2,exampleParentsC.internal().getParents().size());
        exampleParentsA = exampleParentsA.internal().addBackReferences();
        Assert.assertEquals(2,exampleParentsA.exampleParentsB.get().exampleParentsC.get().internal().getParents().size());
    }


    @Test
    public void test_parents_list_nested(){
        ExampleDataA exampleDataA = new ExampleDataA();
        ExampleDataB exampleDataB = new ExampleDataB();
        exampleDataB.referenceAttributeC.set(new ExampleDataC());
        exampleDataA.referenceListAttribute.add(exampleDataB);

        exampleDataA = exampleDataA.internal().addBackReferences();
        Assert.assertEquals(1,exampleDataA.referenceListAttribute.get(0).internal().getParents().size());
        Assert.assertEquals(1,exampleDataA.referenceListAttribute.get(0).referenceAttributeC.get().internal().getParents().size());
    }

    @Test
    public void test_addBackReferencese(){
        ExampleDataA example= new ExampleDataA();

        ExampleDataB exampleDataB = new ExampleDataB();
        exampleDataB.referenceAttributeC.set(new ExampleDataC());
        example.referenceListAttribute.add(exampleDataB);

        example.internal().addBackReferences();

        Assert.assertEquals(1,example.referenceListAttribute.get(0).internal().getParents().size());
        Assert.assertEquals(1,example.referenceListAttribute.get(0).referenceAttributeC.get().internal().getParents().size());
    }

    @Test
    public void test_parents_list_nested_merge(){
        ExampleDataA current= new ExampleDataA();
        current = current.internal().addBackReferences();

        ExampleDataA update = current.internal().copy();

        ExampleDataB exampleDataB = new ExampleDataB();
        exampleDataB.referenceAttributeC.set(new ExampleDataC());
        update.referenceListAttribute.add(exampleDataB);


        new DataMerger<>(current,current.utility().copy(),update).mergeIntoCurrent((p)->true);

        Assert.assertEquals(1,current.referenceListAttribute.get(0).internal().getParents().size());
        Assert.assertEquals(1,current.referenceListAttribute.get(0).referenceAttributeC.get().internal().getParents().size());


    }

    @Test
    public void test_json_null_attribute() throws IOException {
        ExampleDataA exampleDataA = new ExampleDataA();
//        exampleDataA.stringAttribute.set("adsa");
        exampleDataA.stringAttribute.set(null);
        ObjectMapper objectMapper = ObjectMapperBuilder.buildNewObjectMapper();
        String content = objectMapper.writeValueAsString(exampleDataA);
        ExampleDataA copy = objectMapper.readValue(content,ExampleDataA.class);

        System.out.println(content);
        Assert.assertNotNull(copy.stringAttribute);
        Assert.assertNotNull(copy.referenceListAttribute);
        Assert.assertNotNull(copy.referenceAttribute);
    }

    @Test
    public void test_json_metadata() throws IOException {
        ExampleDataA exampleDataA = new ExampleDataA();
        exampleDataA.stringAttribute.set("adad");
        exampleDataA.referenceAttribute.set(new ExampleDataB());


        ObjectMapper objectMapper = ObjectMapperBuilder.buildNewObjectMapper();
        String content = objectMapper.writeValueAsString(exampleDataA);
        System.out.println(content);
        ExampleDataA copy = objectMapper.readValue(content,ExampleDataA.class);

        Assert.assertEquals("ExampleA1",copy.stringAttribute.internal_getPreferredLabelText(Locale.ENGLISH));
        Assert.assertEquals("adad",copy.stringAttribute.get());
        Assert.assertEquals("ExampleA2",copy.referenceAttribute.internal_getPreferredLabelText(Locale.ENGLISH));
        Assert.assertEquals("ExampleA3",copy.referenceListAttribute.internal_getPreferredLabelText(Locale.ENGLISH));
    }

    @Test
    public void test_json_list() throws IOException {
        ExampleDataA exampleDataA = new ExampleDataA();
        exampleDataA.referenceListAttribute.add(new ExampleDataB());
        exampleDataA.referenceListAttribute.add(new ExampleDataB());

        ObjectMapper objectMapper = ObjectMapperBuilder.buildNewObjectMapper();
        String content = objectMapper.writeValueAsString(exampleDataA);
        System.out.println(content);
        ExampleDataA copy = objectMapper.readValue(content, ExampleDataA.class);

        Assert.assertTrue(copy.referenceListAttribute.get(0) instanceof ExampleDataB);
        Assert.assertEquals(2,copy.referenceListAttribute.size());
    }

    @Test
    public void test_json_list_with_ids() throws IOException {
        ExampleDataA exampleDataA = new ExampleDataA();
        ExampleDataB value = new ExampleDataB();
        value.referenceAttribute.set(new ExampleDataA());
        exampleDataA.referenceAttribute.set(value);
        exampleDataA.referenceListAttribute.add(value);
        exampleDataA.referenceListAttribute.add(new ExampleDataB());

        ObjectMapper objectMapper = ObjectMapperBuilder.buildNewObjectMapper();
        String content = objectMapper.writeValueAsString(exampleDataA);
        System.out.println(content);
        ExampleDataA copy = objectMapper.readValue(content,ExampleDataA.class);

        Assert.assertEquals(copy.referenceAttribute.get(),copy.referenceListAttribute.get().get(0));
    }

    @Test
    public void test_unique_ids() {
        HashSet<String> ids=new HashSet<>();
        for (int i=0;i<1000000;i++){
            ExampleDataA exampleDataA = new ExampleDataA();
            String id = exampleDataA.getId();
            if (!ids.add(id)) {
                Assert.fail("doubel id"+id+" count:"+ i);
            }
        }
    }

    @Test
    public void addBackReferences_cycle(){
        ExampleDataA exampleFactoryA = new ExampleDataA();
        ExampleDataB exampleFactoryB = new ExampleDataB();
        exampleFactoryB.referenceAttribute.set(exampleFactoryA);
        exampleFactoryA.referenceAttribute.set(exampleFactoryB);

        exampleFactoryA.internal().addBackReferences();
    }

    @Test
    public void test_visitChildren(){
        ExampleDataA exampleFactoryA = new ExampleDataA();
        ExampleDataB exampleFactoryB = new ExampleDataB();
        exampleFactoryB.referenceAttribute.set(new ExampleDataA());

        exampleFactoryA.referenceAttribute.set(exampleFactoryB);
        exampleFactoryA.referenceListAttribute.add(new ExampleDataB());
        exampleFactoryA.referenceListAttribute.add(new ExampleDataB());

        exampleFactoryA.internal().addBackReferences();
        Assert.assertEquals(5,exampleFactoryA.internal().collectChildrenDeep().size());
    }

    @Test
    public void test_backReferenceCheck(){
        ExampleDataA exampleFactoryA = new ExampleDataA();
        Assert.assertFalse(exampleFactoryA.internal().hasBackReferencesFlat());
        exampleFactoryA.internal().addBackReferences();
        Assert.assertTrue(exampleFactoryA.internal().hasBackReferencesFlat());
    }

    @Test
    public void test_idEquals(){
        ExampleDataA exampleFactoryA = new ExampleDataA();
        Assert.assertFalse(exampleFactoryA.internal().hasBackReferencesFlat());
        exampleFactoryA.internal().addBackReferences();
        Assert.assertTrue(exampleFactoryA.idEquals(exampleFactoryA.utility().copy()));
        Assert.assertFalse(exampleFactoryA.idEquals(new ExampleDataA()));
    }

    @Test
    public void test_copy_uuid(){
        Data exampleFactoryA = new ExampleDataA();
        exampleFactoryA.getId();
        Assert.assertTrue(exampleFactoryA.id instanceof UUID);

        Assert.assertTrue(exampleFactoryA.internal().copy().id instanceof UUID);

        Assert.assertTrue(exampleFactoryA.internal().copy().id instanceof UUID);
    }

    @Test
    public void test_copy_uuid_bugrecreate(){
        ExampleDataA currentModel = new ExampleDataA();
        currentModel.internal().addBackReferences();

        ExampleDataA originalModel = currentModel.internal().copy();
        ExampleDataA newModel = currentModel.internal().copy();

        Assert.assertTrue(currentModel.internal().copy().id instanceof UUID);
        Assert.assertTrue(originalModel.internal().copy().id instanceof UUID);
        Assert.assertTrue(newModel.internal().copy().id instanceof UUID);
    }

    @Test
    public void test_root_aftercopy(){
        ExampleDataA data = new ExampleDataA();
        data.internal().addBackReferences();

        ExampleDataA copy = data.internal().copy();
        Assert.assertEquals(copy,copy.internal().getRoot());

        Assert.assertEquals(copy,getRoot(copy.referenceAttribute));
    }

    @Test
    public void test_root_aftercopy_nested(){
        ExampleDataA data = new ExampleDataA();
        data.referenceAttribute.set(new ExampleDataB());
        data.internal().addBackReferences();

        ExampleDataA copy = data.internal().copy();
        Assert.assertEquals(copy,copy.internal().getRoot());

        Assert.assertEquals(copy,getRoot(copy.referenceAttribute.get().referenceAttribute));
    }


    private Data getRoot(ReferenceBaseAttribute attribute){
        Field root = null;
        try {
            root = ReferenceBaseAttribute.class.getDeclaredField("root");
            root.setAccessible(true);
            return (Data) root.get(attribute);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void test_getPathFromRoot_param(){
        ExampleDataA data = new ExampleDataA();
        ExampleDataB exampleDataB = new ExampleDataB();
        data.referenceAttribute.set(exampleDataB);
        data.internal().addBackReferences();

        HashMap<Data,Data> child2parent = data.internal().getChildToParentMap();
        Assert.assertEquals(1,exampleDataB.internal().getPathFromRoot(child2parent).size());
        Assert.assertEquals(data,exampleDataB.internal().getPathFromRoot(child2parent).get(0));
    }

    @Test
    public void test_semanticCopy_bugrecreation(){
        ExampleDataA data = new ExampleDataA();
        ExampleDataB exampleDataB = new ExampleDataB();
        exampleDataB.referenceAttributeC.setCopySemantic(CopySemantic.SELF);
        exampleDataB.referenceAttributeC.set(new ExampleDataC());

        data.referenceAttribute.set(exampleDataB);

        data.internal().addBackReferences();

        ExampleDataB copy = exampleDataB.utility().semanticCopy();
        data.referenceListAttribute.add(copy);

        Assert.assertEquals(2,data.referenceAttribute.internal_possibleValues().size());
    }


}