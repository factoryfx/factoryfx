package de.factoryfx.data.validation;

import de.factoryfx.data.Data;

public class UniqueList<T extends Data> extends UniqueListBy<T, String> {
    public UniqueList() {
        super(Data::getId);
    }
}
