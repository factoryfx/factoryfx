package de.factoryfx.data.attribute.types;

import java.util.ArrayList;

import de.factoryfx.data.Data;
import org.junit.Assert;
import org.junit.Test;

public class WrappingValueAttributeTest {
    private static class TestA{
        public String name;
    }

    private static class TestAWrapper extends Data{
        private final TestA testA;

        public TestAWrapper(TestA testA) {
            this.testA = testA;
            name=new WrappingValueAttribute<>(String.class, ( ) -> testA.name, (name) -> testA.name= name);
        }

        final WrappingValueAttribute<String> name;


//        @Override
//        public Object getId() {
//            return 1;
//        }
//
//        @Override
//        public void setId(Object object) {
//
//        }
    }

    @Test
    public void test(){
        TestA testA = new TestA();
        testA.name="abc";
        TestAWrapper testAWrapper=new TestAWrapper(testA);

        Assert.assertEquals("abc",testAWrapper.name.get());
        testAWrapper.name.set("123");
        Assert.assertEquals("123",testAWrapper.name.get());
        Assert.assertEquals("123",testA.name);
    }

    @Test
    public void test_type(){
        TestA testA = new TestA();
        testA.name="abc";
        TestAWrapper testAWrapper=new TestAWrapper(testA);

        Assert.assertEquals(String.class,testAWrapper.name.internal_getAttributeType().dataType);

    }

    @Test
    public void testObservable(){
        TestA testA = new TestA();
        testA.name="abc";
        TestAWrapper testAWrapper=new TestAWrapper(testA);

        ArrayList<String> calls= new ArrayList<>();
        testAWrapper.name.internal_addListener((a,o)-> {
            calls.add("");
        });

        testAWrapper.name.set("123");
        Assert.assertEquals(1,calls.size());
        testAWrapper.name.set("345");
        Assert.assertEquals(2,calls.size());
    }

}