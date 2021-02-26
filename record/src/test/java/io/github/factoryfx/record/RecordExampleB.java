package io.github.factoryfx.record;

public class RecordExampleB {


    record Dep(String stringAttribute ) implements Dependencies<RecordExampleB>{

    }
    private final Dep dep;

    public RecordExampleB(Dep dep){
        this.dep=dep;
    }


    public String print() {
       return  dep.stringAttribute;
    }
}
