package de.factoryfx.data.attribute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import de.factoryfx.data.Data;
import de.factoryfx.data.merge.attribute.AttributeMergeHelper;

public class ReferenceAttribute<T extends Data> extends Attribute<T> {

    private Data root;

    private T value;
    private Optional<Class<T>> clazz;

    @JsonCreator
    protected ReferenceAttribute(T value) {
        super(null);
        set(value);
    }

    public ReferenceAttribute(Class<T> clazz, AttributeMetadata attributeMetadata) {
        super(attributeMetadata);
        this.clazz=Optional.ofNullable(clazz);
    }

    @SuppressWarnings("unchecked")
    //workaround for generic parameter ReferenceAttribute<Example<V>> webGuiResource=new ReferenceAttribute(Example<V>)
    public ReferenceAttribute(AttributeMetadata attributeMetadata, Class clazz) {
        super(attributeMetadata);
        this.clazz=Optional.ofNullable(clazz);
    }

    @Override
    public void internal_collectChildren(Set<Data> allModelEntities) {
        if (get() != null) {
            get().internal().collectModelEntitiesTo(allModelEntities);
        }
    }

    @Override
    public boolean internal_match(T value) {
        if (this.value == null && value == null) {
            return true;
        }
        if (this.value == null || value == null) {
            return false;
        }
        return this.value.getId().equals(value.getId());
    }

    @Override
    public AttributeMergeHelper<?> internal_createMergeHelper() {
        return new AttributeMergeHelper<>(this);
    }


    @Override
    @SuppressWarnings("unchecked")
    public void internal_fixDuplicateObjects(Map<String, Data> idToDataMap) {
        Data currentReferenceContent = get();

        if (currentReferenceContent != null) {
            set((T)idToDataMap.get(currentReferenceContent.getId()));
        }
    }

    @Override
    public T get() {
        return value;
    }

    public Optional<T> getOptional() {
        return Optional.ofNullable(value);
    }


    @Override
    public void set(T value) {
        this.value=value;
        if (root!=null && value!=null && value.internal().getRoot()!=root) {
            value.internal().propagateRoot(root);
        }
        for (AttributeChangeListener<T> listener: listeners){
            listener.changed(this,value);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void internal_copyTo(Attribute<T> copyAttribute, Function<Data, Data> dataCopyProvider) {
        copyAttribute.set((T) dataCopyProvider.apply(get()));
    }

    @Override
    public void internal_semanticCopyTo(Attribute<T> copyAttribute) {
        if (get()!=null){
            if (copySemantic==CopySemantic.SELF){
                copyAttribute.set(get());
            } else {
                copyAttribute.set(get().internal().semanticCopy());
            }
        }

    }

    private CopySemantic copySemantic = CopySemantic.COPY;

    /** @see Data.DataUtility#semanticCopy() */
    @SuppressWarnings("unchecked")
    public <A extends ReferenceAttribute<T>> A setCopySemantic(CopySemantic copySemantic){
        this.copySemantic=copySemantic;
        return (A)this;
    }

    @JsonValue
    T getValue() {
        return value;
    }

    @JsonValue
    void setValue(T value) {
        this.value = value;
    }

    List<AttributeChangeListener<T>> listeners= new ArrayList<>();
    @Override
    public void internal_addListener(AttributeChangeListener<T> listener) {
        listeners.add(listener);
    }
    @Override
    public void internal_removeListener(AttributeChangeListener<T> listener) {
        for (AttributeChangeListener<T> listenerItem: new ArrayList<>(listeners)){
            if (listenerItem.unwrap()==listener ||  listenerItem.unwrap()==null){
                listeners.remove(listenerItem);
            }
        }
    }

    @Override
    public String getDisplayText() {
        String referenceDisplayText = "empty";
        if (value!=null){
            referenceDisplayText=value.internal().getDisplayText();
        }
        return referenceDisplayText;
    }

    @Override
    public void internal_visit(AttributeVisitor attributeVisitor) {
        attributeVisitor.reference(this);
    }

    @Override
    public AttributeTypeInfo internal_getAttributeType() {
        return new AttributeTypeInfo(Data.class,null,null, AttributeTypeInfo.AttributeTypeCategory.REFERENCE);
    }


    private Optional<Function<Data,Collection<T>>> possibleValueProviderFromRoot=Optional.empty();
    private Optional<Function<Data,T>> newValueProvider=Optional.empty();
    private Optional<BiConsumer<T,Object>> additionalDeleteAction = Optional.empty();

    /**customise the list of selectable items*/
    @SuppressWarnings("unchecked")
    public <A extends ReferenceAttribute<T>> A possibleValueProvider(Function<Data,Collection<T>> possibleValueProvider){
        this.possibleValueProviderFromRoot =Optional.of(possibleValueProvider);
        return (A)this;
    }

    /**customise how new values are created*/
    @SuppressWarnings("unchecked")
    public <A extends ReferenceAttribute<T>> A newValueProvider(Function<Data,T> newValueProviderFromRoot){
        this.newValueProvider =Optional.of(newValueProviderFromRoot);
        return (A)this;
    }

    /**
     * action after delete e.g delete the factory also in other lists
     * @param additionalDeleteAction deleted value, root
     * @return this
     */
    @SuppressWarnings("unchecked")
    public <A extends ReferenceAttribute<T>> A additionalDeleteAction(BiConsumer<T,Object> additionalDeleteAction){
        this.additionalDeleteAction =Optional.of(additionalDeleteAction);
        return (A)this;
    }

    public void internal_deleteFactory(){
        T removedFactory=get();
        set(null);
        additionalDeleteAction.ifPresent(bc -> bc.accept(removedFactory, root));
    }

    public T internal_addNewFactory(){
        newValueProvider.ifPresent(newFactoryFunction -> {
            T newFactory = newFactoryFunction.apply(root);
            set(newFactory);
        });
        if (!newValueProvider.isPresent()){
            if (clazz.isPresent()){
                try {
                    set(clazz.get().newInstance());
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            } else {
                set(null);
            }

        }
        getOptional().ifPresent(data->data.internal().propagateRoot(root));
        return get();
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
                if (clazz.isPresent()){
                    if (clazz.get().isAssignableFrom(factory.getClass())){
                        result.add((T) factory);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public void internal_prepareUsage(Data root){
        this.root=root;
    }

    private boolean userEditable=true;
    /** marks the reference as readonly for the user(user can still navigate the reference  but can not change the it)*/
    @SuppressWarnings("unchecked")
    public <A extends ReferenceAttribute<T>> A userReadOnly(){
        userEditable=false;
        return (A)this;
    }

    @JsonIgnore
    public boolean internal_isUserEditable(){
        return userEditable;
    }

    private boolean userSelectable=true;
    /**
     * disable select for reference (select dialog)
     */
    @SuppressWarnings("unchecked")
    public <A extends ReferenceAttribute<T>> A userNotSelectable(){
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
    public <A extends ReferenceAttribute<T>> A userNotCreatable(){
        userCreatable =false;
        return (A)this;
    }


    private boolean userCreatable =true;
    @JsonIgnore
    public boolean internal_isUserCreatable(){
        return userCreatable;
    }

}
