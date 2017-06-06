package de.factoryfx.data.attribute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import de.factoryfx.data.Data;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public abstract class ReferenceListAttribute<T extends Data,A extends ReferenceBaseAttribute<T,List<T>,A>> extends ReferenceBaseAttribute<T,List<T>,A> implements Collection<T> {
    ObservableList<T> list = FXCollections.observableArrayList();

    /**
     * @see ReferenceBaseAttribute#ReferenceBaseAttribute(Class ,AttributeMetadata)
     * */
    public ReferenceListAttribute(Class<T> containingFactoryClass, AttributeMetadata attributeMetadata) {
        super(containingFactoryClass,attributeMetadata);

        list.addListener((ListChangeListener<T>) c -> {
            if (c.next()){
                for (T newData : c.getAddedSubList()) {
                    if (newData == null) {
                        throw new IllegalStateException("cant't add null to list");
                    }
                    if (root!=null && newData.internal().getRoot()!=root) {
                        newData.internal().propagateRoot(root);
                    }
                }
            }
            for (AttributeChangeListener<List<T>> listener: listeners){
                listener.changed(ReferenceListAttribute.this,get());
            }
        });
    }

    /**
     * @see ReferenceBaseAttribute#ReferenceBaseAttribute(AttributeMetadata,Class)
     * */
    @SuppressWarnings("unchecked")
    public ReferenceListAttribute(AttributeMetadata attributeMetadata, Class clazz) {
        super(attributeMetadata,clazz);
    }

    @JsonCreator
    protected ReferenceListAttribute() {
        super(null,(AttributeMetadata)null);
    }



    @Override
    public void internal_collectChildren(Set<Data> allModelEntities) {
        list.forEach(entity -> entity.internal().collectModelEntitiesTo(allModelEntities));
    }

    @Override
    public boolean internal_match(List<T> value) {
        if (value==null ){
            return false;
        }
        if (list.size() != value.size()) {
            return false;
        }
        for (int i = 0; i < list.size(); i++) {
            if (!referenceEquals(list.get(i), value.get(i))) {
                return false;
            }
        }
        return true;
    }

    private boolean referenceEquals(Data ref1, Data ref2) {
        if (ref1 == null && ref2 == null) {
            return true;
        }
        if (ref1 == null || ref2 == null) {
            return false;
        }
        return ref1.getId().equals(ref2.getId());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void internal_fixDuplicateObjects(Map<String, Data> idToDataMap) {
        List<T> currentToEditList = get();

        List<T> fixedList = new ArrayList<>();
        for (T entity : currentToEditList) {
            fixedList.add((T)idToDataMap.get(entity.getId()));
        }
        currentToEditList.clear();
        currentToEditList.addAll(fixedList);

    }

    @Override
    public List<T> get() {
        return list;
    }


    /** set list only take the list items not the list itself, (to simplify ChangeListeners)*/
    @Override
    public void set(List<T> value) {
        if (value==null){
            this.list.clear();
        } else {
            this.list.setAll(value);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void internal_copyTo(Attribute<List<T>> copyAttribute, Function<Data,Data> dataCopyProvider) {
        for (T item: get()){
            final T itemCopy = (T) dataCopyProvider.apply(item);
            if (itemCopy!=null){
                copyAttribute.get().add(itemCopy);
            }
        }
    }

    @Override
    public void internal_semanticCopyTo(Attribute<List<T>> copyAttribute) {
        if (copySemantic==CopySemantic.SELF){
            copyAttribute.set(get());
        } else {
            for (T item: get()){
                final T itemCopy = item.internal().semanticCopy();
                if (itemCopy!=null){
                    copyAttribute.get().add(itemCopy);
                }
            }
        }
    }

    public List<T> filtered(Predicate<T> predicate) {
        return get().stream().filter(predicate).collect(Collectors.toList());
    }

    final List<AttributeChangeListener<List<T>>> listeners= new ArrayList<>();
    @Override
    public void internal_addListener(AttributeChangeListener<List<T>> listener) {
        listeners.add(listener);
    }
    @Override
    public void internal_removeListener(AttributeChangeListener<List<T>> listener) {
        for (AttributeChangeListener<List<T>> listenerItem: new ArrayList<>(listeners)){
            if (listenerItem.unwrap()==listener || listenerItem.unwrap()==null){
                listeners.remove(listenerItem);
            }
        }
    }

    @Override
    public String getDisplayText() {
        return new CollectionAttributeUtil<>(get(), t -> t.internal().getDisplayText()).getDisplayText();
    }

    @Override
    public void internal_visit(AttributeVisitor attributeVisitor) {
        attributeVisitor.referenceList(this);
    }

    @Override
    @JsonIgnore
    public AttributeTypeInfo internal_getAttributeType() {
        return new AttributeTypeInfo(ObservableList.class,null,null,Data.class, AttributeTypeInfo.AttributeTypeCategory.REFERENCE_LIST);
    }



    public T internal_addNewFactory(){
        T addedFactory;
        if (newValueProvider!=null) {
            T newFactory = newValueProvider.apply(root);
            get().add(newFactory);
            addedFactory = newFactory;
        } else {
            try {
                T newFactory = containingFactoryClass.newInstance();
                get().add(newFactory);
                addedFactory = newFactory;
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        addedFactory.internal().propagateRoot(root);
        return addedFactory;
    }

    public Class<T> internal_getReferenceClass(){
        return containingFactoryClass;
    }


    public void internal_deleteFactory(T factory){
        list.remove(factory);
        if (additionalDeleteAction!=null){
            additionalDeleteAction.accept(factory, root);
        }
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return list.toArray(a);
    }

    @Override
    public Stream<T> stream() {
        return list.stream();
    }

    @Override
    public Stream<T> parallelStream() {
        return list.parallelStream();
    }

    @Override
    public boolean remove(Object o) {
        return list.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return list.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return list.removeAll(c);
    }

    @Override
    public boolean removeIf(Predicate<? super T> filter) {
        return list.removeIf(filter);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return list.retainAll(c);
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public Spliterator<T> spliterator() {
        return list.spliterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        get().forEach(action);
    }

    @Override
    public int size() {
        return get().size();
    }

    @Override
    public boolean add(T value) {
        if (value==null){
            throw new IllegalStateException("cant't add null to list");
        }
        get().add(value);
        return false;
    }

    public T get(int i) {
        return list.get(i);
    }

    public void set(int i, T value) {
        get().set(i, value);
    }

}
