package io.github.factoryfx.data.validation;

import io.github.factoryfx.data.Data;

public class UniqueList<T extends Data> extends UniqueListBy<T, String> {
    public UniqueList() {
        super(Data::getId);
    }
}
