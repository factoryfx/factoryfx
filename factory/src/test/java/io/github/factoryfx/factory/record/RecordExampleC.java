package io.github.factoryfx.factory.record;

//liveobject is a record
public record RecordExampleC(String stringAttribute) implements Dependencies<RecordExampleC> {

    public String print() {
       return stringAttribute;
    }
}
