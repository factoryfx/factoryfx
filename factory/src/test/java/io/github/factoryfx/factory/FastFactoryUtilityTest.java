package io.github.factoryfx.factory;

import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.merge.DataMerger;
import io.github.factoryfx.factory.testfactories.FastExampleFactoryA;
import io.github.factoryfx.factory.testfactories.FastExampleFactoryB;
import io.github.factoryfx.factory.testfactories.FastExampleFactoryC;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;


public class FastFactoryUtilityTest {

    @Test
    public void test_collectChildrenDeep(){
        FastExampleFactoryA original = new FastExampleFactoryA();
        original.referenceAttribute=new FastExampleFactoryB();
        original.internal().addBackReferences();
        Assertions.assertEquals(2,original.internal().collectChildrenDeep().size());
    }

    @Test
    public void test_copy_value(){

        FastExampleFactoryA original = new FastExampleFactoryA();
        original.stringAttribute="123";

        FastExampleFactoryA copy =original.utility().copy();

        Assertions.assertEquals("123",copy.stringAttribute);
    }

    @Test
    public void test_copy_child(){
        FastExampleFactoryA original = new FastExampleFactoryA();
        original.referenceAttribute=new FastExampleFactoryB();

        FastExampleFactoryA copy =original.utility().copy();

        Assertions.assertEquals(2,copy.internal().collectChildrenDeep().size());
    }

    @Test
    public void test_copy_child_root_set(){
        FastExampleFactoryA original = new FastExampleFactoryA();
        original.referenceAttribute=new FastExampleFactoryB();

        FastExampleFactoryA copy = original.utility().copy();
        FactoryBase<?,?> copyTestable = copy;
        FactoryBase<?,?> refTestable = copy.referenceAttribute;

        Assertions.assertEquals(copy,copyTestable.root);
        Assertions.assertEquals(copy,refTestable.root);
    }


    @Test
    public void test_visit_dual(){
        List<String> counter = new ArrayList<>();

        FastExampleFactoryA original = new FastExampleFactoryA();
        FastExampleFactoryA copy = new FastExampleFactoryA();
        original.internal().visitAttributesForMatch(copy, (attributeVariableName, attribute1, attribute2) -> {
            counter.add("dfsf");
            return false;
        });
        Assertions.assertEquals(3,counter.size());
    }

    @Test
    public void test_copy_childList(){
        FastExampleFactoryA original = new FastExampleFactoryA();
        original.referenceListAttribute= List.of(new FastExampleFactoryB(),new FastExampleFactoryB());

        FastExampleFactoryA copy =original.utility().copy();

        Assertions.assertEquals(3,copy.internal().collectChildrenDeep().size());
    }

    @Test
    public void test_merge_ref(){
        FastExampleFactoryA original = new FastExampleFactoryA();
        original.internal().addBackReferences();

        Assertions.assertEquals(1,original.internal().collectChildrenDeep().size());

        FastExampleFactoryA update= original.utility().copy();
        update.referenceAttribute=new FastExampleFactoryB();

        DataMerger<FastExampleFactoryA> dataMerger = new DataMerger<>(original,original.utility().copy(),update);
        dataMerger.mergeIntoCurrent((p)->true);

        Assertions.assertEquals(2,original.internal().collectChildrenDeep().size());
    }

    @Test
    public void test_merge_add_to_nested_ref(){
        FastExampleFactoryA original = new FastExampleFactoryA();
        original.referenceListAttribute =List.of(new FastExampleFactoryB(),new FastExampleFactoryB());
        original.internal().addBackReferences();


        FastExampleFactoryA update= original.utility().copy();
        FastExampleFactoryC referenceAttributeC1 = new FastExampleFactoryC();
        update.referenceListAttribute.get(0).referenceAttributeC= referenceAttributeC1;
        FastExampleFactoryC referenceAttributeC2 = new FastExampleFactoryC();
        update.referenceListAttribute.get(1).referenceAttributeC= referenceAttributeC2;

        Assertions.assertEquals(5,update.internal().collectChildrenDeep().size());

        DataMerger<FastExampleFactoryA> dataMerger = new DataMerger<>(original,original.utility().copy(),update);
        dataMerger.mergeIntoCurrent((p)->true);

        Assertions.assertEquals(5,original.internal().collectChildrenDeep().size());
        Assertions.assertEquals(referenceAttributeC1,original.referenceListAttribute.get(0).referenceAttributeC);
        Assertions.assertEquals(referenceAttributeC2,original.referenceListAttribute.get(1).referenceAttributeC);
    }

    @Test
    public void test_merge_refList(){
        FastExampleFactoryA original = new FastExampleFactoryA();
        original.internal().addBackReferences();

        Assertions.assertEquals(1,original.internal().collectChildrenDeep().size());

        FastExampleFactoryA update= original.utility().copy();
        update.referenceListAttribute.add(new FastExampleFactoryB());

        DataMerger<FastExampleFactoryA> dataMerger = new DataMerger<>(original,original.utility().copy(),update);
        dataMerger.mergeIntoCurrent((p)->true);

        Assertions.assertEquals(2,original.internal().collectChildrenDeep().size());
    }

    @Test
    public void test_copy(){
        FastExampleFactoryA original = new FastExampleFactoryA();
        FactoryBase<?, ?> copy = original.internal().copy();

        System.out.println(
                "gdgfd"
        );


    }

    @Test
    public void test_parent_after_merge(){
        FastExampleFactoryA original = new FastExampleFactoryA();
        original.internal().addBackReferences();

        FastExampleFactoryA update= original.utility().copy();
        update.referenceAttribute=new FastExampleFactoryB();

        DataMerger<FastExampleFactoryA> dataMerger = new DataMerger<>(original,original.utility().copy(),update);
        dataMerger.mergeIntoCurrent((p)->true);

        Assertions.assertEquals(1,original.referenceAttribute.internal().getParents().size());
        Assertions.assertEquals(original,original.referenceAttribute.internal().getParents().iterator().next());
    }
}