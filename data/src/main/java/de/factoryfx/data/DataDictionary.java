package de.factoryfx.data;

import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.ReferenceAttribute;
import de.factoryfx.data.attribute.ReferenceListAttribute;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class DataDictionary<D extends Data> {
    private static final Map<Class<?>, DataDictionary<?>> dataReferences = new ConcurrentHashMap<>();

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

    /** default implementation use reflection, this method can be used to improve performance*/
    public void setVisitAttributesFlat(BiConsumer<D,AttributeVisitor> visitAttributesFlat){
        this.visitAttributesFlat=visitAttributesFlat;
    }

    public void visitAttributesFlat(D data, AttributeVisitor attributeVisitor){
        if (visitAttributesFlat!=null){
            this.visitAttributesFlat.accept(data,attributeVisitor);
        } else {
            for (Field field : getAttributeFields()) {
                try {
                    attributeVisitor.accept(field.getName(),(Attribute<?,?>) field.get(data));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private BiConsumer<D,Consumer<Data>> visitDataChildren;

    /** default implementation use reflection, this method can be used to improve performance*/
    public void setVisitDataChildren(BiConsumer<D,Consumer<Data>> visitDataChildren){
        this.visitDataChildren=visitDataChildren;
    }

    public void visitDataChildren(D data, Consumer<Data> consumer) {
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
            });
        }
    }

    public DataDictionary(Class<?> clazz){
        initAttributeFields(clazz);
    }

    private ArrayList<Field> attributeFields = new ArrayList<>();

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

    public List<Field> getAttributeFields() {
        return attributeFields;
    }

    public void addBackReferencesToAttributes(D data, Data root) {
        if (visitAttributesFlat==null) {//no BackReferences for FastFactories
            visitAttributesFlat(data,(name, attribute) -> {
                attribute.internal_prepareUsageFlat(root,data);
            });
        }

    }
}
