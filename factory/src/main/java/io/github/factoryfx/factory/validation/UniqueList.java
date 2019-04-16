package io.github.factoryfx.factory.validation;


import io.github.factoryfx.factory.FactoryBase;

import java.util.UUID;

public class UniqueList<T extends FactoryBase<?,?>> extends UniqueListBy<T, UUID> {
    public UniqueList() {
        super(FactoryBase::getId);
    }
}
