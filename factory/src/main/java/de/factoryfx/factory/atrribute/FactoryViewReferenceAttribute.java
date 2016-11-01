package de.factoryfx.factory.atrribute;

import java.util.function.BiFunction;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ViewReferenceAttribute;
import de.factoryfx.factory.FactoryBase;

public class FactoryViewReferenceAttribute<R extends FactoryBase<?,?>, P extends FactoryBase<?,?>,L, T extends FactoryBase<L,?>> extends ViewReferenceAttribute<R,P,T> {

    public FactoryViewReferenceAttribute(AttributeMetadata attributeMetadata, BiFunction view) {
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