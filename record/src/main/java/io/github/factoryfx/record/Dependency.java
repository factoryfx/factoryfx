package io.github.factoryfx.record;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.AttributeCopy;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;

import java.util.List;
import java.util.function.Function;

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
