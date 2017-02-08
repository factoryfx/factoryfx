package de.factoryfx.data.attribute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.factoryfx.data.Data;
import de.factoryfx.data.jackson.ObservableListJacksonAbleWrapper;
import de.factoryfx.data.merge.attribute.AttributeMergeHelper;
import de.factoryfx.data.merge.attribute.ReferenceListMergeHelper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class ReferenceListAttribute<T extends Data> extends Attribute<List<T>> {
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

    @JsonCreator
    protected ReferenceListAttribute(ObservableListJacksonAbleWrapper<T> list) {
        super(null);
        this.list = list.unwrap();
    }

    public boolean add(T value) {
        if (value==null){
            throw new IllegalStateException("cant't add null to list");
        }
        get().add(value);
        return false;
    }

    @Override
    public void internal_collectChildren(Set<Data> allModelEntities) {
        list.forEach(entity -> entity.internal().collectModelEntitiesTo(allModelEntities));
    }

    @Override
    public AttributeMergeHelper<?> internal_createMergeHelper() {
        return new ReferenceListMergeHelper<>(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void internal_fixDuplicateObjects(Map<Object, Data> idToDataMap) {
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
    @SuppressWarnings("unchecked")
    public void internal_semanticCopyTo(Attribute<List<T>> copyAttribute, Function<Data,Data> dataCopyProvider) {
        if (copySemantic==CopySemantic.SELF){
            copyAttribute.set(get());
        } else {
            for (T item: get()){
                final T itemCopy = (T) dataCopyProvider.apply(item);
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

    public boolean contains(T value) {
        return get().contains(value);
    }

    public void forEach(Consumer<? super T> action) {
        get().forEach(action);
    }

    public T get(int i) {
        return list.get(i);
    }

    @JsonProperty
    ObservableList<T> getList() {
        return list;
    }

    @JsonProperty
    void setList(ObservableList<T> list) {
        this.list = ((ObservableListJacksonAbleWrapper<T>)list).unwrap();
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

    public List<T> filtered(Predicate<T> predicate) {
        return get().stream().filter(predicate).collect(Collectors.toList());
    }

    List<AttributeChangeListener<List<T>>> listeners= new ArrayList<>();
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
        StringBuilder stringBuilder = new StringBuilder("List (number of entries: "+ list.size()+")\n");
        for (T item:  list){
            stringBuilder.append(item.internal().getDisplayText());
            stringBuilder.append(",\n");
        }
        return stringBuilder.toString();
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
    private Optional<Supplier<T>> newValueProvider =Optional.empty();

    /**customise the list of selectable items*/
    @SuppressWarnings("unchecked")
    public <A extends ReferenceListAttribute<T>> A possibleValueProvider(Function<Data,Collection<T>> provider){
        possibleValueProviderFromRoot=Optional.of(provider);
        return (A)this;
    }

    /**customise how new values are created*/
    @SuppressWarnings("unchecked")
    public <A extends ReferenceListAttribute<T>> A newValueProvider(Supplier<T> newValueProvider){
        this.newValueProvider =Optional.of(newValueProvider);
        return (A)this;
    }

    public T addNewFactory(){
        T addedFactory=null;
        if (newValueProvider.isPresent()) {
            T newFactory = newValueProvider.get().get();
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

    public void addFactory(T addedFactory){
        get().add(addedFactory);
        for (Data data: get()){
            data.internal().propagateRoot(root);
        }
    }

    @SuppressWarnings("unchecked")
    public Collection<T> possibleValues(){
        Set<T> result = new HashSet<T>();
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
        this.root=root;;
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
    public <A extends ReferenceListAttribute<T>> A userNotCreateable(){
        userCreateable=false;
        return (A)this;
    }


    private boolean userCreateable=true;
    @JsonIgnore
    public boolean internal_isUserCreateable(){
        return userCreateable;
    }

}
