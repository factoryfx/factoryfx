package io.github.factoryfx.factory.merge;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.types.EnumAttribute;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class EnumMergeTest extends MergeHelperTestBase {
    public enum TestEnum{
        A,B
    }

    public static class EnumMergeTestPojo extends FactoryBase<Void, EnumMergeTestPojo> {
        public final EnumAttribute<TestEnum> attribute=new EnumAttribute<>(TestEnum.class);
    }

    @Test
    public void test_merge_change(){
        EnumMergeTestPojo aTest1 = new EnumMergeTestPojo();
        aTest1.attribute.set(TestEnum.A);
        aTest1=aTest1.internal().finalise();

        EnumMergeTestPojo aTest2 = new EnumMergeTestPojo();
        aTest2.attribute.set(TestEnum.B);
        aTest2=aTest2.internal().finalise();

        Assertions.assertEquals(TestEnum.A,aTest1.attribute.get());
        Assertions.assertTrue(merge(aTest1, aTest1.internal().copy(), aTest2).hasNoConflicts());
        Assertions.assertEquals(TestEnum.B,aTest1.attribute.get());

        ObjectMapperBuilder.build().copy(aTest1);
        Assertions.assertEquals(TestEnum.B,aTest1.attribute.get());
    }



}