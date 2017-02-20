package de.factoryfx.data.attribute;

import java.util.Collection;
import java.util.function.Function;

public class CollectionAttributeUtil<T> {

    private final Collection<T> list;
    private final Function<T,String> listItemDisplayText;

    public CollectionAttributeUtil(Collection<T> list, Function<T,String> listItemDisplayText){
        this.list= list;
        this.listItemDisplayText = listItemDisplayText;
    }

    public String getDisplayText() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        stringBuilder.append(list.size());
        stringBuilder.append("][");
        int counter=0;
        for (T item:  list){
            stringBuilder.append(listItemDisplayText.apply(item));
            if (counter<list.size()-1){
                stringBuilder.append(", ");
            }
            counter++;
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }


}
