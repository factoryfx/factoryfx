package io.github.factoryfx.factory.metadata;

import io.github.factoryfx.factory.AttributeVisitor;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.FactoryEnclosingAttributeVisitor;
import io.github.factoryfx.factory.fastfactory.FastFactoryUtility;
import io.github.factoryfx.factory.attribute.dependency.*;
import io.github.factoryfx.factory.attribute.*;
import io.github.factoryfx.factory.storage.migration.metadata.AttributeStorageMetadata;
import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadata;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class FactoryMetadata<R extends FactoryBase<?,R>,F extends FactoryBase<?,R>> {

    private boolean temporaryAttributes=false;
    private Constructor constructor;
    private final Class<? extends FactoryBase<?,?>> clazz;

    public FactoryMetadata(Class<F> clazz){
        this.clazz=clazz;
        initAttributeFields(clazz);

        for (Field attributeField : attributeFields) {
            if (attributeField.getName().equals("id")){
                throw new IllegalStateException(clazz.getName()+", Factories can't have an id attribute because that conflicts with the factory id property");
            }
        }
    }

    /**
     * Data use temporary to simulate normal data, this is an optimization hind cause some operation don't make sense with Temporary attributes
     * @return DataDictionary for fluent configuration
     */
    public FactoryMetadata<R, F> setUseTemporaryAttributes(){
        temporaryAttributes=true;
        return this;
    }

    public void visitAttributesFlat(F data, AttributeVisitor attributeVisitor){
        if (fastFactoryUtility!=null){
            fastFactoryUtility.visitAttributesFlat(data,  attributeVisitor);
        } else {
            for (Field field : attributeFields) {
                try {
                    attributeVisitor.accept(field.getName(),(Attribute<?,?>) field.get(data));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("\nto fix the error add jpms boilerplate, \noption 1: module-info.info: opens "+data.getClass().getPackage().getName()+";\noption 2: open all, open module {A} { ... } (open keyword before module)\n",e);
                }
            }
        }
    }


    public void visitFactoryEnclosingAttributesFlat(F factory, FactoryEnclosingAttributeVisitor visitor) {
        for (AttributeFieldAccessor<R,F,FactoryChildrenEnclosingAttribute> attributeFieldAccessor : factoryChildrenEnclosingAttributeFields) {
            visitor.accept(attributeFieldAccessor.getName(),attributeFieldAccessor.get(factory));
        }
    }

    private FastFactoryUtility<R,F> fastFactoryUtility;
    public void setFastFactoryUtility(FastFactoryUtility<R,F> fastFactoryUtility) {
        this.fastFactoryUtility=fastFactoryUtility;
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
            for (Field field : attributeFields) {
                try {
                    if (!consumer.accept((Attribute<V,?>) field.get(factory), (Attribute<V,?>) field.get(other))){
                        break;
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <V> void visitAttributesForMatch(F factory, F other, FactoryBase.AttributeMatchVisitor<V> consumer) {
        if (fastFactoryUtility!=null){
            fastFactoryUtility.visitAttributesForMatch(factory, other, consumer);
        } else {
            for (Field field : attributeFields) {
                try {
                    if (!consumer.accept(field.getName(),(Attribute<V,?>) field.get(factory), (Attribute<V,?>) field.get(other))){
                        break;
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <V> void visitAttributesTripleFlat(F data, F other1, F other2, FactoryBase.TriAttributeVisitor<V> consumer) {
        if (fastFactoryUtility!=null){
            this.fastFactoryUtility.visitAttributesTripleFlat( data,  other1,  other2, consumer);
        }  else {
            for (Field field : attributeFields) {
                try {
                    consumer.accept(field.getName(),(Attribute<V,?>) field.get(data), (Attribute<V,?>) field.get(other1), (Attribute<V,?>) field.get(other2));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private final ArrayList<Field> attributeFields = new ArrayList<>();
    private final HashMap<String,Class<?>> fieldToReferenceClass = new HashMap<>();
    private final ArrayList<AttributeFieldAccessor<R,F,FactoryChildrenEnclosingAttribute>> factoryChildrenEnclosingAttributeFields = new ArrayList<>();

    private void initAttributeFields(Class<?> clazz) {
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

        //generics parameter for attributes
        for (Field field : attributeFields) {
            try {
                field.setAccessible(true);//should improve performance
            } catch (InaccessibleObjectException e){
                throw new RuntimeException("\n\nto fix the error add jpms boilerplate, \noption 1: module-info.info: opens "+clazz.getPackage().getName()+";\noption 2: open all, open module {A} { ... } (open keyword before module)\n",e);
            }
//            Class<?> attributeType = field.getType();
//            while(attributeType!=ReferenceBaseAttribute.class && attributeType!=Object.class){
//                attributeType=attributeType.getSuperclass();
//            }

            if (FactoryChildrenEnclosingAttribute.class.isAssignableFrom(field.getType())) {
                try {
                    factoryChildrenEnclosingAttributeFields.add(new AttributeFieldAccessor<>(lookup.unreflectGetter(field), field.getName()));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }

            if (ReferenceBaseAttribute.class.isAssignableFrom(field.getType())){
                field.getType().getGenericSuperclass();
                Type type = field.getGenericType();
                if (type instanceof ParameterizedType) {
                    ParameterizedType ptype = (ParameterizedType) type;
                    //assume last generic parameter ist reference class. is kind of guess work but best we can do with reflection
                    try {
                        Type actualTypeArgument = ptype.getActualTypeArguments()[ptype.getActualTypeArguments().length - 1];
                        String className= actualTypeArgument.getTypeName();
                        if (actualTypeArgument instanceof ParameterizedType) {
                            className=((ParameterizedType)actualTypeArgument).getRawType().getTypeName();
                        }

                        fieldToReferenceClass.put(field.getName(), Class.forName(className));
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    public void addBackReferencesAndReferenceClassToAttributes(F data, R root) {
        if (!temporaryAttributes) {//no BackReferences for FastFactories they are set manually
            visitFactoryEnclosingAttributesFlat(data, (attributeVariableName, attribute) -> {
                attribute.internal_addBackReferences(root,data);
                attribute.internal_setReferenceClass(fieldToReferenceClass.get(attributeVariableName));
            });
        }
    }

    @SuppressWarnings("unchecked")
    public void addBackReferencesAndReferenceClassToAttributesUnsafe(FactoryBase<?,R> data, R root) {
        this.addBackReferencesAndReferenceClassToAttributes((F)data,root);
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

    @SuppressWarnings("unchecked")
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
                result = (F) constructor.newInstance(defaultConstructor);
            } catch (InstantiationException | InvocationTargetException | IllegalAccessException e ) {
                throw new RuntimeException(clazz.getName(),e);
            }
        }
        return result;
    }

    public F newInstance(){
        return newCopyInstance(null);
    }

    public void setAttributeReferenceClasses(F data){
        if (!this.temporaryAttributes){
            this.visitAttributesFlat(data, (attributeVariableName, attribute) -> {
                if (attribute instanceof ReferenceBaseAttribute) {
                    ((ReferenceBaseAttribute<?,?,?>)attribute).internal_setReferenceClass(fieldToReferenceClass.get(attributeVariableName));
                }
            });
        }
    }

    public DataStorageMetadata createDataStorageMetadata(long count) {
        F data = newInstance();
        setAttributeReferenceClasses(data);
        ArrayList<AttributeStorageMetadata> attributes = new ArrayList<>();
        visitAttributesFlat(data, (attributeVariableName, attribute) -> attributes.add(attribute.createAttributeStorageMetadata(attributeVariableName)));
        return new DataStorageMetadata(attributes,clazz.getName(),count);
    }

    public void visitChildFactoriesAndViewsFlat(F factory, Consumer<FactoryBase<?,?>> consumer, boolean includeViews) {
        if (fastFactoryUtility!=null){
            this.fastFactoryUtility.visitChildFactoriesAndViewsFlat(factory, consumer);
        } else {
            visitFactoryEnclosingAttributesFlat(factory, (attributeVariableName, attribute) -> attribute.internal_visitChildren(consumer, includeViews));
        }
    }
}
