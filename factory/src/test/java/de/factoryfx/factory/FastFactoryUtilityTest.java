package de.factoryfx.factory;

import de.factoryfx.data.merge.DataMerger;
import de.factoryfx.factory.testfactories.FastExampleFactoryA;
import de.factoryfx.factory.testfactories.FastExampleFactoryB;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class FastFactoryUtilityTest {

    @Test
    public void test_copy(){

        FastExampleFactoryA original = new FastExampleFactoryA();
        original.referenceAttribute=new FastExampleFactoryB();

        FastExampleFactoryA copy =original.utility().copy();

        Assertions.assertEquals(2,copy.internal().collectChildrenDeep().size());
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
}