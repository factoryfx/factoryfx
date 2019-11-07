package io.github.factoryfx.factory.attribute.dependency;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.FactoryTreeBuilderBasedAttributeSetup;
import io.github.factoryfx.factory.attribute.*;
import io.github.factoryfx.factory.metadata.FactoryMetadataManager;
import io.github.factoryfx.factory.storage.migration.metadata.AttributeStorageMetadata;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/** Base for Reference attributes, with common api  */
public abstract class ReferenceBaseAttribute<F extends FactoryBase<?,?>, U, A extends ReferenceBaseAttribute<F,U,A>> extends Attribute<U,A> implements FactoryChildrenEnclosingAttribute {

    FactoryBase<?,?> root;
    protected FactoryBase<?,?> parent;//data that contains this attribute
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

    private Function<FactoryBase<?,?>,Collection<F>> possibleValueProviderFromRoot;

    /**customise the list of selectable items
     * @param provider provider
     * @return self*/
    @SuppressWarnings("unchecked")
    public A possibleValueProvider(Function<FactoryBase<?,?>,Collection<F>> provider){
        possibleValueProviderFromRoot=provider;
        return (A)this;
    }

    public Collection<F> internal_possibleValues(){
        if (clazz!=null && possibleValueProviderFromRoot==null){
            possibleValueProviderFromRoot=new DefaultPossibleValueProvider<>(clazz);
        }
        if (possibleValueProviderFromRoot!=null){
            return possibleValueProviderFromRoot.apply(root);
        }
        return new ArrayList<>();
    }

    @Override
    public void internal_addBackReferences( FactoryBase<?,?> root, FactoryBase<?,?> parent){
        this.root=root;
        this.parent=parent;
    }

    BiFunction<FactoryBase<?,?>,A,List<F>> newValuesProviderFromRootAndAttribute;
    /**
     * customise how new values are created
     * @param newValuesProvider value, root
     * @return the new added factory
     *
     * Deprecated use newValuesProvider(BiFunction... ) instead
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public A newValuesProvider(Function<FactoryBase<?,?>,List<F>> newValuesProvider){
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
    public A newValuesProvider(BiFunction<FactoryBase<?,?>,A,List<F>> newValuesProviderFromRootAndAttribute){
        this.newValuesProviderFromRootAndAttribute = newValuesProviderFromRootAndAttribute;
        return (A)this;
    }

    protected BiConsumer<F,FactoryBase<?,?>> additionalDeleteAction;
    /**
     * action after delete, e.g delete the factory also in other lists
     * @param additionalDeleteAction deleted value, root
     * @return self
     */
    @SuppressWarnings("unchecked")
    public A additionalDeleteAction(BiConsumer<F,FactoryBase<?,?>> additionalDeleteAction){
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
    public CopySemantic internal_getCopySemantic(){
        return Objects.requireNonNullElse(copySemantic, CopySemantic.COPY);
    }

    /**
     * @see FactoryBase.UtilityFactory#semanticCopy()
     *
     * @param copySemantic copySemantic
     * @return self*/
    @SuppressWarnings("unchecked")
    public A setCopySemantic(CopySemantic copySemantic){
        this.copySemantic=copySemantic;
        return (A)this;
    }

    protected Class<F> clazz;

    /**setup value selection and new value adding for user editing
     * @param clazz class
     * */
    @SuppressWarnings("unchecked")
    @Override
    public void internal_setReferenceClass(Class<?> clazz){
        this.clazz=(Class<F>)clazz;
    }

    public Class<F> internal_getReferenceClass(){
        return clazz;
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
        return new AttributeStorageMetadata(variableName,getClass().getName(), clazz!=null?clazz.getName():null);
    }

    @SuppressWarnings("unchecked")
    public List<F> internal_createNewPossibleValues(){
        if (newValuesProviderFromRootAndAttribute!=null) {
            return newValuesProviderFromRootAndAttribute.apply(root,(A)this);
        }
        FactoryTreeBuilderBasedAttributeSetup factoryTreeBuilderBasedAttributeSetup = root.internal().getFactoryTreeBuilderBasedAttributeSetup();
        if (factoryTreeBuilderBasedAttributeSetup!=null && clazz !=null ){
            return factoryTreeBuilderBasedAttributeSetup.createNewFactory(clazz);
        }
        if (clazz !=null ){
            return List.of((F)FactoryMetadataManager.getMetadataUnsafe(clazz).newInstance());
        }
        return new ArrayList<>();
    }

    @Override
    public void internal_reset(){
        this.root=null;
        this.parent=null;
        this.internal_removeAllListener();
    }

}
