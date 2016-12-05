package de.factoryfx.factory.atrribute;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ViewListReferenceAttribute;
import de.factoryfx.factory.FactoryBase;

public class FactoryViewListReferenceAttribute<R extends FactoryBase<?,?>,L, T extends FactoryBase<L,?>> extends ViewListReferenceAttribute<R,T> {

    public FactoryViewListReferenceAttribute(AttributeMetadata attributeMetadata, Function<R, List<T>> view) {
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

