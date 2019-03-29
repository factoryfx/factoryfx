package io.github.factoryfx.factory.attribute;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CollectionAttributeUtil<T> {

    private final Collection<T> list;
    private final Function<T,String> listItemDisplayText;

    public CollectionAttributeUtil(Collection<T> list, Function<T,String> listItemDisplayText){
        this.list= list;
        this.listItemDisplayText = listItemDisplayText;
    }

    public String getDisplayText() {
        return "[" + list.size() + "][" + list.stream().map(listItemDisplayText).collect(Collectors.joining(", ")) + "]";
    }


}
