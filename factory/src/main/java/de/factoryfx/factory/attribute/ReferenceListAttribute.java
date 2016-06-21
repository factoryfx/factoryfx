package de.factoryfx.factory.attribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class ReferenceListAttribute<T extends FactoryBase<?,? super T>> extends Attribute<ObservableList<T>> {
    ObservableList<T> list = FXCollections.observableArrayList();

    public ReferenceListAttribute(ObservableList<T> defaultValue) {
        list=defaultValue;
    }

    public ReferenceListAttribute() {
    }

    @JsonCreator
    public ReferenceListAttribute(ObservableListJacksonAbleWrapper<T> list) {
        this.list = list.unwrap();
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

    Map<AttributeChangeListener<ObservableList<T>>, ListChangeListener<T>> listeners= new HashMap<>();
    @Override
    public void addListener(AttributeChangeListener<ObservableList<T>> listener) {
        ListChangeListener<T> listListener = change -> listener.changed(ReferenceListAttribute.this,get());
        listeners.put(listener,listListener);
        list.addListener(listListener);
    }
    @Override
    public void removeListener(AttributeChangeListener<ObservableList<T>> listener) {
        list.removeListener(listeners.get(listener));
        listeners.remove(listener);
    }

    @Override
    public String getDisplayText(Locale locale) {
        StringBuilder stringBuilder = new StringBuilder("List (number of entries: "+ list.size()+")\n");
        for (T item:  list){
            stringBuilder.append(item.getDisplayText());
            stringBuilder.append(",\n");
        }
        return metadata.labelText.getPreferred(locale)+":\n"+stringBuilder.toString();
    }

    @Override
    public void visit(AttributeVisitor attributeVisitor) {
        attributeVisitor.referenceList(this);
    }


    public Optional<Function<FactoryBase<?,?>,List<FactoryBase<?,?>>>> possibleValueProviderFromRoot=Optional.empty();
}
