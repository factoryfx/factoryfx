package io.github.factoryfx.factory.metadata;

import io.github.factoryfx.factory.*;
import io.github.factoryfx.factory.attribute.types.EnumAttribute;
import io.github.factoryfx.factory.attribute.types.EnumListAttribute;
import io.github.factoryfx.factory.fastfactory.FastFactoryUtility;
import io.github.factoryfx.factory.attribute.dependency.*;
import io.github.factoryfx.factory.attribute.*;
import io.github.factoryfx.factory.parametrized.ParametrizedObjectCreatorAttribute;
import io.github.factoryfx.factory.storage.migration.metadata.AttributeStorageMetadata;
import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadata;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class FactoryMetadata<R extends FactoryBase<?,R>,F extends FactoryBase<?,R>> {

    private Constructor<F> constructor;
    private final Class<F> clazz;
    private Class<?> liveObjectClass;

    public FactoryMetadata(Class<F> clazz){
        this.clazz=clazz;
        initAttributeFields(clazz);
    }

    public void visitAttributesFlat(F data, AttributeVisitor attributeVisitor){
        if (fastFactoryUtility!=null){
            fastFactoryUtility.visitAttributesFlat(data,  attributeVisitor);
        } else {
            for (AttributeMetadataAndAccessor<F,Attribute<?, ?>> attributeMetadata : attributeMetadataList) {
                attributeVisitor.accept(attributeMetadata.attributeMetadata,attributeMetadata.get(data));
            }
        }
    }

    private List<AttributeMetadataAndAccessor<F,Attribute<?,?>>> attributeMetadataList;


    public void visitAttributeMetadata(AttributeMetadataVisitor consumer) {
        if (fastFactoryUtility!=null){
            fastFactoryUtility.visitAttributesMetadataFlat(consumer);
        } else {
            for (AttributeMetadataAndAccessor<F,?> attributeMetadata : attributeMetadataList) {
                consumer.accept(attributeMetadata.attributeMetadata);
            }
        }
    }


    public void visitFactoryEnclosingAttributesFlat(F factory, FactoryEnclosingAttributeVisitor visitor) {
        for (AttributeMetadataAndAccessor<F,Attribute<?,?>> attribute : factoryChildrenEnclosingAttributeFields) {
            visitor.accept(attribute.attributeMetadata.attributeVariableName,(FactoryChildrenEnclosingAttribute)attribute.get(factory));
        }
    }

    private FastFactoryUtility<R,F> fastFactoryUtility;
    public void setFastFactoryUtility(FastFactoryUtility<R,F> fastFactoryUtility) {
        this.fastFactoryUtility=fastFactoryUtility;
    }

    public Class<?> getLiveObjectClass() {
        return this.liveObjectClass;
    }


    public static class AttributeNamePair{
        public final String name;
        public final Attribute<?,?> attribute;

        public AttributeNamePair(String name, Attribute<?, ?> attribute) {
            this.name = name;
            this.attribute = attribute;
        }
    }

    @SuppressWarnings("unchecked")
    public <V> void visitAttributesForCopy(F factory, F other, FactoryBase.BiCopyAttributeVisitor<V> consumer) {
        if (fastFactoryUtility!=null){
            fastFactoryUtility.visitAttributesForCopy(factory, other, consumer);
        } else {
            for (AttributeMetadataAndAccessor<F,?> field : attributeMetadataList) {
                if (!consumer.accept((Attribute<V,?>)field.get(factory),(Attribute<V,?>)field.get(other))){
                    break;
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <V> void visitAttributesForMatch(F factory, F other, FactoryBase.AttributeMatchVisitor<V> consumer) {
        if (fastFactoryUtility!=null){
            fastFactoryUtility.visitAttributesForMatch(factory, other, consumer);
        } else {
            for (AttributeMetadataAndAccessor<F,?> field : attributeMetadataList) {
                if (!consumer.accept(field.attributeMetadata.attributeVariableName,(Attribute<V,?>) field.get(factory), (Attribute<V,?>) field.get(other))){
                    break;
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <V> void visitAttributesTripleFlat(F data, F other1, F other2, FactoryBase.TriAttributeVisitor<V> consumer) {
        if (fastFactoryUtility!=null){
            this.fastFactoryUtility.visitAttributesTripleFlat( data,  other1,  other2, consumer);
        }  else {
            for (AttributeMetadataAndAccessor<F,?> field : attributeMetadataList) {
                consumer.accept(field.attributeMetadata.attributeVariableName,(Attribute<V,?>) field.get(data), (Attribute<V,?>) field.get(other1), (Attribute<V,?>) field.get(other2));
            }
        }
    }


    private final ArrayList<AttributeMetadataAndAccessor<F,Attribute<?,?>>> factoryChildrenEnclosingAttributeFields = new ArrayList<>();

    @SuppressWarnings("unchecked")
    private void initAttributeFields(Class<?> clazz) {
        attributeMetadataList=new ArrayList<>();
        ArrayList<Field> attributeFields = new ArrayList<>();

        Class<?> parent = clazz.getSuperclass();
        if (parent!=null){// skip Object
            initAttributeFields(parent);
        }
        Stream.of(clazz.getDeclaredFields()).
                filter(f-> Modifier.isPublic(f.getModifiers())).
                filter(f->!Modifier.isStatic(f.getModifiers())).
                filter(f->Attribute.class.isAssignableFrom(f.getType())).
                forEach(attributeFields::add);

        MethodHandles.Lookup lookup = MethodHandles.lookup();

        F factory=null;
        if (!attributeFields.isEmpty()){//special case AttributelessFactory
            factory = newInstance();
        }

        liveObjectClass= initLiveObjectClass();

        //generics parameter for attributes
        for (Field field : attributeFields) {
            if (field.getName().equals("id")){
                throw new IllegalStateException(clazz.getName()+", Factories can't have an id attribute because that conflicts with the factory id property");
            }

            try {
                field.setAccessible(true);//should improve performance
            } catch (InaccessibleObjectException e){
                throw new RuntimeException("\n\nto fix the error add jpms boilerplate, \noption 1: module-info.info: opens "+clazz.getPackage().getName()+";\noption 2: open all, open module {A} { ... } (open keyword before module)\n",e);
            }
//            Class<?> attributeType = field.getType();
//            while(attributeType!=ReferenceBaseAttribute.class && attributeType!=Object.class){
//                attributeType=attributeType.getSuperclass();
//            }


            AttributeFieldAccessor<F,?> attributeFieldAccessor;
            try {
                attributeFieldAccessor = new AttributeFieldAccessor<>(lookup.unreflectGetter(field));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            field.getType().getGenericSuperclass();
            Type type = field.getGenericType();
            Class<?> genericType1=null;
            Class<?> genericType2=null;
            Class<?> genericType3=null;
            Class<?> genericType4=null;
            if (type instanceof ParameterizedType) {
                ParameterizedType ptype = (ParameterizedType) type;
                //assume last generic parameter ist reference class. is kind of guess work but best we can do with reflection

                genericType1 = getFieldGenericType(ptype,0);
                genericType2 = getFieldGenericType(ptype,1);
                genericType3 = getFieldGenericType(ptype,0);
                genericType4 = getFieldGenericType(ptype,1);
            }


            Attribute<?,?> attribute= (Attribute<?, ?>) attributeFieldAccessor.get(factory);
            Class<? extends FactoryBase<?, ?>> referenceClass = null;
            if (genericType2!=null && FactoryBase.class.isAssignableFrom(genericType2)) {
                referenceClass = (Class<? extends FactoryBase<?, ?>>)genericType2;
            }

            Class<?> liveobjectClass=null;
            if (genericType1!=null && attribute instanceof ReferenceBaseAttribute){
                liveobjectClass=genericType1;
            }
            if (attribute instanceof ParametrizedObjectCreatorAttribute){
                liveobjectClass=genericType3;
                referenceClass=(Class<? extends FactoryBase<?, ?>>)genericType4;
            }



            Class<? extends Enum<?>> enumClass=null;
            if (attribute instanceof EnumAttribute){
                enumClass= (Class<? extends Enum<?>>)genericType1;
            }
            if (attribute instanceof EnumListAttribute){
                enumClass= (Class<? extends Enum<?>>)genericType1;
            }
            if (attribute==null){
                throw new IllegalStateException("attribute is null: "+clazz.getName()+"#"+field.getName());
            }
            AttributeMetadata attributeMetadata = new AttributeMetadata(
                    field.getName(),
                    (Class<? extends Attribute<?, ?>>)field.getType(),
                    referenceClass,
                    liveobjectClass,
                    enumClass,
                    attribute.internal_getLabelText(),
                    attribute.internal_required());
            AttributeMetadataAndAccessor<F, Attribute<?, ?>> attributeMetadataAndAccessor = new AttributeMetadataAndAccessor<>(attributeMetadata, (AttributeFieldAccessor<F, Attribute<?, ?>>) attributeFieldAccessor);
            attributeMetadataList.add(attributeMetadataAndAccessor);

            if (FactoryChildrenEnclosingAttribute.class.isAssignableFrom(field.getType())) {
                factoryChildrenEnclosingAttributeFields.add(attributeMetadataAndAccessor);
            }

        }

    }

    private Class<?> initLiveObjectClass() {
        Class<?> parameterizedClass=clazz;
        while(parameterizedClass!=null){
            Type genericSuperclass = parameterizedClass.getGenericSuperclass();
            if (genericSuperclass instanceof ParameterizedType){
                Type actualTypeArgument = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
                if (actualTypeArgument instanceof Class){
                    return (Class<?>) actualTypeArgument;
                }
            }
            parameterizedClass=parameterizedClass.getSuperclass();
        }
        return null;
    }


    private Class<?> getFieldGenericType(ParameterizedType ptype, int index) {
        if (index >= ptype.getActualTypeArguments().length){
            return null;
        }
        Type actualTypeArgument = ptype.getActualTypeArguments()[index];
        if (actualTypeArgument instanceof ParameterizedType) {
            return (Class<?> ) ((ParameterizedType)actualTypeArgument).getRawType();
        }
        if (actualTypeArgument instanceof Class){
            return (Class<?> )actualTypeArgument;
        }
        return null;
    }

    public void addBackReferencesToAttributes(F data, R root) {
        if (fastFactoryUtility!=null){
            this.fastFactoryUtility.addBackReferencesToAttributes( data,  root);
        } else {
            visitFactoryEnclosingAttributesFlat(data, (attributeMetadata, attribute) -> {
                attribute.internal_addBackReferences(root,data);
            });
        }
    }

    @SuppressWarnings("unchecked")
    public void addBackReferencesToAttributesUnsafe(FactoryBase<?,R> data, R root) {
        this.addBackReferencesToAttributes((F)data,root);
    }

    private static Object[] defaultConstructor = new Object[0];
    private Function<F, F> newCopyInstanceSupplier =null;

    /**
     *  new instance configuration default use reflection over default constructor
     *  used for copies
     *
     * @param newCopyInstanceSupplier newCopyInstanceSupplier
     * @return DataDictionary for fluent configuration
     * */
    public FactoryMetadata<R, F> setNewCopyInstanceSupplier(Function<F, F> newCopyInstanceSupplier){
        this.newCopyInstanceSupplier =newCopyInstanceSupplier;
        return this;
    }

    public F newCopyInstance(F data) {
        F result;
        if (newCopyInstanceSupplier !=null && data!=null){
            result= newCopyInstanceSupplier.apply(data);
        } else {
            if (constructor==null){
                try {
                    this.constructor = clazz.getDeclaredConstructor();
                    this.constructor.setAccessible(true);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException("no constructor found, for nested classes ensure that they are static",e);
                } catch (InaccessibleObjectException e){
                    throw new RuntimeException("\n\nto fix the error add jpms boilerplate, \noption 1: module-info.info: opens "+clazz.getPackage().getName()+";\noption 2: open all, open module {A} { ... } (open keyword before module)\n",e);
                }


            }

            try {
                result = constructor.newInstance(defaultConstructor);
            } catch (InstantiationException | InvocationTargetException | IllegalAccessException e ) {
                throw new RuntimeException(clazz.getName(),e);
            }
        }
        return result;
    }

    public F newInstance(){
        return newCopyInstance(null);
    }

    public DataStorageMetadata createDataStorageMetadata(long count) {
        F data = newInstance();
        ArrayList<AttributeStorageMetadata> attributes = new ArrayList<>();
        visitAttributesFlat(data, (attributeMetadata, attribute) -> {
            attributes.add(attribute.createAttributeStorageMetadata(attributeMetadata));
        });
        return new DataStorageMetadata(attributes,clazz.getName(),count);
    }

    public void visitChildFactoriesAndViewsFlat(F factory, Consumer<FactoryBase<?,?>> consumer, boolean includeViews) {
        if (fastFactoryUtility!=null){
            this.fastFactoryUtility.visitChildFactoriesAndViewsFlat(factory, consumer);
        } else {
            visitFactoryEnclosingAttributesFlat(factory, (attributeVariableName, attribute) -> attribute.internal_visitChildren(consumer, includeViews));
        }
    }

    public AttributeMetadata getAttributeMetadata(Function<F,Attribute<?,?>> attributeSupplier) {
        F factory = newInstance();
        Attribute<?, ?> attributeParam = attributeSupplier.apply(factory);
        return getAttributeMetadata(factory,attributeParam);
    }

    public AttributeMetadata getAttributeMetadata(F factory, Attribute<?,?> attributeParam) {
        ArrayList<AttributeMetadata > result=new ArrayList<>(1);
        result.add(null);
        this.visitAttributesFlat(factory, (attributeMetadata, attribute) -> {
            if (attributeParam==attribute) {
                result.set(0,attributeMetadata);
            }
        });
        return result.get(0);
    }
}
