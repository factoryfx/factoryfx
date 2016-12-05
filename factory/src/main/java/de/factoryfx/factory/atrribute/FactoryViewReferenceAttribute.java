package de.factoryfx.factory.atrribute;

import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ViewReferenceAttribute;
import de.factoryfx.factory.FactoryBase;

public class FactoryViewReferenceAttribute<R extends FactoryBase<?,?>,L, T extends FactoryBase<L,?>> extends ViewReferenceAttribute<R,T> {

    public FactoryViewReferenceAttribute(AttributeMetadata attributeMetadata, Function<R,T> view) {
        super(attributeMetadata, view);
    }

    @JsonCreator
    FactoryViewReferenceAttribute() {
        super(null, null);
    }


    public L instance() {
        if (get() == null) {
            return null;
        }
        return get().instance();
    }

}