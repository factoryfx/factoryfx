package io.github.factoryfx.factory.record;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;

public class Dependency<L , D extends Dependencies<L>> extends FactoryAttribute<L, RecordFactory<L,D,?>> {
    @JsonCreator
    private Dependency() {
    }

    public Dependency(RecordFactory<L, D, ?> dependency) {
        set(dependency);
    }

    D dep(){
        return get().dep();
    }

}
