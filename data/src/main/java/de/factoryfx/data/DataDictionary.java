package de.factoryfx.data;

import de.factoryfx.data.attribute.*;
import de.factoryfx.data.storage.migration.metadata.AttributeStorageMetadata;
import de.factoryfx.data.storage.migration.metadata.DataStorageMetadata;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class DataDictionary<D extends Data> {
    private static final Map<Class<?>, DataDictionary<?>> dataReferences = new ConcurrentHashMap<>();//ConcurrentHashMap


    @SuppressWarnings("unchecked")
    public static <T extends Data> DataDictionary<T> getDataDictionary(Class<T> clazz){
        DataDictionary<T> result=(DataDictionary<T>)dataReferences.get(clazz);
        if (result==null){
            result=new DataDictionary<>(clazz);
            dataReferences.put(clazz,result);
        }
        return result;
    }

    private BiConsumer<D,AttributeVisitor> visitAttributesFlat;
    private boolean temporaryAttributes=false;
    private Constructor constructor;
    private final Class<? extends Data> clazz;

    public DataDictionary(Class<? extends Data> clazz){
        this.clazz=clazz;
        initAttributeFields(clazz);
    }

    /**
     * default implementation use reflection, this method can be used to improve performance

     * @param visitAttributesFlat  visitor
     * @return DataDictionary for fluent configuration
     * */
    public DataDictionary<D> setVisitAttributesFlat(BiConsumer<D,AttributeVisitor> visitAttributesFlat){
        this.visitAttributesFlat=visitAttributesFlat;
        return this;
    }

    /**
     * Data use temporary to simulate normal data, this is an optimization hind cause some operation don't make sense with Temporary attributes
     * @return DataDictionary for fluent configuration
     */
    public DataDictionary<D> setUseTemporaryAttributes(){
        temporaryAttributes=true;
        return this;
    }

    void visitAttributesFlat(D data, AttributeVisitor attributeVisitor){
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

    private BiConsumer<D,Consumer<Data>> visitDataChildren;

    /**
     * default implementation use reflection, this method can be used to improve performance
     *
     * @param visitDataChildren visitor
     * @return DataDictionary for fluent configuration
     * */
    public DataDictionary<D> setVisitDataChildren(BiConsumer<D,Consumer<Data>> visitDataChildren){
        this.visitDataChildren=visitDataChildren;
        return this;
    }

    void visitDataChildren(D data, Consumer<Data> consumer) {
        visitDataAndViewChildren(data,consumer,false);
    }

    void visitDataAndViewChildren(D data, Consumer<Data> consumer) {
        visitDataAndViewChildren(data,consumer,true);
    }


    private void visitDataAndViewChildren(D data, Consumer<Data> consumer, boolean visitViews) {
        if (this.visitDataChildren != null) {
            this.visitDataChildren.accept(data, consumer);
        } else {
            visitAttributesFlat(data, (attributeVariableName, attribute) -> {
                if (attribute instanceof ReferenceAttribute) {
                    Data child = ((ReferenceAttribute<?, ?>) attribute).get();
                    if (child != null) {
                        consumer.accept(child);
                    }
                }
                if (attribute instanceof ReferenceListAttribute) {
                    ((ReferenceListAttribute<?, ?>) attribute).forEach(consumer);
                }

                if (visitViews){
                    if (attribute instanceof ViewReferenceAttribute) {
                        Data child = ((ViewReferenceAttribute<?, ?, ?>) attribute).get();
                        if (child != null) {
                            consumer.accept(child);
                        }
                    }
                    if (attribute instanceof ViewListReferenceAttribute) {
                        ((ViewListReferenceAttribute<?, ?, ?>) attribute).get().forEach(consumer);
                    }
                }
            });
        }
    }

    public static class AttributeNamePair{
        public final String name;
        public final Attribute<?,?> attribute;

        public AttributeNamePair(String name, Attribute<?, ?> attribute) {
            this.name = name;
            this.attribute = attribute;
        }
    }

    void visitAttributesDualFlat(D data, D other, Data.BiAttributeVisitor consumer) {
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

    void visitAttributesTripleFlat(D data, D other1, D other2, Data.TriAttributeVisitor consumer) {
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

    private List<AttributeNamePair> getAttributes(D data, int initialCapacity) {
        List<AttributeNamePair> attributes = new ArrayList<>(initialCapacity);
        visitAttributesFlat(data, (attributeVariableName, attribute) -> attributes.add(new AttributeNamePair(attributeVariableName,attribute)));
        return attributes;
    }

    private final ArrayList<Field> attributeFields = new ArrayList<>();

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
    }

    void addBackReferencesToAttributes(D data, Data root) {
        if (!temporaryAttributes) {//no BackReferences for FastFactories
            visitAttributesFlat(data,(name, attribute) -> attribute.internal_addBackReferences(root,data));
        }
    }

    private static Object[] defaultConstructor = new Object[0];
    private Function<D,D> newCopyInstanceSupplier =null;

    /**
     *  new instance configuration default use reflection over default constructor
     *  used for copies
     *
     * @param newCopyInstanceSupplier newCopyInstanceSupplier
     * @return DataDictionary for fluent configuration
     * */
    public DataDictionary<D> setNewCopyInstanceSupplier(Function<D,D> newCopyInstanceSupplier){
        this.newCopyInstanceSupplier =newCopyInstanceSupplier;
        return this;
    }

    @SuppressWarnings("unchecked")
    D newCopyInstance(D data) {
        D result;
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
                result = (D) constructor.newInstance(defaultConstructor);
            } catch (InstantiationException | InvocationTargetException | IllegalAccessException e ) {
                throw new RuntimeException(clazz.getName(),e);
            }
        }
        return result;
    }

    public D newInstance(){
        return newCopyInstance(null);
    }


    public DataStorageMetadata createDataStorageMetadata(long count) {
        D data = newInstance();
        ArrayList<AttributeStorageMetadata> attributes = new ArrayList<>();
        visitAttributesFlat(data, (attributeVariableName, attribute) -> attributes.add(attribute.createAttributeStorageMetadata(attributeVariableName)));
        return new DataStorageMetadata(attributes,clazz.getName(),count);
    }
}
