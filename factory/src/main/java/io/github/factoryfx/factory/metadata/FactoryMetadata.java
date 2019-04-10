package io.github.factoryfx.factory.metadata;

import io.github.factoryfx.factory.AttributeVisitor;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.dependency.*;
import io.github.factoryfx.factory.attribute.*;
import io.github.factoryfx.factory.storage.migration.metadata.AttributeStorageMetadata;
import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadata;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class FactoryMetadata<R extends FactoryBase<?,R>, L,F extends FactoryBase<L,R>> {

    private BiConsumer<F, AttributeVisitor> visitAttributesFlat;
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
     * default implementation use reflection, this method can be used to improve performance

     * @param visitAttributesFlat  visitor
     * @return DataDictionary for fluent configuration
     * */
    public FactoryMetadata<R,L, F> setVisitAttributesFlat(BiConsumer<F,AttributeVisitor> visitAttributesFlat){
        this.visitAttributesFlat=visitAttributesFlat;
        return this;
    }

    /**
     * Data use temporary to simulate normal data, this is an optimization hind cause some operation don't make sense with Temporary attributes
     * @return DataDictionary for fluent configuration
     */
    public FactoryMetadata<R,L, F> setUseTemporaryAttributes(){
        temporaryAttributes=true;
        return this;
    }

    public void visitAttributesFlat(F data, AttributeVisitor attributeVisitor){
        if (visitAttributesFlat!=null){
            this.visitAttributesFlat.accept(data,attributeVisitor);
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

    private BiConsumer<F,Consumer<FactoryBase<?,?>>> visitDataChildren;

    /**
     * default implementation use reflection, this method can be used to improve performance
     *
     * @param visitDataChildren visitor
     * @return DataDictionary for fluent configuration
     * */
    public FactoryMetadata<R, L,F> setVisitDataChildren(BiConsumer<F,Consumer<FactoryBase<?,?>>> visitDataChildren){
        this.visitDataChildren=visitDataChildren;
        return this;
    }

//    void visitDataChildren(D data, Consumer<FactoryBase<?,?>> consumer) {
//        visitDataAndViewChildren(data,consumer,false);
//    }
//
//    void visitDataAndViewChildren(D data, Consumer<FactoryBase<?,?>> consumer) {
//        visitDataAndViewChildren(data,consumer,true);
//    }
//
//
//    private void visitDataAndViewChildren(D data, Consumer<FactoryBase<?,?>> consumer, boolean visitViews) {
//        if (this.visitDataChildren != null) {
//            this.visitDataChildren.accept(data, consumer);
//        } else {
//            visitAttributesFlat(data, (attributeVariableName, attribute) -> {
//                if (attribute instanceof ReferenceAttribute) {
//                    FactoryBase<?,?> child = ((ReferenceAttribute<?, ?>) attribute).get();
//                    if (child != null) {
//                        consumer.accept(child);
//                    }
//                }
//                if (attribute instanceof ReferenceListAttribute) {
//                    ((ReferenceListAttribute<?, ?>) attribute).forEach(consumer);
//                }
//
//                if (visitViews){
//                    if (attribute instanceof FactoryViewReferenceAttribute) {
//                        FactoryBase<?,?> child = ((FactoryViewReferenceAttribute<?, ?, ?>) attribute).get();
//                        if (child != null) {
//                            consumer.accept(child);
//                        }
//                    }
//                    if (attribute instanceof FactoryViewListReferenceAttribute) {
//                        ((FactoryViewListReferenceAttribute<?, ?, ?>) attribute).get().forEach(consumer);
//                    }
//                }
//            });
//        }
//    }

    public static class AttributeNamePair{
        public final String name;
        public final Attribute<?,?> attribute;

        public AttributeNamePair(String name, Attribute<?, ?> attribute) {
            this.name = name;
            this.attribute = attribute;
        }
    }

    public void visitAttributesDualFlat(F data, F other, FactoryBase.BiAttributeVisitor consumer) {
        if (this.visitDataChildren != null) {
            List<AttributeNamePair> attributes = getAttributes(data,10);
            List<AttributeNamePair> otherAttributes = getAttributes(other,attributes.size());
            for (int i = 0; i < attributes.size(); i++) {
                consumer.accept(attributes.get(i).name,attributes.get(i).attribute,otherAttributes.get(i).attribute);
            }
        } else {
            for (Field field : attributeFields) {
                try {
                    consumer.accept(field.getName(),(Attribute<?,?>) field.get(data), (Attribute<?,?>) field.get(other));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void visitAttributesTripleFlat(F data, F other1, F other2, FactoryBase.TriAttributeVisitor consumer) {
        if (this.visitDataChildren != null) {
            List<AttributeNamePair> attributes = getAttributes(data,10);
            List<AttributeNamePair> otherAttributes = getAttributes(other1,attributes.size());
            List<AttributeNamePair> other2Attributes = getAttributes(other2,attributes.size());
            for (int i = 0; i < attributes.size(); i++) {
                consumer.accept(attributes.get(i).name,attributes.get(i).attribute,otherAttributes.get(i).attribute,other2Attributes.get(i).attribute);
            }
        } else {
            for (Field field : attributeFields) {
                try {
                    consumer.accept(field.getName(),(Attribute<?,?>) field.get(data), (Attribute<?,?>) field.get(other1), (Attribute<?,?>) field.get(other2));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private List<AttributeNamePair> getAttributes(F data, int initialCapacity) {
        List<AttributeNamePair> attributes = new ArrayList<>(initialCapacity);
        visitAttributesFlat(data, (attributeVariableName, attribute) -> attributes.add(new AttributeNamePair(attributeVariableName,attribute)));
        return attributes;
    }

    private final ArrayList<Field> attributeFields = new ArrayList<>();
    private final HashMap<String,Class<?>> fieldToReferenceClass = new HashMap<>();

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


        //generics parameter for attributes
        for (Field field : attributeFields) {
//            Class<?> attributeType = field.getType();
//            while(attributeType!=ReferenceBaseAttribute.class && attributeType!=Object.class){
//                attributeType=attributeType.getSuperclass();
//            }

            if (ReferenceBaseAttribute.class.isAssignableFrom(field.getType())){
                field.getType().getGenericSuperclass();
                Type type = field.getGenericType();
                if (type instanceof ParameterizedType) {
                    ParameterizedType ptype = (ParameterizedType) type;
                    //last generic parameter is kind of guess work but best we can do with reflection
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

    @SuppressWarnings("unchecked")
    public void addBackReferencesToAttributes(F data, R root) {
        if (!temporaryAttributes) {//no BackReferences for FastFactories
            visitAttributesFlat(data,(name, attribute) -> {
                if (attribute instanceof RootAwareAttribute<?,?>){
                    ((RootAwareAttribute<R,?>)attribute).internal_addBackReferences(root,data);
                }
            });
        }
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
    public FactoryMetadata<R,L, F> setNewCopyInstanceSupplier(Function<F, F> newCopyInstanceSupplier){
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
                    throw new RuntimeException(e);
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
        this.visitAttributesFlat(data, (attributeVariableName, attribute) -> {
            if (attribute instanceof ReferenceBaseAttribute) {
                ((ReferenceBaseAttribute<?,?,?,?>)attribute).internal_setReferenceClass(fieldToReferenceClass.get(attributeVariableName));
            }
        });
    }


    public DataStorageMetadata createDataStorageMetadata(long count) {
        F data = newInstance();
        setAttributeReferenceClasses(data);
        ArrayList<AttributeStorageMetadata> attributes = new ArrayList<>();
        visitAttributesFlat(data, (attributeVariableName, attribute) -> attributes.add(attribute.createAttributeStorageMetadata(attributeVariableName)));
        return new DataStorageMetadata(attributes,clazz.getName(),count);
    }

    private BiConsumer<F,Consumer<FactoryBase<?,R>>> visitChildFactoriesAndViewsFlat;

    public  void setVisitChildFactoriesAndViewsFlat(BiConsumer<F,Consumer<FactoryBase<?,R>>> visitChildFactoriesAndViewsFlat){
        this.visitChildFactoriesAndViewsFlat=visitChildFactoriesAndViewsFlat;
    }

    @SuppressWarnings("unchecked")
    public void visitChildFactoriesAndViewsFlat(F data, Consumer<FactoryBase<?,R>> consumer, boolean includeViews) {
        if (this.visitChildFactoriesAndViewsFlat != null) {
            this.visitChildFactoriesAndViewsFlat.accept(data, consumer);

        } else

            data.internal().visitAttributesFlat((attributeVariableName, attribute) -> {
                if (attribute instanceof FactoryBaseAttribute) {
                    FactoryBase<?,R> factory = (FactoryBase<?,R>) attribute.get();
                    if (factory != null) {
                        consumer.accept(factory);
                    }
                    return;
                }
                if (attribute instanceof FactoryListBaseAttribute) {
                    List<?> factories = ((FactoryListBaseAttribute<?, ?, ?, ?>) attribute).get();
                    for (Object factory : factories) {
                        consumer.accept((FactoryBase<?,R>) factory);
                    }
                    return;
                }
                if (includeViews){
                    if (attribute instanceof FactoryViewAttribute) {
                        FactoryBase<?,R> factory = (FactoryBase<?,R>) attribute.get();
                        if (factory != null) {
                            consumer.accept(factory);
                        }
                        return;
                    }
                    if (attribute instanceof FactoryViewListAttribute) {
                        List<?> factories = ((FactoryViewListAttribute<?, ?, ?>) attribute).get();
                        for (Object factory : factories) {
                            consumer.accept((FactoryBase<?,R>) factory);
                        }
                        return;
                    }
                }
            });

    }
}
