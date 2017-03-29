package de.factoryfx.data.attribute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import de.factoryfx.data.Data;
import de.factoryfx.data.merge.attribute.AttributeMergeHelper;
import de.factoryfx.data.merge.attribute.ReferenceListMergeHelper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class ReferenceListAttribute<T extends Data> extends Attribute<List<T>> implements Collection<T> {
    private Data root;

    ObservableList<T> list = FXCollections.observableArrayList();
    private Class<T> clazz;

    public ReferenceListAttribute(Class<T> clazz, AttributeMetadata attributeMetadata) {
        super(attributeMetadata);
        this.clazz=clazz;

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

    @JsonCreator
    protected ReferenceListAttribute() {
        super(null);
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
    public AttributeMergeHelper<?> internal_createMergeHelper() {
        return new ReferenceListMergeHelper<>(this);
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


    //** set list only take the list items not the list itself, (to simplify ChangeListeners)*/
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
                final T itemCopy = item.internal().copy();
                if (itemCopy!=null){
                    copyAttribute.get().add(itemCopy);
                }
            }
        }
    }

    private CopySemantic copySemantic = CopySemantic.COPY;
    @SuppressWarnings("unchecked")
    public <A extends ReferenceListAttribute<T>> A setCopySemantic(CopySemantic copySemantic){
        this.copySemantic=copySemantic;
        return (A)this;
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
        return new AttributeTypeInfo(ObservableList.class,null,null,Data.class, AttributeTypeInfo.AttributeTypeCategory.REFERENCE_LIST, null);
    }


    private Optional<Function<Data,Collection<T>>> possibleValueProviderFromRoot=Optional.empty();
    private Optional<Function<Data,T>> newValueProvider =Optional.empty();
    private Optional<BiConsumer<T,Object>> additionalDeleteAction = Optional.empty();

    /**customise the list of selectable items*/
    @SuppressWarnings("unchecked")
    public <A extends ReferenceListAttribute<T>> A possibleValueProvider(Function<Data,Collection<T>> provider){
        possibleValueProviderFromRoot=Optional.of(provider);
        return (A)this;
    }

    /**customise how new values are created*/
    @SuppressWarnings("unchecked")
    public <A extends ReferenceListAttribute<T>> A newValueProvider(Function<Data,T> newValueProviderFromRoot){
        this.newValueProvider =Optional.of(newValueProviderFromRoot);
        return (A)this;
    }

    public T internal_addNewFactory(){
        T addedFactory=null;
        if (newValueProvider.isPresent()) {
            T newFactory = newValueProvider.get().apply(root);
            get().add(newFactory);
            addedFactory = newFactory;
        }

        if (!newValueProvider.isPresent()){
            try {
                T newFactory = clazz.newInstance();
                get().add(newFactory);
                addedFactory = newFactory;
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        for (Data data: get()){
            data.internal().propagateRoot(root);
        }

        return addedFactory;
    }

    /**
     * action after delete e.g delete the factory also in other lists
     * @param additionalDeleteAction deleted value, root
     * @return this
     */
    @SuppressWarnings("unchecked")
    public <A extends ReferenceListAttribute<T>> A additionalDeleteAction(BiConsumer<T,Object> additionalDeleteAction){
        this.additionalDeleteAction =Optional.of(additionalDeleteAction);
        return (A)this;
    }

    public void internal_deleteFactory(T factory){
        list.remove(factory);
        additionalDeleteAction.ifPresent(bc -> bc.accept(factory, root));
    }

    @SuppressWarnings("unchecked")
    public Collection<T> internal_possibleValues(){
        Set<T> result = new HashSet<>();
        possibleValueProviderFromRoot.ifPresent(factoryBaseListFunction -> {
            Collection<T> factories = factoryBaseListFunction.apply(root);
            factories.forEach(result::add);
        });
        if (!possibleValueProviderFromRoot.isPresent()){
            for (Data factory: root.internal().collectChildrenDeep()){
                if (clazz.isAssignableFrom(factory.getClass())){
                    result.add((T) factory);
                }
            }
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void internal_prepareUsage(Data root){
        this.root=root;
    }

    private boolean userEditable=true;
    /**
     * marks the reference as readonly for the user(user can still navigate but not change the reference)
     */
    @SuppressWarnings("unchecked")
    public <A extends ReferenceListAttribute<T>> A userReadOnly(){
        userEditable=false;
        return (A)this;
    }

    @JsonIgnore
    public boolean internal_isUserEditable(){
        return userEditable;
    }

    private boolean userSelectable=true;
    /**
     * disable select for reference (slect dialog)
     */
    @SuppressWarnings("unchecked")
    public <A extends ReferenceListAttribute<T>> A userNotSelectable(){
        userSelectable=false;
        return (A)this;
    }

    @JsonIgnore
    public boolean internal_isUserSelectable(){
        return userSelectable;
    }

    /**
     * disable new for reference
     */
    @SuppressWarnings("unchecked")
    public <A extends ReferenceListAttribute<T>> A userNotCreatable(){
        userCreatable =false;
        return (A)this;
    }


    private boolean userCreatable =true;
    @JsonIgnore
    public boolean internal_isUserCreatable(){
        return userCreatable;
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
        return list.contains(c);
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
