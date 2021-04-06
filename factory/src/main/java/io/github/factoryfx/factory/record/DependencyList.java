package io.github.factoryfx.factory.record;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.github.factoryfx.factory.attribute.dependency.FactoryListAttribute;

import java.util.List;
import java.util.stream.Collectors;

public class DependencyList<L , D extends Dependencies<L>> extends FactoryListAttribute<L, RecordFactory<L,D,?>> {
    @JsonCreator
    private DependencyList() {
    }

    public DependencyList(List<RecordFactory<L, D, ?>> dependencies) {
        set(dependencies);
    }

    List<D> deps(){
        return get().stream().map(RecordFactory::dep).collect(Collectors.toList());
    }

}
