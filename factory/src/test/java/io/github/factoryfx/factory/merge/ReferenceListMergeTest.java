package io.github.factoryfx.factory.merge;

import java.util.UUID;
import java.util.stream.Collectors;

import io.github.factoryfx.factory.merge.testdata.ExampleDataA;
import io.github.factoryfx.factory.merge.testdata.ExampleDataB;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class ReferenceListMergeTest extends MergeHelperTestBase{

    @Test
    public void test_same(){
        ExampleDataA current = new ExampleDataA();
        ExampleDataB newValue1 = new ExampleDataB();
        newValue1.stringAttribute.set("1111");
        ExampleDataB newValue2 = new ExampleDataB();
        newValue2.stringAttribute.set("2222");
        ExampleDataB newValue3 = new ExampleDataB();
        newValue3.stringAttribute.set("2222");
        current.referenceListAttribute.add(newValue1);
        current.referenceListAttribute.add(newValue2);
        current.referenceListAttribute.add(newValue3);
        current = current.internal().finalise();


        ExampleDataA update = new ExampleDataA();
        update.referenceListAttribute.add(newValue1);
        update.referenceListAttribute.add(newValue2);
        update.referenceListAttribute.add(newValue3);
        update = update.internal().finalise();

        Assertions.assertTrue(merge(current, current, update).hasNoConflicts());
        Assertions.assertEquals(3, current.referenceListAttribute.size());
        Assertions.assertEquals(newValue1.stringAttribute.get(), current.referenceListAttribute.get(0).stringAttribute.get());
        Assertions.assertEquals(newValue2.stringAttribute.get(), current.referenceListAttribute.get(1).stringAttribute.get());
        Assertions.assertEquals(newValue3.stringAttribute.get(), current.referenceListAttribute.get(2).stringAttribute.get());
    }

    @Test
    public void test_1_new_added(){
        ExampleDataA current = new ExampleDataA();
        ExampleDataB newValue1 = new ExampleDataB();
        ExampleDataB newValue2 = new ExampleDataB();
        ExampleDataB newValue3 = new ExampleDataB();
        current.referenceListAttribute.add(newValue1);
        current.referenceListAttribute.add(newValue2);
        current.referenceListAttribute.add(newValue3);
        current = current.internal().finalise();

        ExampleDataA update = current.internal().copy();
        ExampleDataB replacedValue = new ExampleDataB();
        update.referenceListAttribute.set(2,replacedValue);

        UUID idBefore1=current.referenceListAttribute.get(0).getId();
        UUID idBefore2=current.referenceListAttribute.get(1).getId();
        UUID idBefore3=update.referenceListAttribute.get(2).getId();
        Assertions.assertTrue(merge(current, current, update).hasNoConflicts());
        Assertions.assertEquals(3, current.referenceListAttribute.size());
        Assertions.assertEquals(idBefore1, current.referenceListAttribute.get(0).getId());
        Assertions.assertEquals(idBefore2, current.referenceListAttribute.get(1).getId());
        Assertions.assertEquals(idBefore3, current.referenceListAttribute.get(2).getId());
    }

    @Test
    public void test_1_current_added(){
        ExampleDataA current = new ExampleDataA();
        ExampleDataB newValue1 = new ExampleDataB();
        ExampleDataB newValue2 = new ExampleDataB();
        current.referenceListAttribute.add(newValue1);
        current.referenceListAttribute.add(newValue2);
        current = current.internal().finalise();

        ExampleDataA orginal = current.internal().copy();
        ExampleDataA update = current.internal().copy();

        {
            ExampleDataB replacedValue = new ExampleDataB();
            current.referenceListAttribute.add(replacedValue);
        }

        UUID idBefore1=current.referenceListAttribute.get(0).getId();
        UUID idBefore2=current.referenceListAttribute.get(1).getId();
        UUID idBefore3=current.referenceListAttribute.get(2).getId();

        Assertions.assertTrue(merge(current, orginal, update).hasNoConflicts());
        Assertions.assertEquals(3, current.referenceListAttribute.size());
        Assertions.assertEquals(idBefore1, current.referenceListAttribute.get(0).getId());
        Assertions.assertEquals(idBefore2, current.referenceListAttribute.get(1).getId());
        Assertions.assertEquals(idBefore3,current.referenceListAttribute.get(2).getId());
    }

    @Test
    public void test_delete(){
        ExampleDataA current = new ExampleDataA();
        ExampleDataB newValue1 = new ExampleDataB();
        ExampleDataB newValue2 = new ExampleDataB();
        ExampleDataB newValue3 = new ExampleDataB();
        current.referenceListAttribute.add(newValue1);
        current.referenceListAttribute.add(newValue2);
        current.referenceListAttribute.add(newValue3);
        current = current.internal().finalise();


        ExampleDataA update = current.internal().copy();
        update.referenceListAttribute.remove(2);
        update = update.internal().finalise();

        UUID idBefore1=current.referenceListAttribute.get(0).getId();
        UUID idBefore2=current.referenceListAttribute.get(1).getId();
        Assertions.assertTrue(merge(current, current, update).hasNoConflicts());
        Assertions.assertEquals(2, current.referenceListAttribute.size());
        Assertions.assertEquals(idBefore1, current.referenceListAttribute.get(0).getId());
        Assertions.assertEquals(idBefore2, current.referenceListAttribute.get(1).getId());
    }

    @Disabled
    @Test
    public void test_both_added(){
        ExampleDataA current = new ExampleDataA();
        ExampleDataB newValue1 = new ExampleDataB();
        ExampleDataB newValue2 = new ExampleDataB();
        current.referenceListAttribute.add(newValue1);
        current.referenceListAttribute.add(newValue2);


        ExampleDataA orginal = current.internal().copy();
        ExampleDataA update = current.internal().copy();

        ExampleDataB newValue3 = new ExampleDataB();
        current.referenceListAttribute.add(newValue3);

        ExampleDataB newValue4 = new ExampleDataB();
        update.referenceListAttribute.add(newValue4);

        Assertions.assertTrue(merge(current, orginal, update).hasNoConflicts());
        Assertions.assertEquals(4, current.referenceListAttribute.size());
        Assertions.assertTrue(current.referenceListAttribute.stream().map(bTest -> bTest.getId()).collect(Collectors.toList()).contains(newValue1.getId()));
        Assertions.assertTrue(current.referenceListAttribute.stream().map(bTest -> bTest.getId()).collect(Collectors.toList()).contains(newValue2.getId()));
        Assertions.assertTrue(current.referenceListAttribute.stream().map(bTest -> bTest.getId()).collect(Collectors.toList()).contains(newValue3.getId()));
        Assertions.assertTrue(current.referenceListAttribute.stream().map(bTest -> bTest.getId()).collect(Collectors.toList()).contains(newValue4.getId()));
    }

    @Test
    public void test_no_change_should_merge_nothing(){
        ExampleDataA current = new ExampleDataA();
        ExampleDataB newValue1 = new ExampleDataB();
        ExampleDataB newValue2 = new ExampleDataB();
        current.referenceListAttribute.add(newValue1);
        current.referenceListAttribute.add(newValue2);
        current = current.internal().finalise();


        ExampleDataA orginal = current.internal().copy();
        ExampleDataA update = current.internal().copy();

        final MergeDiffInfo merge = merge(current, orginal, update);
        Assertions.assertTrue(merge.hasNoConflicts());
        Assertions.assertEquals(2, current.referenceListAttribute.size());
        Assertions.assertEquals(0, merge.mergeInfos.size());
    }

    @Test
    public void test_order_changed(){
        ExampleDataA current = new ExampleDataA();
        ExampleDataB newValue1 = new ExampleDataB();
        ExampleDataB newValue2 = new ExampleDataB();
        current.referenceListAttribute.add(newValue1);
        current.referenceListAttribute.add(newValue2);
        current = current.internal().finalise();

        ExampleDataA orginal = current.internal().copy();

        ExampleDataA update = current.internal().copy();
        final ExampleDataB first = update.referenceListAttribute.get(0);
        final ExampleDataB second = update.referenceListAttribute.get(1);
        update.referenceListAttribute.clear();
        update.referenceListAttribute.add(second);
        update.referenceListAttribute.add(first);

        UUID idBefore1=current.referenceListAttribute.get(0).getId();
        UUID idBefore2=current.referenceListAttribute.get(1).getId();
        final MergeDiffInfo merge = merge(current, orginal, update);
        Assertions.assertTrue(merge.hasNoConflicts());
        Assertions.assertEquals(2, current.referenceListAttribute.size());
        Assertions.assertEquals(idBefore2, current.referenceListAttribute.get(0).getId());
        Assertions.assertEquals(idBefore1, current.referenceListAttribute.get(1).getId());
    }

    @Test
    public void test_keep_existing(){//important for factories
        ExampleDataA current = new ExampleDataA();
        ExampleDataB newValue1 = new ExampleDataB();
        current.referenceListAttribute.add(newValue1);
        current = current.internal().finalise();

        ExampleDataA orginal = current.internal().copy();

        ExampleDataA update = current.internal().copy();
        update.referenceListAttribute.add(new ExampleDataB());

        ExampleDataB existing = current.referenceListAttribute.get(0);
        merge(current, orginal, update);

        Assertions.assertTrue(existing==current.referenceListAttribute.get(0));

    }
}