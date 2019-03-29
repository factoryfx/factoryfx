package io.github.factoryfx.factory.attribute.dependency;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.FactoryTreeBuilderBasedAttributeSetup;
import io.github.factoryfx.factory.attribute.*;


public abstract class FactoryReferenceListBaseAttribute<R extends FactoryBase<?,R>,L, F extends FactoryBase<? extends L,R>,A extends FactoryReferenceListBaseAttribute<R,L, F,A>> extends ReferenceBaseAttribute<R, F,List<F>,A> implements List<F> {
    final List<F> list = new ArrayList<>();

    public FactoryReferenceListBaseAttribute() {
        super();
    }

    public List<L> instances(){
        ArrayList<L> result = new ArrayList<>(this.size());
        for(F item: this){
            result.add(item.internal().instance());
        }
        return result;
    }

    public L instances(Predicate<F> filter){
        Optional<F> any = get().stream().filter(filter).findAny();
        return any.map(t -> t.internal().instance()).orElse(null);
    }

    @Override
    public boolean internal_mergeMatch(List<F> value) {
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

    private boolean referenceEquals(FactoryBase<?,?> ref1, FactoryBase<?,?> ref2) {
        if (ref1 == null && ref2 == null) {
            return true;
        }
        if (ref1 == null || ref2 == null) {
            return false;
        }
        return ref1.idEquals(ref2);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void internal_merge(Attribute<?,?> newValue) {
        Map<String, F> previousMap=new HashMap();
        for (F item : this.list) {
            previousMap.put(item.getId(),item);
        }

        this.list.clear();
        List<F> newList = (List<F>) newValue.get();

        for (F newItem : newList) {
            F oldItem = previousMap.get(newItem.getId());
            if (oldItem!=null){
                this.list.add(oldItem);
            } else {
                this.list.add(newItem);
            }
        }

        afterModify();
    }


    @Override
    @SuppressWarnings("unchecked")
    public void internal_fixDuplicateObjects(Map<String, FactoryBase<?,?>> idToDataMap) {

        List<F> fixedList = new ArrayList<>();
        for (F entity : this.list) {
            fixedList.add((F)idToDataMap.get(entity.getId()));
        }

        this.list.clear();
        this.list.addAll(fixedList);
    }

    @Override
    public List<F> get() {
        return this;
    }


    /** set list only take the list items not the list itself, (to simplify ChangeListeners)*/
    @Override
    public void set(List<F> value) {
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
    public void internal_copyTo(A copyAttribute, final int level, final int maxLevel, final List<FactoryBase<?,R>> oldData, FactoryBase<?,R> parent, R root) {
        for (F item: get()){
            F itemCopy = null;
            if (item!=null) {
                itemCopy = (F)item.internal().copyDeep(level, maxLevel, oldData, parent, root);
            }

            if (itemCopy!=null){
                copyAttribute.get().add(itemCopy);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void internal_semanticCopyTo(A copyAttribute) {
        if (getCopySemantic()== CopySemantic.SELF){
            copyAttribute.set(get());
        } else {
            for (F item: get()){
                final F itemCopy = (F)item.utility().semanticCopy();
                if (itemCopy!=null){
                    copyAttribute.get().add(itemCopy);
                }
            }
        }
    }

    public List<F> filtered(Predicate<F> predicate) {
        return get().stream().filter(predicate).collect(Collectors.toList());
    }

    List<AttributeChangeListener<List<F>,A>> listeners;
    @Override
    public void internal_addListener(AttributeChangeListener<List<F>,A> listener) {
        if (listeners==null){
            listeners=new ArrayList<>();
        }
        listeners.add(listener);
    }
    @Override
    public void internal_removeListener(AttributeChangeListener<List<F>,A> listener) {
        if (listeners!=null){
            for (AttributeChangeListener<List<F>,A> listenerItem: new ArrayList<>(listeners)){
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

    @SuppressWarnings("unchecked")
    public List<F> internal_createNewPossibleValues(){
        FactoryTreeBuilderBasedAttributeSetup factoryTreeBuilderBasedAttributeSetup = root.internal().getFactoryTreeBuilderBasedAttributeSetup();
        if (factoryTreeBuilderBasedAttributeSetup!=null){
            return factoryTreeBuilderBasedAttributeSetup.createNewFactory(clazz);
        }
        if (newValuesProviderFromRootAndAttribute!=null) {
            return newValuesProviderFromRootAndAttribute.apply(root,(A)this);
        }
        if (getNewValueProvider()!=null) {
            return Collections.singletonList(getNewValueProvider().apply(root));
        }
        return new ArrayList<>();
    }

    public void internal_deleteFactory(F factory){
        remove(factory);
        if (additionalDeleteAction!=null){
            additionalDeleteAction.accept(factory, root);
        }
    }

    private void afterAdd(F added){
        if (added == null) {
            throw new IllegalStateException("cant't add null to list");
        }
        if (root!=null && added.internal().getRoot()!=root) {
            added.internal().addBackReferencesForSubtreeUnsafe(root,parent);
        }

        afterModify();
    }

    private void afterModify(){
        if (listeners!=null) {
            for (AttributeChangeListener<List<F>, A> listener : listeners) {
                listener.changed(FactoryReferenceListBaseAttribute.this, get());
            }
        }
    }

    private void afterAdd(Collection<? extends F> added){
        for (F add: added){
            afterAdd(add);
        }
    }

    @Override
    public void sort(Comparator<? super F> c) {
        list.sort(c);
        afterModify();
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
    public Iterator<F> iterator() {
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
    public Stream<F> stream() {
        return list.stream();
    }

    @Override
    public Stream<F> parallelStream() {
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
    public boolean addAll(Collection<? extends F> c) {
        boolean result = list.addAll(c);
        afterAdd(c);
        return result;
    }

    @Override
    public boolean addAll(int index, Collection<? extends F> c) {
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
    public boolean removeIf(Predicate<? super F> filter) {
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
    public F get(int index) {
        return list.get(index);
    }

    @Override
    public F set(int index, F element) {
        F set = list.set(index, element);
        afterAdd(element);
        return set;
    }

    @Override
    public void add(int index, F element) {
        list.add(index,element);
        afterAdd(element);
    }

    @Override
    public F remove(int index) {
        F remove = list.remove(index);
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
    public ListIterator<F> listIterator() {
        return list.listIterator();
    }

    @Override
    public ListIterator<F> listIterator(int index) {
        return list.listIterator(index);
    }

    @Override
    public List<F> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex,toIndex);
    }

    @Override
    public Spliterator<F> spliterator() {
        return list.spliterator();
    }

    @Override
    public void forEach(Consumer<? super F> action) {
        list.forEach(action);
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean add(F value) {
        list.add(value);
        afterAdd(value);
        return false;
    }

    @JsonIgnore
    @SuppressWarnings("unchecked")
    public A defaultExpanded(){
        this.defaultExpanded=true;
        return (A)this;
    }

    @JsonIgnore
    private boolean defaultExpanded =false;

    /**
     * edit hint to show list initial expanded
     * @return self
     * */
    @JsonIgnore
    public boolean internal_isDefaultExpanded(){
        return defaultExpanded;
    }

}
