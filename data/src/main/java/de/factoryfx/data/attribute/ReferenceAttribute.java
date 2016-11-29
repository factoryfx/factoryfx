package de.factoryfx.data.attribute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import de.factoryfx.data.Data;
import de.factoryfx.data.merge.attribute.AttributeMergeHelper;
import de.factoryfx.data.merge.attribute.ReferenceMergeHelper;

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
    public void collectChildren(Set<Data> allModelEntities) {
        if (get() != null) {
            get().internal().collectModelEntitiesTo(allModelEntities);
        }
    }

    @Override
    public AttributeMergeHelper<?> createMergeHelper() {
        return new ReferenceMergeHelper<>(this);
    }


    @Override
    @SuppressWarnings("unchecked")
    public void fixDuplicateObjects(Function<Object, Optional<Data>> getCurrentEntity) {
        Data currentReferenceContent = get();

        if (currentReferenceContent != null) {
            currentReferenceContent.internal().fixDuplicateObjects(getCurrentEntity);
            Optional<Data> existingOptional = getCurrentEntity.apply(currentReferenceContent.getId());
            if (existingOptional.isPresent()) {
                set((T) existingOptional.get());
            }
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
        for (AttributeChangeListener<T> listener: listeners){
            listener.changed(this,value);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void copyTo(Attribute<T> copyAttribute, Function<Data, Data> dataCopyProvider) {
        copyAttribute.set((T) dataCopyProvider.apply(get()));
    }

    @Override
    public void semanticCopyTo(Attribute<T> copyAttribute, Function<Data, Data> dataCopyProvider) {
        if (copySemantic==CopySemantic.SELF){
            copyAttribute.set(get());
        } else {
            copyAttribute.set((T) dataCopyProvider.apply(get()));
        }
    }

    private CopySemantic copySemantic = CopySemantic.COPY;
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
    public void addListener(AttributeChangeListener<T> listener) {
        listeners.add(listener);
    }
    @Override
    public void removeListener(AttributeChangeListener<T> listener) {
        for (AttributeChangeListener<T> listenerItem: new ArrayList<>(listeners)){
            if (listenerItem.unwrap()==listener){
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
    public void visit(AttributeVisitor attributeVisitor) {
        attributeVisitor.reference(this);
    }

    @Override
    public AttributeTypeInfo getAttributeType() {
        return new AttributeTypeInfo(Data.class,null,null, AttributeTypeInfo.AttributeTypeCategory.REFERENCE);
    }


    private Optional<Function<Data,Collection<T>>> possibleValueProviderFromRoot=Optional.empty();
    private Optional<Supplier<T>> newValueProvider=Optional.empty();

    /**customise the list of selectable items*/
    @SuppressWarnings("unchecked")
    public <A extends ReferenceAttribute<T>> A possibleValueProvider(Function<Data,Collection<T>> possibleValueProvider){
        this.possibleValueProviderFromRoot =Optional.of(possibleValueProvider);
        return (A)this;
    }

    /**customise how new values are created*/
    @SuppressWarnings("unchecked")
    public <A extends ReferenceAttribute<T>> A newValueProvider(Supplier<T> newValueProvider){
        this.newValueProvider =Optional.of(newValueProvider);
        return (A)this;
    }

    public T addNewFactory(){
        newValueProvider.ifPresent(newFactoryFunction -> {
            T newFactory = newFactoryFunction.get();
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
        getOptional().ifPresent(data->data.internal().prepareUsage(root));
        return get();
    }

    @SuppressWarnings("unchecked")
    public List<T> possibleValues(){
        ArrayList<T> result = new ArrayList<>();
        possibleValueProviderFromRoot.ifPresent(factoryBaseListFunction -> {
            Collection<T> factories = factoryBaseListFunction.apply(root);
            factories.forEach(factory -> result.add(factory));
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
    public void prepareUsage(Data root, Data parent){
        this.root=root;
    }

    private boolean userEditable=true;
    @SuppressWarnings("unchecked")
    /** marks the reference as readonly for the user(user can still anviagte but change the refernce)*/
    public <A extends ReferenceAttribute<T>> A userReadOnly(){
        userEditable=false;
        return (A)this;
    }

    @JsonIgnore
    public boolean isUserEditable(){
        return userEditable;
    }

    private boolean userSelectable=true;
    /**
     * disable select for reference (slect dialog)
     */
    @SuppressWarnings("unchecked")
    public <A extends ReferenceAttribute<T>> A userNotSelectable(){
        userSelectable=false;
        return (A)this;
    }

    @JsonIgnore
    public boolean isUserSelectable(){
        return userSelectable;
    }

}
