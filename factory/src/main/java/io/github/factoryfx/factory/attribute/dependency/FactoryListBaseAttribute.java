package io.github.factoryfx.factory.attribute.dependency;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.*;
import io.github.factoryfx.factory.metadata.AttributeMetadata;


public class FactoryListBaseAttribute<L, F extends FactoryBase<? extends L,?>,A extends FactoryListBaseAttribute<L, F,A>> extends ReferenceBaseAttribute<F,List<F>,A> implements List<F> {
    final List<F> list = new ArrayList<>();
    @JsonIgnore
    private List<F> previouslist;

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
        beforeModify();
        if (value==null){
            if (!list.isEmpty()){
                beforeRemove(list);
                this.list.clear();
                afterModify();
            }
        } else {
            beforeRemove(list);
            this.list.clear();
            this.list.addAll(value);
            afterAdd(value);
            afterModify();
        }

    }

    @Override
    public void internal_resetModification() {
        if (previouslist!=null){
            this.list.clear();
            this.list.addAll(previouslist);
            root.internal().needReFinalisation();
        }
    }

    @Override
    public void internal_clearModifyState() {
        previouslist=null;
    }

    private void beforeModify(){
        if (previouslist==null && isFinalised()){
            previouslist=new ArrayList<>(list);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void internal_copyTo(AttributeCopy<List<F>> copyAttribute, Function<FactoryBase<?,?>,FactoryBase<?,?>> newCopyInstanceProvider, int level, int maxLevel, List<FactoryBase<?, ?>> oldData, FactoryBase<?, ?> parent, FactoryBase<?, ?> root) {
        if (!isEmpty()) {
            List<F> copy = new ArrayList<>(size());
            for (F item: get()){
                if (item!=null) {
                    F itemCopy = (F)item.internal().copyDeep(newCopyInstanceProvider,level, maxLevel, oldData, parent, root);
                    if (itemCopy!=null){
                        copy.add(itemCopy);
                    }
                }
            }
            copyAttribute.set(copy);
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
    }


    private void afterAdd(Collection<? extends F> added){
        for (F add: added){
            afterAdd(add);
        }
    }

    private void beforeRemove(F removed){
        if (root!=null) {
            root.internal().addRemoved(removed);
        }
    }

    private void beforeRemove(Collection<? extends F> removed){
        for (F remove: removed){
            beforeRemove(remove);
        }
    }

    private void afterModify(){
        if (root!=null) {
            root.internal().needReFinalisation();
            this.root.internal().addModified(parent);
            if (previouslist==null){
                previouslist=new ArrayList<>(list);
            }
        }
        updateListeners(list);
    }


    @Override
    public void sort(Comparator<? super F> c) {
        beforeModify();
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
    public boolean remove(Object object) {
        beforeModify();
        beforeRemove(list.get(list.indexOf(object)));
        boolean remove = list.remove(object);
        afterModify();
        return remove;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends F> c) {
        beforeModify();
        boolean result = list.addAll(c);
        afterAdd(c);
        afterModify();
        return result;
    }

    @Override
    public boolean addAll(int index, Collection<? extends F> c) {
        beforeModify();
        boolean result = list.addAll(index, c);
        afterAdd(c);
        afterModify();
        return result;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        beforeModify();
        beforeRemove(list);
        boolean result = list.removeAll(c);
        afterModify();
        return result;
    }

    @Override
    public boolean removeIf(Predicate<? super F> filter) {
        beforeModify();
        beforeRemove(list);
        boolean result = list.removeIf(filter);
        afterModify();
        return result;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        beforeModify();
        beforeRemove(list);
        boolean retainAll = list.retainAll(c);
        afterModify();
        return retainAll;
    }

    @Override
    public void clear() {
        beforeModify();
        beforeRemove(list);
        list.clear();
        afterModify();
    }

    @Override
    public F get(int index) {
        return list.get(index);
    }

    @Override
    public F set(int index, F element) {
        beforeModify();
        F oldReplaced = list.set(index, element);
        beforeRemove(oldReplaced);
        afterAdd(element);
        afterModify();
        return oldReplaced;
    }

    @Override
    public void add(int index, F element) {
        beforeModify();
        list.add(index,element);
        afterAdd(element);
        afterModify();
    }

    @Override
    public F remove(int index) {
        beforeModify();
        F remove = list.remove(index);
        beforeRemove(remove);
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
        beforeModify();
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

    public List<PossibleNewValue<F>> internal_createNewPossibleValues(AttributeMetadata attributeMetadata){
        return internal_createNewPossibleValuesFactories(attributeMetadata).stream().map(f->new PossibleNewValue<>(this::add,f,this.root)).toList();
    }

    public List<PossibleNewValue<F>> internal_possibleValues(AttributeMetadata attributeMetadata){
        return internal_possibleValuesFactories(attributeMetadata).stream().map(f->new PossibleNewValue<>(this::add,f,this.root)).toList();
    }


}
