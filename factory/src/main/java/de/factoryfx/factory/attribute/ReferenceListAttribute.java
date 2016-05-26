package de.factoryfx.factory.attribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.jackson.ObservableListJacksonAbleWrapper;
import de.factoryfx.factory.merge.attribute.AttributeMergeHelper;
import de.factoryfx.factory.merge.attribute.ReferenceListMergeHelper;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class ReferenceListAttribute<T extends FactoryBase<?,? super T>> extends Attribute<ObservableList<T>> {
    ObservableList<T> list = FXCollections.observableArrayList();

    public ReferenceListAttribute(AttributeMetadata<ObservableList<T>> attributeMetadata) {
        super(attributeMetadata);
    }

    @JsonCreator
    public ReferenceListAttribute(ObservableListJacksonAbleWrapper<T> list) {
        super(null);
        this.list = list;
    }

    public void add(T value) {
        get().add(value);
    }


    @Override
    public void collectChildren(Set<FactoryBase<?,?>> allModelEntities) {
        list.forEach(entity -> entity.collectModelEntitiesTo(allModelEntities));
    }

    @Override
    public AttributeMergeHelper<?> createMergeHelper() {
        return new ReferenceListMergeHelper<>(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void fixDuplicateObjects(Function<String, Optional<FactoryBase<?,?>>> getCurrentEntity) {
        List<T> currentToEditList = get();

        for (T entity : currentToEditList) {
            entity.fixDuplicateObjects(getCurrentEntity);
        }

        List<T> fixedList = new ArrayList<>();
        for (T entity : currentToEditList) {
            Optional<FactoryBase<?,?>> existingOptional = getCurrentEntity.apply(entity.getId());
            if (existingOptional.isPresent()) {
                fixedList.add((T) existingOptional.get());
            } else {
                fixedList.add(entity);
            }
        }
        currentToEditList.clear();
        currentToEditList.addAll(fixedList);

    }

    @Override
    public ObservableList<T> get() {
        return list;
    }



    @Override
    public void set(ObservableList<T> value) {
        setList(value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setFromValueOnlyAttribute(Object value, HashMap<String, FactoryBase<?,?>> objectPool) {
        ObservableList<T> newList = (ObservableList<T>) value;
        list.clear();
        newList.forEach(t -> list.add((T) ((FactoryBase<?,?>) t).reconstructMetadataDeep(objectPool)));
    }

    public boolean contains(T value) {
        return get().contains(value);
    }

    public void forEach(Consumer<? super T> action) {
        get().forEach(action);
    }

    public T get(int i) {
        return list.get(i);
    }

    @JsonValue
    ObservableList<T> getList() {
        return list;
    }

    @JsonValue
    void setList(ObservableList<T> list) {
        this.list = list;
    }

    public void remove(T value) {
        get().remove(value);
    }

    public void set(int i, T value) {
        get().set(i, value);
    }

    public int size() {
        return get().size();
    }

    public Stream<T> stream() {
        return get().stream();
    }

    Map<InvalidationListener, ListChangeListener<T>> listeners= new HashMap<>();
    @Override
    public void addListener(InvalidationListener listener) {
        ListChangeListener<T> mapListener = change -> listener.invalidated(get());
        listeners.put(listener,mapListener);
        list.addListener(mapListener);
    }
    @Override
    public void removeListener(InvalidationListener listener) {
        list.removeListener(listeners.get(listener));
    }

}
