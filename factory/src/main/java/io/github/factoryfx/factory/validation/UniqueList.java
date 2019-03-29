package io.github.factoryfx.factory.validation;


import io.github.factoryfx.factory.FactoryBase;

public class UniqueList<T extends FactoryBase<?,?>> extends UniqueListBy<T, String> {
    public UniqueList() {
        super(FactoryBase::getId);
    }
}
