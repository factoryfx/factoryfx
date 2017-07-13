package de.factoryfx.data;

import de.factoryfx.data.merge.testfactories.ExampleDataA;
import de.factoryfx.data.merge.testfactories.ExampleDataB;

public class DataPerformance {

    public static void main(String[] args) {
        ExampleDataA exampleFactoryA = new ExampleDataA();
        exampleFactoryA.stringAttribute.set("dfssfdsfdsfd");
        exampleFactoryA.referenceAttribute.set(craeteExampleDataB(0));
        exampleFactoryA.referenceListAttribute.add(craeteExampleDataB(0));

        System.out.println(exampleFactoryA.internal().collectChildrenDeep().size());


        int[] forceExecution=new int[]{0};

        final long start = System.currentTimeMillis();
        int times=300;
        for (int i=0;i<times;i++){
            final Data copy = exampleFactoryA.internal().copyFromRoot();
            forceExecution[0]++;
        }
        System.out.println(forceExecution[0]);
        System.out.println("single copy time: "+(System.currentTimeMillis()-start)/times);
    }

    private static  ExampleDataB craeteExampleDataB(int deep){
        if (deep>20){
            return null;
        }
        ExampleDataB exampleDataB = new ExampleDataB();
        exampleDataB.referenceAttribute.set(craeteExampleDataA(++deep));
        return exampleDataB;
    }

    private static ExampleDataA craeteExampleDataA(int deep){
        if (deep>20){
            return null;
        }
        ExampleDataA exampleDataA = new ExampleDataA();
        exampleDataA.referenceAttribute.set(craeteExampleDataB(++deep));
        for (int i=0;i<5;i++){
            ExampleDataB value = craeteExampleDataB(++deep);
            if (value!=null){
                exampleDataA.referenceListAttribute.add(value);
            }
        }
        return exampleDataA;
    }

}