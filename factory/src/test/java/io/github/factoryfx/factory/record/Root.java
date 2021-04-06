package io.github.factoryfx.factory.record;

public class Root {
    private final RecordExampleA instance;
    private final RecordExampleB instanceB;

    public Root(RecordExampleA instance, RecordExampleB instanceB) {
        this.instance=instance;
        this.instanceB=instanceB;
    }

    public String print(){
        if (instanceB!=null){
            return instanceB.print();
        }
        return instance.print();
    }
}
