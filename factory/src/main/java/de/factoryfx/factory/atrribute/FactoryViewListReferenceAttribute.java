package de.factoryfx.factory.atrribute;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ViewListReferenceAttribute;
import de.factoryfx.factory.FactoryBase;

public class FactoryViewListReferenceAttribute<R extends FactoryBase<?,?>, P extends FactoryBase<?,?>,L, T extends FactoryBase<L,?>> extends ViewListReferenceAttribute<R,P,T> {

    public FactoryViewListReferenceAttribute(AttributeMetadata attributeMetadata, BiFunction<R, P, List<T>> view) {
        super(attributeMetadata, view);
    }

    @JsonCreator
    FactoryViewListReferenceAttribute() {
        super(null, null);
    }

    public List<L> instances(){
        if (get()==null){
            return null;
        }
        ArrayList<L> result = new ArrayList<>();
        for(T item: get()){
            result.add(item.instance());
        }
        return result;
    }

    public boolean add(T data){
        return get().add(data);
    }


}

