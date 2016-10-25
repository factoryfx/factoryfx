package de.factoryfx.factory.atrribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.Data;
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

    //Workaround for genrics (T with generic params)
    @SuppressWarnings("unchecked")
    public FactoryReferenceListAttribute(AttributeMetadata attributeMetadata, Class clazz) {
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

    public boolean add(T data){
        return get().add(data);
    }

    /**customise the list of selectable items*/
    public FactoryReferenceListAttribute possibleValueProvider(Function<Data,List<T>> provider){
        super.possibleValueProvider(provider);
        return this;
    }

    /**customise how new values are created*/
    public FactoryReferenceListAttribute newValueProvider(Supplier<T> newValueProvider){
        super.newValueProvider(newValueProvider);
        return this;
    }

}
