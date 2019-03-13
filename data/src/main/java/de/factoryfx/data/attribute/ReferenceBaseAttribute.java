package de.factoryfx.data.attribute;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.factoryfx.data.Data;
import de.factoryfx.data.storage.migration.metadata.AttributeStorageMetadata;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/** Base for Reference attributes, with common api  */
public abstract class ReferenceBaseAttribute<T extends Data, U, A extends ReferenceBaseAttribute<T,U,A>> extends Attribute<U,A> {

    protected Data root;
    protected Data parent;//data that contains this attribute
//    protected Class<T> containingFactoryClass;



    public ReferenceBaseAttribute() {
        super();
    }

//    /**
//     * @param attributeMetadata AttributeMetadata
//     * @param containingFactoryClass class off the factory in this Attribute, workaround for java generics Type Erasure.<br>( workaround for generic parameter ReferenceAttribute<Example<V>> webGuiResource=new ReferenceAttribute(Example<V>) )
//     */
//    @SuppressWarnings("unchecked")
//    public ReferenceBaseAttribute(AttributeMetadata attributeMetadata, Class containingFactoryClass) {
//        super(attributeMetadata);
//        this.containingFactoryClass = containingFactoryClass;
//    }

    private Function<Data,Collection<T>> possibleValueProviderFromRoot;

    /**customise the list of selectable items
     * @param provider provider
     * @return self*/
    @SuppressWarnings("unchecked")
    public A possibleValueProvider(Function<Data,Collection<T>> provider){
        possibleValueProviderFromRoot=provider;
        return (A)this;
    }

    public Collection<T> internal_possibleValues(){
        if (clazz!=null && possibleValueProviderFromRoot==null){
            possibleValueProviderFromRoot=new DefaultPossibleValueProvider<>(clazz);
        }
        if (possibleValueProviderFromRoot!=null){
            return possibleValueProviderFromRoot.apply(root);
        }
        return new ArrayList<>();
    }

    @Override
    public void internal_addBackReferences(Data root, Data parent){
        this.root=root;
        this.parent=parent;
    }

    private Function<Data,T> newValueProvider;
    /**
     * customise how new values are created
     * @param newValueProviderFromRoot value, root
     * @return the new added factory
     */
    @SuppressWarnings("unchecked")
    public A newValueProvider(Function<Data,T> newValueProviderFromRoot){
        this.newValueProvider =newValueProviderFromRoot;
        return (A)this;
    }

    protected Function<Data,T> getNewValueProvider(){
        if (clazz!=null && newValueProvider==null){
            newValueProvider=new DefaultNewValueProvider<>(clazz);
        }
        return newValueProvider;
    }

    BiFunction<Data,A,List<T>> newValuesProviderFromRootAndAttribute;
    /**
     * customise how new values are created
     * @param newValuesProvider value, root
     * @return the new added factory
     *
     * Deprecated use newValuesProvider(BiFunction... ) instead
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public A newValuesProvider(Function<Data,List<T>> newValuesProvider){
        this.newValuesProviderFromRootAndAttribute = (root, attribute)->{
            return newValuesProvider.apply(root);
        };
        return (A)this;
    }

    /**
     * customise how new values are created
     * @param newValuesProviderFromRootAndAttribute root, attribute to List.of(T)
     * @return the new added factory
     */
    @SuppressWarnings("unchecked")
    public A newValuesProvider(BiFunction<Data,A,List<T>> newValuesProviderFromRootAndAttribute){
        this.newValuesProviderFromRootAndAttribute = newValuesProviderFromRootAndAttribute;
        return (A)this;
    }

    protected BiConsumer<T,Data> additionalDeleteAction;
    /**
     * action after delete, e.g delete the factory also in other lists
     * @param additionalDeleteAction deleted value, root
     * @return self
     */
    @SuppressWarnings("unchecked")
    public A additionalDeleteAction(BiConsumer<T,Data> additionalDeleteAction){
        this.additionalDeleteAction=additionalDeleteAction;
        return (A)this;
    }

    private boolean userSelectable=true;
    /**
     * disable select for reference, used in gui to disable the select button so that the user can't select new factories in this attribute
     * @return self
     */
    @SuppressWarnings("unchecked")
    public A userNotSelectable(){
        userSelectable=false;
        return (A)this;
    }

    @JsonIgnore
    public boolean internal_isUserSelectable(){
        return userSelectable;
    }


    private boolean userCreatable =true;
    /**
     * disable new for reference, used in gui to disable the new button so that the user can't create new factories in this attribute
     * @return self
     */
    @SuppressWarnings("unchecked")
    public A userNotCreatable(){
        userCreatable =false;
        return (A)this;
    }

    @JsonIgnore
    public boolean internal_isUserCreatable(){
        return userCreatable;
    }

    private CopySemantic copySemantic;

    @JsonIgnore
    protected CopySemantic getCopySemantic(){
        return Objects.requireNonNullElse(copySemantic, CopySemantic.COPY);
    }

    /**
     * @see Data.DataUtility#semanticCopy()
     *
     * @param copySemantic copySemantic
     * @return self*/
    @SuppressWarnings("unchecked")
    public A setCopySemantic(CopySemantic copySemantic){
        this.copySemantic=copySemantic;
        return (A)this;
    }

    protected Class<T> clazz;
    /**setup value selection and new value adding for user editing
     * @param clazz class
     * @return self*/
    @SuppressWarnings("unchecked")
    protected A setup(Class<T> clazz){
        this.clazz=clazz;//lazy creation for performance
        return (A)this;
    }

    public Class<T> internal_getReferenceClass(){
        return clazz;
    }

    /** workaround for java limitations width nested generic parameter e.g.: Class&lt;TypA&lt;TypB&gt;&gt;  <br>
     * if possible use {@link #setup} instead
     * @param clazz class
     * @return self
     */
    @SuppressWarnings("unchecked")
    protected A setupUnsafe(Class clazz){
        if (!Data.class.isAssignableFrom(clazz)){
            throw new IllegalArgumentException(clazz+" is no data class, maybe mixed up with liveobject class");
        }
        return setup((Class<T>)clazz);
    }

    /**
     * reference is a selection from a catalogue
     * @return self
     */
    @SuppressWarnings("unchecked")
    public A catalogueBased(){
        catalogueBased=true;
        setCopySemantic(CopySemantic.SELF);
        return (A)this;
    }

    @JsonIgnore
    public boolean internal_isCatalogueBased(){
        return catalogueBased;
    }

    private boolean catalogueBased =false;

    @Override
    public AttributeStorageMetadata createAttributeStorageMetadata(String variableName) {
        return new AttributeStorageMetadata(variableName,getClass().getName(),true, clazz.getName());
    }
}
