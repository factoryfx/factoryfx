package io.github.factoryfx.factory.attribute.dependency;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.*;


public class FactoryListBaseAttribute<L, F extends FactoryBase<? extends L,?>,A extends FactoryListBaseAttribute<L, F,A>> extends ReferenceBaseAttribute<F,List<F>,A> implements List<F> {
    final List<F> list = new ArrayList<>();

    public FactoryListBaseAttribute() {
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
    public boolean internal_mergeMatch(AttributeMatch<List<F>> value) {
        return internal_referenceListEquals(list,value.get());
    }

    @Override
    public void internal_merge(List<F> newList) {
        internal_mergeFactoryList(this.list,newList);
    }


    @Override
    @SuppressWarnings("unchecked")
    public <RL extends FactoryBase<?,RL>> void internal_fixDuplicateObjects(Map<UUID, FactoryBase<?,RL>> idToDataMap) {

        List<F> fixedList = new ArrayList<>(this.list.size());
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
            afterModify();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void internal_copyTo(AttributeCopy<List<F>> copyAttribute, int level, int maxLevel, List<FactoryBase<?, ?>> oldData, FactoryBase<?, ?> parent, FactoryBase<?, ?> root) {
        if (!isEmpty()) {
            List<F> copy = new ArrayList<>(size());
            for (F item: get()){
                if (item!=null) {
                    F itemCopy = (F)item.internal().copyDeep(level, maxLevel, oldData, parent, root);
                    if (itemCopy!=null){
                        copy.add(itemCopy);
                    }
                }
            }
            copyAttribute.set(copy);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void internal_semanticCopyTo(AttributeCopy<List<F>> copyAttribute) {
        if (internal_getCopySemantic()== CopySemantic.SELF){
            copyAttribute.set(get());
        } else {
            List<F> result = new ArrayList<>();
            for (F item: get()){
                final F itemCopy = (F)item.utility().semanticCopy();
                if (itemCopy!=null){
                    result.add(itemCopy);
                }
            }
            copyAttribute.set(result);
        }
    }

    public List<F> filtered(Predicate<F> predicate) {
        return get().stream().filter(predicate).collect(Collectors.toList());
    }

    @Override
    public String getDisplayText() {
        return new CollectionAttributeUtil<>(get(), t -> t.internal().getDisplayText()).getDisplayText();
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
        if (root!=null) {
            added.internal().setRootDeepUnchecked(root);
        }
    }


    private void afterAdd(Collection<? extends F> added){
        for (F add: added){
            afterAdd(add);
        }
    }

    private void afterModify(){
        if (root!=null) {
            root.internal().needRecalculationForBackReferences();
        }
        updateListeners(list);
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
        afterModify();
        return result;
    }

    @Override
    public boolean addAll(int index, Collection<? extends F> c) {
        boolean result = list.addAll(index, c);
        afterAdd(c);
        afterModify();
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
        afterModify();
        return set;
    }

    @Override
    public void add(int index, F element) {
        list.add(index,element);
        afterAdd(element);
        afterModify();
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
        afterModify();
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

    @Override
    public void internal_visitChildren(Consumer<FactoryBase<?, ?>> consumer, boolean includeViews) {
        for (F factory : list) {
            consumer.accept(factory);
        }
    }

    /**
     * add is costly with the change detection and root back reference adding
     * this halt the change detection until batch is ended;
     * @param batchAction batchAction
     */
    public void batchModify(Consumer<List<F>> batchAction){
        batchAction.accept(this.list);
        afterAdd(list);
        afterModify();

    }


}
