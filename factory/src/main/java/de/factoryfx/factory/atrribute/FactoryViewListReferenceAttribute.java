package de.factoryfx.factory.atrribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import de.factoryfx.data.attribute.ViewListReferenceAttribute;
import de.factoryfx.factory.FactoryBase;

public class FactoryViewListReferenceAttribute<R extends FactoryBase<?,?>,L, T extends FactoryBase<L,?>> extends ViewListReferenceAttribute<R,T,FactoryViewListReferenceAttribute<R,L,T>> {

    public FactoryViewListReferenceAttribute(Function<R, List<T>> view) {
        super(view);
    }

    public List<L> instances(){
        if (get()==null){
            return null;
        }
        ArrayList<L> result = new ArrayList<>();
        for(T item: get()){
            result.add(item.internalFactory().instance());
        }
        return result;
    }

    public boolean add(T data){
        return get().add(data);
    }

    public L instance(Predicate<T> filter){
        Optional<T> any = get().stream().filter(filter).findAny();
        return any.map(t -> t.internalFactory().instance()).orElse(null);
    }
}

