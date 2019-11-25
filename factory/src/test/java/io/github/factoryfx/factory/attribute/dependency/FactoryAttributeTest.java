package io.github.factoryfx.factory.attribute.dependency;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.AttributeChangeListener;
import io.github.factoryfx.factory.attribute.CopySemantic;
import io.github.factoryfx.factory.attribute.WeakAttributeChangeListener;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.merge.testdata.ExampleDataA;
import io.github.factoryfx.factory.metadata.FactoryMetadataManager;
import io.github.factoryfx.factory.validation.ValidationError;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import io.github.factoryfx.factory.testfactories.ExampleLiveObjectA;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class FactoryAttributeTest {

    @Test
    public void test_null(){
        FactoryAttribute<ExampleLiveObjectA, ExampleFactoryA> attribute = new FactoryAttribute<ExampleLiveObjectA, ExampleFactoryA>();

        {
            attribute.set(null);
            List<ValidationError> validationErrors = attribute.internal_validate(new ExampleFactoryA(),"");
            Assertions.assertEquals(1, validationErrors.size());
        }

        {
            attribute.set(new ExampleFactoryA());
            List<ValidationError> validationErrors = attribute.internal_validate(new ExampleFactoryA(),"");
            Assertions.assertEquals(0, validationErrors.size());
        }
    }

    @Test
    public void test_internal_require_true(){
        FactoryAttribute<ExampleLiveObjectA, ExampleFactoryA> attribute = new FactoryAttribute<>();
        Assertions.assertTrue(attribute.internal_required());
    }

    @Test
    public void test_internal_require_false(){
        FactoryAttribute<ExampleLiveObjectA, ExampleFactoryA> attribute = new FactoryAttribute<ExampleLiveObjectA, ExampleFactoryA>().nullable();
        Assertions.assertFalse(attribute.internal_required());
    }



    @Test
    public void test_nullable(){
        FactoryAttribute<ExampleLiveObjectA, ExampleFactoryA> attribute = new FactoryAttribute<ExampleLiveObjectA, ExampleFactoryA>().nullable();


        {
            attribute.set(null);
            List<ValidationError> validationErrors = attribute.internal_validate(new ExampleFactoryA(),"");
            Assertions.assertEquals(0, validationErrors.size());
        }

        {
            attribute.set(new ExampleFactoryA());
            List<ValidationError> validationErrors = attribute.internal_validate(new ExampleFactoryA(),"");
            Assertions.assertEquals(0, validationErrors.size());
        }
    }

    public static class CreateExampleFactory extends SimpleFactoryBase<Void,ExampleFactoryA> {
        @SuppressWarnings("unchecked")
        FactoryAttribute<ExampleLiveObjectA, ExampleFactoryA> attribute = new FactoryAttribute<ExampleLiveObjectA, ExampleFactoryA>().nullable();
        @Override
        protected Void createImpl() {
            return null;
        }
    }

    @Test
    public void test_create_json(){
        CreateExampleFactory factory = new CreateExampleFactory();
        factory.attribute.set(new ExampleFactoryA());
        ObjectMapperBuilder.build().copy(factory);
    }

    public static class MockExampleFactoryRoot extends SimpleFactoryBase<Void,MockExampleFactoryRoot> {
        public final FactoryAttribute<LiveDummy, ExampleFactory> attribute = new FactoryAttribute<>();
        @Override
        protected Void createImpl() {
            return null;
        }
    }

    public static class ExampleFactory extends SimpleFactoryBase<LiveDummy,MockExampleFactoryRoot> {

        @Override
        protected LiveDummy createImpl() {
            return new LiveDummy();
        }
    }

    public static class LiveDummy  {
        public void doX(){

        }
    }

    public static class MockExampleFactoryMock extends ExampleFactory {

        @Override
        protected LiveDummy createImpl() {
            return Mockito.mock(LiveDummy.class);
        }
    }

    @Test
    public void test_mock(){
        MockExampleFactoryRoot root = new MockExampleFactoryRoot();
        root.attribute.set(new MockExampleFactoryMock());
    }


    public static class ExampleReferenceFactory extends FactoryBase<Void,ExampleDataA> {
        public FactoryAttribute<Void,ExampleDataA> referenceAttribute =new FactoryAttribute<>();
    }

    @Test
    public void testObservable(){
        FactoryAttribute<Void,ExampleDataA> referenceAttribute=new FactoryAttribute<>();
        ArrayList<String> calls= new ArrayList<>();
        referenceAttribute.internal_addListener((a,value) -> calls.add(""));
        referenceAttribute.set(new ExampleDataA());

        Assertions.assertEquals(1,calls.size());
    }

    @Test
    public void test_json(){
        ExampleReferenceFactory exampleReferenceFactory = new ExampleReferenceFactory();
        ExampleDataA exampleFactoryA = new ExampleDataA();
        exampleFactoryA.stringAttribute.set("sadsasd");
        exampleReferenceFactory.referenceAttribute.set(exampleFactoryA);
        ObjectMapperBuilder.build().copy(exampleReferenceFactory);
    }

    @Test
    public void remove_Listener(){
        FactoryAttribute<Void,ExampleDataA> referenceAttribute=new FactoryAttribute<>();
        ArrayList<String> calls= new ArrayList<>();
        AttributeChangeListener<ExampleDataA, FactoryAttribute<Void,ExampleDataA>> invalidationListener = (a, o) -> {
            calls.add("");
        };
        referenceAttribute.internal_addListener(invalidationListener);
        referenceAttribute.set(new ExampleDataA());

        Assertions.assertEquals(1,calls.size());

        referenceAttribute.internal_removeListener(invalidationListener);
        referenceAttribute.set(new ExampleDataA());
        Assertions.assertEquals(1,calls.size());
    }

    @Test
    public void test_add_new(){
        ExampleReferenceFactory exampleReferenceFactory = new ExampleReferenceFactory();
        exampleReferenceFactory.internal().finalise();

        Assertions.assertNull(exampleReferenceFactory.referenceAttribute.get());
        List<ExampleDataA> exampleFactoryAS = exampleReferenceFactory.referenceAttribute.internal_createNewPossibleValues(FactoryMetadataManager.getMetadata(ExampleReferenceFactory.class).getAttributeMetadata(f->f.referenceAttribute));
        exampleReferenceFactory.referenceAttribute.set(exampleFactoryAS.get(0));
        Assertions.assertNotNull(exampleReferenceFactory.referenceAttribute.get());

    }

    @Test
    public void test_get_possible(){
        ExampleReferenceFactory root = new ExampleReferenceFactory();
        ExampleDataA exampleFactoryA = new ExampleDataA();
        root.referenceAttribute.set(exampleFactoryA);
        root.internal().finalise();

        Collection<ExampleDataA> possibleFactories =root.referenceAttribute.internal_possibleValues(FactoryMetadataManager.getMetadata(ExampleReferenceFactory.class).getAttributeMetadata(f->f.referenceAttribute));
        Assertions.assertEquals(1,possibleFactories.size());
        Assertions.assertEquals(exampleFactoryA,new ArrayList<>(possibleFactories).get(0));

    }

    @Test
    public void test_Observable_first(){
        FactoryAttribute<Void,ExampleDataA> referenceAttribute=new FactoryAttribute<>();
        ArrayList<Object> calls= new ArrayList<>();
        referenceAttribute.internal_addListener((a,value) -> calls.add(value));
        ExampleDataA added = new ExampleDataA();
        referenceAttribute.set(added);

        Assertions.assertEquals(1,calls.size());
        Assertions.assertEquals(added,calls.get(0));
    }

    @Test
    public void test_listener(){
        FactoryAttribute<Void,ExampleDataA> referenceAttribute=new FactoryAttribute<>();

        List<ExampleDataA> calls = new ArrayList<>();
        List<ExampleDataA> callsAttributeGet = new ArrayList<>();
        referenceAttribute.internal_addListener((attribute, value) -> {
            calls.add(value);
            callsAttributeGet.add(attribute.get());
        });
        ExampleDataA exampleFactoryA = new ExampleDataA();
        referenceAttribute.set(exampleFactoryA);
        Assertions.assertEquals(1,calls.size());
        Assertions.assertEquals(exampleFactoryA,calls.get(0));
        Assertions.assertEquals(exampleFactoryA,callsAttributeGet.get(0));

    }

    @Test
    public void removeListener() {
        FactoryAttribute<Void,ExampleDataA> attribute=new FactoryAttribute<>();

        final AttributeChangeListener<ExampleDataA, FactoryAttribute<Void,ExampleDataA>> attributeChangeListener = (a, value) -> System.out.println(value);
        attribute.internal_addListener(attributeChangeListener);
        Assertions.assertTrue(attribute.internal_getListeners().size()==1);
        attribute.internal_removeListener(attributeChangeListener);
        Assertions.assertTrue(attribute.internal_getListeners().size()==0);
    }

    @Test
    public void removeWeakListener() {
        FactoryAttribute<Void,ExampleDataA> attribute=new FactoryAttribute<>();

        final AttributeChangeListener<ExampleDataA, FactoryAttribute<Void,ExampleDataA>> attributeChangeListener = (a, value) -> System.out.println(value);
        attribute.internal_addListener(new WeakAttributeChangeListener<>(attributeChangeListener));
        Assertions.assertTrue(attribute.internal_getListeners().size()==1);
        attribute.internal_removeListener(attributeChangeListener);
        Assertions.assertTrue(attribute.internal_getListeners().size()==0);
    }

    @Test
    public void removeWeakListener_after_gc() {
        FactoryAttribute<Void,ExampleDataA> attribute=new FactoryAttribute<>();

        final AttributeChangeListener<ExampleDataA, FactoryAttribute<Void,ExampleDataA>> attributeChangeListener = (a, value) -> System.out.println(value);
        attribute.internal_addListener(new WeakAttributeChangeListener<>(null));
        Assertions.assertTrue(attribute.internal_getListeners().size()==1);
        attribute.internal_removeListener(attributeChangeListener);
        Assertions.assertTrue(attribute.internal_getListeners().size()==0);
    }

    @Test
    public void test_semanticcopy_self(){
        FactoryAttribute<Void,ExampleDataA> attributeFrom =new FactoryAttribute<>();
        attributeFrom.setCopySemantic(CopySemantic.SELF);
        attributeFrom.set(new ExampleDataA());
        FactoryAttribute<Void,ExampleDataA> attributeTo =new FactoryAttribute<>();
        attributeFrom.internal_semanticCopyTo(attributeTo);
        Assertions.assertTrue(attributeFrom.get()==attributeTo.get(),"same reference");
    }

    @Test
    public void test_semanticcopy_copy(){
        FactoryAttribute<Void,ExampleDataA> attributeFrom =new FactoryAttribute<>();
        attributeFrom.setCopySemantic(CopySemantic.COPY);
        attributeFrom.set(new ExampleDataA());
        FactoryAttribute<Void,ExampleDataA> attributeTo =new FactoryAttribute<>();
        attributeFrom.internal_semanticCopyTo(attributeTo);
        Assertions.assertTrue(attributeFrom.get()!=attributeTo.get(),"not same reference");
        Assertions.assertNotEquals(attributeFrom.get().getId(),attributeTo.get().getId());
    }

}