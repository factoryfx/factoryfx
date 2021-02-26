package io.github.factoryfx.record;

public class RecordExampleA {


    record Dep(Dependency<RecordExampleB, RecordExampleB.Dep> exampleB, DependencyList<RecordExampleB, RecordExampleB.Dep> exampleBList) implements Dependencies<RecordExampleA>{

    }
    private final Dep dep;

    public RecordExampleA(Dep dep){
        this.dep=dep;
    }


    public String print() {
       return dep.exampleB.instance().print();
    }
}
