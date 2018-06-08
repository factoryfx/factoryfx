package de.factoryfx.data.attribute;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.factoryfx.data.Data;

public abstract class ReferenceListAttribute<T extends Data,A extends ReferenceBaseAttribute<T,List<T>,A>> extends ReferenceBaseAttribute<T,List<T>,A> implements List<T> {
    List<T> list = new ArrayList<>();

    public ReferenceListAttribute() {
        super();
    }

    @Override
    public boolean internal_mergeMatch(List<T> value) {
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
        return this;
    }


    /** set list only take the list items not the list itself, (to simplify ChangeListeners)*/
    @Override
    public void set(List<T> value) {
        if (value==null){
            if (!list.isEmpty()){
                this.list.clear();
                afterModify();
            }
        } else {
            this.list.clear();
            this.list.addAll(value);
            afterAdd(value);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void internal_copyTo(A copyAttribute, Function<Data,Data> dataCopyProvider) {
        for (T item: get()){
            final T itemCopy = (T) dataCopyProvider.apply(item);
            if (itemCopy!=null){
                copyAttribute.get().add(itemCopy);
            }
        }
    }

    @Override
    public void internal_semanticCopyTo(A copyAttribute) {
        if (getCopySemantic()==CopySemantic.SELF){
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

    List<AttributeChangeListener<List<T>,A>> listeners;
    @Override
    public void internal_addListener(AttributeChangeListener<List<T>,A> listener) {
        if (listeners==null){
            listeners=new ArrayList<>();
        }
        listeners.add(listener);
    }
    @Override
    public void internal_removeListener(AttributeChangeListener<List<T>,A> listener) {
        if (listeners!=null){
            for (AttributeChangeListener<List<T>,A> listenerItem: new ArrayList<>(listeners)){
                if (listenerItem.unwrap()==listener || listenerItem.unwrap()==null){
                    listeners.remove(listenerItem);
                }
            }
        }
    }

    @Override
    public String getDisplayText() {
        return new CollectionAttributeUtil<>(get(), t -> t.internal().getDisplayText()).getDisplayText();
    }


    @Override
    @JsonIgnore
    public AttributeTypeInfo internal_getAttributeType() {
        return new AttributeTypeInfo(List.class,null,null,Data.class, AttributeTypeInfo.AttributeTypeCategory.REFERENCE_LIST);
    }

    public List<T> internal_createNewPossibleValues(){
        if (newValuesProvider!=null) {
            return newValuesProvider.apply(root);
        }
        if (getNewValueProvider()!=null) {
            return Collections.singletonList(getNewValueProvider().apply(root));
        }
        return new ArrayList<>();
    }

    public void internal_deleteFactory(T factory){
        remove(factory);
        if (additionalDeleteAction!=null){
            additionalDeleteAction.accept(factory, root);
        }
    }

    private void afterAdd(T added){
        if (added == null) {
            throw new IllegalStateException("cant't add null to list");
        }
        if (root!=null && added.internal().getRoot()!=root) {
            added.internal().addBackReferencesForSubtree(root,parent);
        }

        afterModify();
    }

    private void afterModify(){
        if (listeners!=null) {
            for (AttributeChangeListener<List<T>, A> listener : listeners) {
                listener.changed(ReferenceListAttribute.this, get());
            }
        }
    }

    private void afterAdd(Collection<? extends T> added){
        for (T add: added){
            afterAdd(add);
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
        boolean remove = list.remove(o);
        afterModify();
        return remove;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean result = list.addAll(c);
        afterAdd(c);
        return result;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        boolean result = list.addAll(index, c);
        afterAdd(c);
        return result;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean result = list.removeAll(c);
        afterModify();
        return result;
    }

    @Override
    public boolean removeIf(Predicate<? super T> filter) {
        boolean result = list.removeIf(filter);
        afterModify();
        return result;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return list.retainAll(c);
    }

    @Override
    public void clear() {
        list.clear();
        afterModify();
    }

    @Override
    public T get(int index) {
        return list.get(index);
    }

    @Override
    public T set(int index, T element) {
        T set = list.set(index, element);
        afterAdd(element);
        return set;
    }

    @Override
    public void add(int index, T element) {
        list.add(index,element);
        afterAdd(element);
    }

    @Override
    public T remove(int index) {
        T remove = list.remove(index);
        afterModify();
        return remove;
    }

    @Override
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return list.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return list.listIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex,toIndex);
    }

    @Override
    public Spliterator<T> spliterator() {
        return list.spliterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        list.forEach(action);
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean add(T value) {
        list.add(value);
        afterAdd(value);
        return false;
    }


}
