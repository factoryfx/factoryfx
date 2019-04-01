package io.github.factoryfx.factory;

import io.github.factoryfx.factory.merge.testdata.ExampleDataA;
import io.github.factoryfx.factory.merge.testdata.ExampleDataB;


public class FactoryCopyPerformance {

    public static void main(String[] args) {
        ExampleDataA exampleFactoryA = new ExampleDataA();
        exampleFactoryA.stringAttribute.set("dfssfdsfdsfd");
        exampleFactoryA.referenceAttribute.set(createExampleDataB(0));
        exampleFactoryA.referenceListAttribute.add(createExampleDataB(0));

//        System.out.println(exampleFactoryA.internal().collectChildrenDeep().size());


        int[] forceExecution=new int[]{0};

        final long start = System.currentTimeMillis();
        int times=300;
//        times=1;
        for (int i=0;i<times;i++){
            final ExampleDataA copy = exampleFactoryA.internal().copy();
//            ObjectMapperBuilder.build().copy(exampleFactoryA);
            forceExecution[0]++;
        }
        System.out.println(forceExecution[0]);
        System.out.println("single copy time: "+(System.currentTimeMillis()-start)/times);
    }

    private static ExampleDataB createExampleDataB(int deep){
        if (deep>20){
            return null;
        }
        ExampleDataB exampleDataB = new ExampleDataB();
        exampleDataB.referenceAttribute.set(createExampleDataA(++deep));
        return exampleDataB;
    }

    private static ExampleDataA createExampleDataA(int deep){
        if (deep>20){
            return null;
        }
        ExampleDataA exampleDataA = new ExampleDataA();
        exampleDataA.referenceAttribute.set(createExampleDataB(++deep));
        for (int i=0;i<5;i++){
            ExampleDataB value = createExampleDataB(++deep);
            if (value!=null){
                exampleDataA.referenceListAttribute.add(value);
            }
        }
        return exampleDataA;
    }

}