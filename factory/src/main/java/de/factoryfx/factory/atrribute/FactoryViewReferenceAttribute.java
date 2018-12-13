package de.factoryfx.factory.atrribute;

import java.util.function.Function;

import de.factoryfx.data.attribute.ViewReferenceAttribute;
import de.factoryfx.factory.FactoryBase;

public class FactoryViewReferenceAttribute<R extends FactoryBase<?,?,?>,L, T extends FactoryBase<L,?,?>> extends ViewReferenceAttribute<R,T,FactoryViewReferenceAttribute<R,L,T>> {

    public FactoryViewReferenceAttribute(Function<R,T> view) {
        super(view);
    }

    public L instance() {
        if (get() == null) {
            return null;
        }
        return get().internalFactory().instance();
    }

}