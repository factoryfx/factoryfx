package de.factoryfx.factory.atrribute;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ReferenceListAttribute;
import de.factoryfx.data.jackson.ObservableListJacksonAbleWrapper;
import de.factoryfx.factory.FactoryBase;

public class FactoryReferenceListAttribute<L,T extends FactoryBase<L,?>> extends  ReferenceListAttribute<T>{

    @JsonCreator
    protected FactoryReferenceListAttribute() {
        super();
    }

    @JsonCreator
    protected FactoryReferenceListAttribute(ObservableListJacksonAbleWrapper<T> list) {
        super(list);
    }

    public FactoryReferenceListAttribute(Class<T> clazz, AttributeMetadata attributeMetadata) {
        super(clazz, attributeMetadata);
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
}
