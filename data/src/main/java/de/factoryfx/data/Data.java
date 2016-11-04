package de.factoryfx.data;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Strings;
import de.factoryfx.data.attribute.*;
import de.factoryfx.data.attribute.types.ObjectValueAttribute;
import de.factoryfx.data.merge.MergeResult;
import de.factoryfx.data.merge.MergeResultEntry;
import de.factoryfx.data.merge.attribute.AttributeMergeHelper;
import de.factoryfx.data.util.TextSearchSupport;
import de.factoryfx.data.validation.ValidationError;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;

public abstract class Data implements TextSearchSupport {

    public abstract Object getId();

    public abstract void setId(Object object);

    @JsonIgnore
    private static final Map<Class<?>, Field[]> fields = new HashMap<>();
    @JsonIgnore
    private Field[] instanceFields;

    public Data() {
        initFieldsCache();
    }

    @JsonIgnore
    private Field[] getFields() {
        return instanceFields;
    }

    private void initFieldsCache() {
        synchronized (fields) {
            Field[] f = fields.get(getClass());
            if (f == null) {
                f = getFieldsOrdered(getClass());
                ArrayList<Field> removeStatic = new ArrayList<>();
                for (Field ff : f) {
                    if (!Modifier.isStatic(ff.getModifiers())) {
                        removeStatic.add(ff);
                    }
                }
                fields.put(getClass(), removeStatic.toArray(new Field[removeStatic.size()]));
            }
            instanceFields = f;
        }
    }

    private Field[] getFieldsOrdered(Class<?> clazz) {
        if (clazz == Object.class)
            return new Field[0];
        ArrayList<Field> fields = new ArrayList<>();
        Class<?> parent = clazz.getSuperclass();
        Stream.of(getFieldsOrdered(parent)).forEach(fields::add);
        Stream.of(clazz.getDeclaredFields()).filter(f->Modifier.isPublic(f.getModifiers())).forEach(fields::add);
        return fields.toArray(new Field[fields.size()]);
    }

    @FunctionalInterface
    interface TriConsumer<A, B, C> {
        void accept(A a, B b, C c);
    }

    public void visitChildFactoriesFlat(Consumer<Data> consumer) {
        visitAttributesFlat((attributeVariableName, attribute) -> {
            attribute.visit(new Attribute.AttributeVisitor() {
                @Override
                public void value(Attribute<?> value) {

                }

                @Override
                public void reference(ReferenceAttribute<?> reference) {
                    reference.getOptional().ifPresent((factoryBase)->consumer.accept(factoryBase));
                }

                @Override
                public void referenceList(ReferenceListAttribute<?> referenceList) {
                    referenceList.forEach(new Consumer<Data>() {
                        @Override
                        public void accept(Data factoryBase) {
                            consumer.accept(factoryBase);
                        }
                    });
                }
            });
        });
    }

    @SuppressWarnings("unchecked")
    public <A> void  visitAttributesDualFlat(Data modelBase, BiConsumer<Attribute<A>, Attribute<A>> consumer) {
        Field[] fields = getFields();
        for (Field field : fields) {
            try {
                Object fieldValue = field.get(this);
                if (fieldValue instanceof Attribute) {
                    consumer.accept((Attribute<A>) field.get(this), (Attribute<A>) field.get(modelBase));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FunctionalInterface
    public interface AttributeVisitor{
        void accept(String attributeVariableName, Attribute<?> attribute);
    }

    public void visitAttributesFlat(AttributeVisitor consumer) {
        Field[] fields = getFields();
        for (Field field : fields) {
            try {
                Object fieldValue = field.get(this);
                if (fieldValue instanceof Attribute) {
                    consumer.accept(field.getName(),(Attribute) fieldValue);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void visitAttributesFlat(Consumer<Attribute<?>> consumer) {
        Field[] fields = getFields();
        for (Field field : fields) {
            try {
                Object fieldValue = field.get(this);
                if (fieldValue instanceof Attribute) {
                    consumer.accept((Attribute) fieldValue);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void visitAttributesTripleFlat(Optional<?> modelBase1, Optional<?> modelBase2, TriConsumer<Attribute<?>, Optional<Attribute<?>>, Optional<Attribute<?>>> consumer) {
        Field[] fields = getFields();
        for (Field field : fields) {
            try {
//                fields[f].setAccessible(true);
                Object fieldValue = field.get(this);
                if (fieldValue instanceof Attribute) {

                    Attribute<?> value1 = null;
                    if (modelBase1.isPresent()) {
                        value1 = (Attribute<?>) field.get(modelBase1.get());
                    }
                    Attribute<?> value2 = null;
                    if (modelBase2.isPresent()) {
                        value2 = (Attribute<?>) field.get(modelBase2.get());
                    }
                    consumer.accept((Attribute<?>) field.get(this), Optional.ofNullable(value1), Optional.ofNullable(value2));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public List<Attribute<?>> attributeList(){
        ArrayList<Attribute<?>> result = new ArrayList<>();
        this.visitAttributesFlat((attributeVariableName, attribute) -> {
            result.add(attribute);
        });
        return result;
    }

    //template method to support different tabs in datateditor
    public List<Pair<String,List<Attribute<?>>>> attributeListGrouped(){
        ArrayList<Attribute<?>> result = new ArrayList<>();
        this.visitAttributesFlat((attributeVariableName, attribute) -> {
            result.add(attribute);
        });
        return Arrays.asList(new Pair<>("Data",result));
    }

    public Map<Object,Data> collectChildFactoriesMap() {
        HashSet<Data> factoryBases = new HashSet<>();
//        factoryBases.add(this); TODO required?
        collectModelEntitiesTo(factoryBases);

        HashMap<Object, Data> result = new HashMap<>();
        for (Data factory: factoryBases){
            result.put(factory.getId(),factory);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public <T extends Data> Set<T> collectChildrenFlat() {
        HashSet<T> result = new HashSet<>();
        this.visitChildFactoriesFlat(factoryBase -> result.add((T)factoryBase));
        return result;
    }

    public Set<Data> collectChildrenDeep() {
        HashSet<Data> factoryBases = new HashSet<>();
        collectModelEntitiesTo(factoryBases);
        return factoryBases;
    }


    public void collectModelEntitiesTo(Set<Data> allModelEntities) {
        if (allModelEntities.add(this)){
            visitAttributesFlat(attribute -> attribute.collectChildren(allModelEntities));
        }
    }
    

    /**
     * after deserialization from json only the value is present and metadata are missing
     * we create a copy which contains the metadata and than copy the meta data in teh original object
     */
    @SuppressWarnings("unchecked")
    public <T extends Data> T reconstructMetadataDeepRoot() {
        Data copy = this.newInstance();


        Field[] fields = getFields();
        for (Field field : fields) {
            try {
                if (de.factoryfx.data.attribute.Attribute.class.isAssignableFrom(field.getType())){
                    Object value=null;
                    if (field.get(this)!=null){
                        value= ((Attribute)field.get(this)).get();
                    }
                    field.setAccessible(true);
                    field.set(this,field.get(copy));

                    ((Attribute)field.get(this)).set(value);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        this.visitChildFactoriesFlat(factoryBase -> factoryBase.reconstructMetadataDeepRoot());

        return (T)this;
    }


    protected Data newInstance() {
        try {
            return getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public void fixDuplicateObjects(Function<Object, Optional<Data>> getCurrentEntity) {
        visitAttributesFlat(attribute -> attribute.fixDuplicateObjects(getCurrentEntity));
    }

    Supplier<String> displayTextProvider= () -> Data.this.getClass().getSimpleName()+":"+getId();

    @JsonIgnore
    @SuppressWarnings("unchecked")
    public String getDisplayText(){
        return displayTextProvider.get();
    }
    public void setDisplayTextProvider(Supplier<String> displayTextProvider){
        this.displayTextProvider=displayTextProvider;
    }

    /** validate attributes without visiting child factories*/
    public List<ValidationError> validateFlat(){
        ArrayList<ValidationError> result = new ArrayList<>();
        visitAttributesFlat((attributeVariableName, attribute) -> {
            result.addAll(attribute.validate());
        });
        return result;
    }

    @SuppressWarnings("unchecked")
    public void merge(Optional<Data> originalValue, Optional<Data> newValue, MergeResult mergeResult) {

        this.visitAttributesTripleFlat(originalValue, newValue, (currentAttribute, originalAttribute, newAttribute) -> {
            AttributeMergeHelper<?> attributeMergeHelper = currentAttribute.createMergeHelper();
            if (attributeMergeHelper.executeMerge()){
                boolean hasNoConflict = attributeMergeHelper.hasNoConflict(originalAttribute, newAttribute);
                MergeResultEntry mergeResultEntry = new MergeResultEntry(Data.this, currentAttribute, newAttribute);
                if (hasNoConflict) {
                    if (newAttribute.isPresent()) {
                        if (attributeMergeHelper.isMergeable(originalAttribute, newAttribute)){
                            mergeResult.addMergeExecutions(() -> attributeMergeHelper.merge(originalAttribute, newAttribute.get()));
                            mergeResult.addMergeInfo(mergeResultEntry);
                        }
                    }
                } else {
                    mergeResult.addConflictInfos(mergeResultEntry);
                }
            }

        });
    }


    public HashMap<Data, Data> getChildToParentMap(Set<Data> allModelEntities) {
        HashMap<Data, Data> result = new HashMap<>();
        for (Data factoryBase : allModelEntities) {
            factoryBase.visitAttributesFlat(attribute -> {
                attribute.visit(nestedFactoryBase -> {
                    result.put(nestedFactoryBase, factoryBase);
                });
            });
        }
        return result;
    }


    public List<Data> getMassPathTo(HashMap<Data, Data> childToParent, Data target) {
        List<Data> path = new ArrayList<>();
        Optional<Data> pathElement = Optional.ofNullable(childToParent.get(target));
        while (pathElement.isPresent()) {
            path.add(pathElement.get());
            pathElement = Optional.ofNullable(childToParent.get(pathElement.get()));
        }
        Collections.reverse(path);
        return path;
    }

    /**Slow. for multiple calls use getMassPathTo*/
    public List<Data> getPathTo(Data target) {
        return getMassPathTo(getChildToParentMap(collectChildrenDeep()), target);
    }

    @SuppressWarnings("unchecked")
    public <T extends Data> T copy() {

        T copy = copyDeep(0,Integer.MAX_VALUE,new HashMap<>());

        Map<Object, Data> stringFactoryBaseMap = this.collectChildFactoriesMap();
        for (Data factory: copy.collectChildrenDeep()){
            factory.visitAttributesDualFlat(stringFactoryBaseMap.get(factory.getId()),(copyAttribute, previousAttribute) -> {
                if (copyAttribute instanceof ObjectValueAttribute){
                    ((Attribute)copyAttribute).set(((Attribute)previousAttribute).get());
                }
            });
        }
        return copy;
    }

    /**copy including one the references first level of nested references*/
    public <T extends Data> T copyOneLevelDeep(){
        return copyDeep(0,1,new HashMap<>());
    }

    @SuppressWarnings("unchecked")
    private <T extends Data> T copyDeep(final int level,final int maxLevel, HashMap<Object,Data> identityPreserver){
        if (level>maxLevel){
            return null;
        }
        T result= (T) identityPreserver.get(this.getId());
        if (result==null){
            result = (T)newInstance();
            result.setId(this.getId());
            this.visitAttributesDualFlat(result, (thisAttribute, copyAttribute) -> {
                if (!(thisAttribute instanceof ViewReferenceAttribute || thisAttribute instanceof ViewListReferenceAttribute)) {
                    Object value = thisAttribute.get();
                    if (value instanceof Data) {
                        value = ((Data) value).copyDeep(level + 1, maxLevel, identityPreserver);
                    }
                    if (thisAttribute instanceof ReferenceListAttribute) {
                        final ObservableList<Data> referenceList = FXCollections.observableArrayList();
                        ((ReferenceListAttribute) thisAttribute).get().forEach(factory -> referenceList.add(((Data) factory).copyDeep(level + 1, maxLevel, identityPreserver)));
                        value = referenceList;
                    }

                    copyAttribute.copy(value);
                }
            });
            identityPreserver.put(result.getId(),result);
        }
        return result;
    }

    //use this method only for root
    @SuppressWarnings("unchecked")
    public <T extends Data> T prepareRootEditing() {
        return (T)prepareEditing(this);
    }

    public boolean readyForEditing(){
        return isReadyForEditing;
    }

    public <T extends Data> T endRootEditing() {
        for (Data data: collectChildrenDeep()){
            data.visitAttributesFlat((attributeVariableName, attribute) -> {
                attribute.endEditing();
            });
        }
        return (T)this;
    }

    private boolean isReadyForEditing;
    public <T extends Data> T prepareEditing(Data root){
        for (Data data: collectChildrenDeep()){
            data.visitAttributesFlat((attributeVariableName, attribute) -> {
                attribute.prepareEditing(root,data);
            });
            data.isReadyForEditing=true;
        }
        return (T)this;
    }

    @Override
    public boolean matchSearchText(String text) {
        return Strings.isNullOrEmpty(text) || Strings.nullToEmpty(getDisplayText()).toLowerCase().contains(text.toLowerCase());
    }
}
