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
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.AttributeChangeListener;
import de.factoryfx.data.attribute.ReferenceAttribute;
import de.factoryfx.data.attribute.ReferenceListAttribute;
import de.factoryfx.data.merge.MergeResult;
import de.factoryfx.data.merge.MergeResultEntry;
import de.factoryfx.data.merge.attribute.AttributeMergeHelper;
import de.factoryfx.data.validation.AttributeValidation;
import de.factoryfx.data.validation.Validation;
import de.factoryfx.data.validation.ValidationError;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.util.Pair;

public abstract class Data {

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

    private void visitChildFactoriesFlat(Consumer<Data> consumer) {
        visitAttributesFlat((attributeVariableName, attribute) -> {
            attribute.internal_visit(new de.factoryfx.data.attribute.AttributeVisitor() {
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
    private <A> void  visitAttributesDualFlat(Data modelBase, BiConsumer<Attribute<A>, Attribute<A>> consumer) {
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

    private void visitAttributesFlat(AttributeVisitor consumer) {
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

    private void visitAttributesFlat(Consumer<Attribute<?>> consumer) {
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

    private void visitAttributesTripleFlat(Optional<?> modelBase1, Optional<?> modelBase2, TriConsumer<Attribute<?>, Optional<Attribute<?>>, Optional<Attribute<?>>> consumer) {
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

    private Map<Object,Data> collectChildFactoriesMap() {
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
    private <T extends Data> Set<T> collectChildrenFlat() {
        HashSet<T> result = new HashSet<>();
        this.visitChildFactoriesFlat(factoryBase -> result.add((T)factoryBase));
        return result;
    }

    private Set<Data> collectChildrenDeep() {
        HashSet<Data> factoryBases = new HashSet<>();
        collectModelEntitiesTo(factoryBases);
        return factoryBases;
    }


    private void collectModelEntitiesTo(Set<Data> allModelEntities) {
        if (allModelEntities.add(this)){
            visitAttributesFlat(attribute -> attribute.internal_collectChildren(allModelEntities));
        }
    }
    

    /**
     * after deserialization from json only the value is present and metadata are missing
     * we create a copy which contains the metadata and than copy then transfer the value in the new  copy (which is what conveniently copy does)
     */
    @SuppressWarnings("unchecked")
    private <T extends Data> T reconstructMetadataDeepRoot() {
//        for (Data data: this.collectChildrenDeep()){
//            Data copy = data.newInstance();
//
//            data.dataValidations=copy.dataValidations;
//            data.displayTextDependencies=copy.displayTextDependencies;
//
//            Field[] fields = data.getFields();
//            for (Field field : fields) {
//                try {
//                    if (de.factoryfx.data.attribute.Attribute.class.isAssignableFrom(field.getType())){
//                        Object value=null;
////                        field.setAccessible(true);
//                        if (field.get(data)!=null){
//                            final Attribute attribute = (Attribute) field.get(data);
//                            if (!(attribute instanceof ViewReferenceAttribute) && !(attribute instanceof ViewListReferenceAttribute)){
//                                value= attribute.get();
//                            }
//                        }
//                        field.setAccessible(true);
//                        field.set(data,field.get(copy));
//
//                        ((Attribute)field.get(data)).set(value);
//                    }
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }
        return this.copy();
    }

    Supplier<Data> newInstanceSupplier= () -> {
        try {
            return Data.this.getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    };
    private void setNewInstanceSupplier(Supplier<Data> newInstanceSupplier){
        this.newInstanceSupplier=newInstanceSupplier;
    }
    private Data newInstance() {
        return newInstanceSupplier.get();
    }

    @SuppressWarnings("unchecked")
    private void fixDuplicateObjects(Function<Object, Optional<Data>> getCurrentEntity) {
        visitAttributesFlat(attribute -> attribute.internal_fixDuplicateObjects(getCurrentEntity));
    }

    Supplier<String> displayTextProvider= () -> Data.this.getClass().getSimpleName()+":"+getId();

    @JsonIgnore
    @SuppressWarnings("unchecked")
    private String getDisplayText(){
        return displayTextProvider.get();
    }

    private void setDisplayTextProvider(Supplier<String> displayTextProvider){
        this.displayTextProvider=displayTextProvider;
    }

    /** internal_validate attributes without visiting child factories*/
    @SuppressWarnings("unchecked")
    private List<ValidationError> validateFlat(){
        ArrayList<ValidationError> result = new ArrayList<>();
        visitAttributesFlat((attributeVariableName, attribute) -> {
            result.addAll(attribute.internal_validate());
        });

        for (AttributeValidation<?> validation: dataValidations){
            validation.validate(this).ifPresent(validationError -> result.add(validationError));
        }
        return result;
    }

    List<AttributeValidation<?>> dataValidations = new ArrayList<>();
    private <T> void addValidation(Validation<T> validation, Attribute<?>... dependencies){
        for ( Attribute<?> dependency: dependencies){
            dataValidations.add(new AttributeValidation<>(validation,dependency));
        }
    }

    @SuppressWarnings("unchecked")
    private void merge(Optional<Data> originalValue, Optional<Data> newValue, MergeResult mergeResult) {

        this.visitAttributesTripleFlat(originalValue, newValue, (currentAttribute, originalAttribute, newAttribute) -> {
            AttributeMergeHelper<?> attributeMergeHelper = currentAttribute.internal_createMergeHelper();
            if (attributeMergeHelper.executeMerge()) {
                boolean hasNoConflict = attributeMergeHelper.hasNoConflict(originalAttribute, newAttribute);
                MergeResultEntry mergeResultEntry = new MergeResultEntry(Data.this, currentAttribute, newAttribute);
                if (hasNoConflict) {
                    if (newAttribute.isPresent()) {
                        if (attributeMergeHelper.isMergeable(originalAttribute, newAttribute)) {
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


    private HashMap<Data, Data> getChildToParentMap(Set<Data> allModelEntities) {
        HashMap<Data, Data> result = new HashMap<>();
        for (Data factoryBase : allModelEntities) {
            factoryBase.visitAttributesFlat(attribute -> {
                attribute.internal_visit(nestedFactoryBase -> {
                    result.put(nestedFactoryBase, factoryBase);
                });
            });
        }
        return result;
    }



    private List<Data> getMassPathTo(HashMap<Data, Data> childToParent, Data target) {
        List<Data> path = new ArrayList<>();
        Optional<Data> pathElement = Optional.ofNullable(childToParent.get(target));
        while (pathElement.isPresent()) {
            path.add(pathElement.get());
            pathElement = Optional.ofNullable(childToParent.get(pathElement.get()));
        }
        Collections.reverse(path);
        return path;
    }

    private <T extends Data> T copy() {
        return copyDeep(0,Integer.MAX_VALUE,new HashMap<>());
    }

    @SuppressWarnings("unchecked")
    private <T extends Data> T semanticCopy() {
        T result = (T)newInstance();
//        result.setId(this.getId());
        this.visitAttributesDualFlat(result, (thisAttribute, copyAttribute) -> {
            thisAttribute.internal_semanticCopyTo(copyAttribute,(data)->{
                if (data==null){
                    return null;
                }
                return data.semanticCopy();
            });
        });
        return result;
    }



    /**copy including one the references first level of nested references*/
    private <T extends Data> T copyOneLevelDeep(){
        return copyDeep(0,1,new HashMap<>());
    }

    /**copy without nested references, only value attributes are copied*/
    private <T extends Data> T copyZeroLevelDeep(){
        return copyDeep(0,0,new HashMap<>());
    }


    @SuppressWarnings("unchecked")
    private <T extends Data> T copyDeep(final int level, final int maxLevel, HashMap<Object,Data> identityPreserver){
        if (level>maxLevel){
            return null;
        }
        T result= (T) identityPreserver.get(this.getId());
        if (result==null){
            result = (T)newInstance();
            result.setId(this.getId());
            this.visitAttributesDualFlat(result, (thisAttribute, copyAttribute) -> {
                thisAttribute.internal_copyTo(copyAttribute,(data)->{
                    if (data==null){
                        return null;
                    }
                    return data.copyDeep(level + 1, maxLevel, identityPreserver);
                });
            });
            identityPreserver.put(this.getId(),result);
        }
        return result;
    }


    private boolean readyForUsage(){
        return root!=null;
    }

    @SuppressWarnings("unchecked")
    private <T extends Data> T endUsage() {
        for (Data data: collectChildrenDeep()){
            data.visitAttributesFlat((attributeVariableName, attribute) -> {
                attribute.internal_endUsage();
            });
        }
        return (T)this;
    }

    @SuppressWarnings("unchecked")
    <T extends Data> T propagateRoot(Data root){
        for (Data data: collectChildrenDeep()){
            data.root=root;
            data.visitAttributesFlat((attributeVariableName, attribute) -> {
                attribute.internal_prepareUsage(root);
            });
        }
        for (Data data: collectChildrenDeep()){
            data.visitAttributesFlat((attributeVariableName, attribute) -> {
                attribute.internal_afterPreparedUsage(root);
            });
        }

        return (T)this;
    }

    private <T extends Data> T prepareUsage() {
        T result = reconstructMetadataDeepRoot();
        return result.propagateRoot(result);
    }

    Data root;
    private Data getRoot(){
        return root;
    }

    private Function<List<Attribute<?>>,List<Pair<String,List<Attribute<?>>>>> attributeListGroupedSupplier=(List<Attribute<?>> allAttributes)->{
        return Arrays.asList(new Pair<>("Data",allAttributes));
    };
    private void setAttributeListGroupedSupplier(Function<List<Attribute<?>>,List<Pair<String,List<Attribute<?>>>>> attributeListGroupedSupplier){
        this.attributeListGroupedSupplier=attributeListGroupedSupplier;
    }
    private List<Pair<String,List<Attribute<?>>>> attributeListGrouped(){
        return attributeListGroupedSupplier.apply(attributeList());
    }

    private List<Attribute<?>> attributeList(){
        ArrayList<Attribute<?>> result = new ArrayList<>();
        this.visitAttributesFlat((attributeVariableName, attribute) -> {
            result.add(attribute);
        });
        return result;
    }

    private Function<String,Boolean> matchSearchTextFunction=text->{
            return Strings.isNullOrEmpty(text) || Strings.nullToEmpty(getDisplayText()).toLowerCase().contains(text.toLowerCase());
    };

    private void setMatchSearchTextFunction(Function<String,Boolean> matchSearchTextFunction) {
        this.matchSearchTextFunction=matchSearchTextFunction;
    }

    private boolean matchSearchText(String text) {
        return matchSearchTextFunction.apply(text);
    }

    private List<Attribute<?>> displayTextDependencies= Collections.emptyList();
    public void setDisplayTextDependencies(List<Attribute<?>> displayTextDependencies) {
        this.displayTextDependencies = displayTextDependencies;
    }

    private SimpleStringProperty simpleStringProperty=null;
    @JsonIgnore
    private ReadOnlyStringProperty getDisplayTextObservable() {
        if (simpleStringProperty==null){
            simpleStringProperty = new SimpleStringProperty();
            simpleStringProperty.set(getDisplayText());
            addDisplayTextListeners(this,(attributeParam, value) -> simpleStringProperty.set(getDisplayText()));
        }
        return simpleStringProperty;
    }

    @SuppressWarnings("unchecked")
    private void addDisplayTextListeners(Data data, AttributeChangeListener attributeChangeListener){
        for (Attribute<?> attribute: data.displayTextDependencies){
            attribute.internal_addListener(attributeChangeListener);
            if (attribute instanceof ReferenceAttribute<?>){
                Data nestedData= ((ReferenceAttribute)attribute).get();
                if (nestedData!=null){
                    addDisplayTextListeners(nestedData,attributeChangeListener);
                }
            }
            if (attribute instanceof ReferenceListAttribute<?>){
                List<Data> nestedDatas= ((ReferenceListAttribute)attribute).get();
                nestedDatas.forEach(nestedData -> {
                    if (nestedData!=null){
                        addDisplayTextListeners(nestedData,attributeChangeListener);
                    }
                });
            }
        }
    }

    DataUtility dataUtility = new DataUtility(this);
    /** public utility api */
    public DataUtility utility(){
        return dataUtility;
    }

    public static class DataUtility {
        private final Data data;

        public DataUtility(Data data) {
            this.data = data;
        }


        /** semantic copy can be configured on the attributes, unlike internal copy which always create complete copy with same ids*/
        public <T extends Data> T semanticCopy(){
            return data.semanticCopy();
        }

    }

    DataConfiguration dataConfiguration = new DataConfiguration(this);
    /** data configurations api */
    public DataConfiguration config(){
        return dataConfiguration;
    }

    public static class DataConfiguration {
        private final Data data;

        public DataConfiguration(Data data) {
            this.data = data;
        }

        public void setDisplayTextProvider(Supplier<String> displayTextProvider){
            data.setDisplayTextProvider(displayTextProvider);
        }

        public void setAttributeListGroupedSupplier(Function<List<Attribute<?>>,List<Pair<String,List<Attribute<?>>>>> attributeListGroupedSupplier){
            this.data.setAttributeListGroupedSupplier(attributeListGroupedSupplier);
        }

        public void setNewInstanceSupplier(Supplier<Data> newInstanceSupplier){
            this.data.setNewInstanceSupplier(newInstanceSupplier);
        }

        public void setMatchSearchTextFunction(Function<String,Boolean> matchSearchTextFunction){
            data.setMatchSearchTextFunction(matchSearchTextFunction);
        }

        public void setDisplayTextDependencies(List<Attribute<?>> attributes){
            data.setDisplayTextDependencies(attributes);
        }

        public void setDisplayTextDependencies(Attribute<?>... attributes){
            data.setDisplayTextDependencies(Arrays.asList(attributes));
        }

        public <T> void addValidation(Validation<T> validation, Attribute<?>... dependencies){
            data.addValidation(validation,dependencies);
        }

    }

    Internal internal = new Internal(this);
    /** <b>internal methods should be only used from the framework.</b>
     *  They may change in the Future.
     *  There is no fitting visibility in java therefore this workaround.
     */
    public Internal internal(){
        return internal;
    }

    public static class Internal{
        private final Data data;

        public Internal(Data data) {
            this.data = data;
        }

        public boolean matchSearchText(String newValue) {
            return data.matchSearchText(newValue);
        }

        public void visitChildFactoriesFlat(Consumer<Data> consumer) {
            data.visitChildFactoriesFlat(consumer);
        }

        public <A> void  visitAttributesDualFlat(Data modelBase, BiConsumer<Attribute<A>, Attribute<A>> consumer) {
            data.visitAttributesDualFlat(modelBase,consumer);
        }

        public void visitAttributesFlat(AttributeVisitor consumer) {
            data.visitAttributesFlat(consumer);
        }

        public void visitAttributesFlat(Consumer<Attribute<?>> consumer) {
            data.visitAttributesFlat(consumer);
        }

        public List<Pair<String,List<Attribute<?>>>> attributeListGrouped(){
            return data.attributeListGrouped();
        }

        public Map<Object,Data> collectChildFactoriesMap() {
            return data.collectChildFactoriesMap();
        }

        public <T extends Data> Set<T> collectChildrenFlat() {
            return data.collectChildrenFlat();
        }

        public Set<Data> collectChildrenDeep() {
            return data.collectChildrenDeep();
        }


        public void collectModelEntitiesTo(Set<Data> allModelEntities) {
            data.collectModelEntitiesTo(allModelEntities);
        }

        public void fixDuplicateObjects(Function<Object, Optional<Data>> getCurrentEntity) {
            data.fixDuplicateObjects(getCurrentEntity);
        }

        public String getDisplayText(){
            return data.getDisplayText();
        }

        public ReadOnlyStringProperty getDisplayTextObservable(){
            return data.getDisplayTextObservable();
        }

        public List<ValidationError> validateFlat(){
            return data.validateFlat();
        }

        public void merge(Optional<Data> originalValue, Optional<Data> newValue, MergeResult mergeResult) {
            data.merge(originalValue,newValue,mergeResult);
        }
        public List<Data> getPathFromRoot() {
            return data.root.getMassPathTo(data.root.getChildToParentMap(data.root.collectChildrenDeep()), data);
        }

        public <T extends Data> T copy() {
            return  data.copy();
        }

        public <T extends Data> T copyOneLevelDeep(){
            return data.copyOneLevelDeep();
        }

        public <T extends Data> T copyZeroLevelDeep(){
            return data.copyZeroLevelDeep();
        }

        /**
         * after serialisation or programmatically creation this mus be called first before using the object
         * to:
         * -fix jackson wrong deserialisation (metatadat ==null)
         * -propagate root node  to all chileds (for validation etc)
         *
         * unfortunately we must create a copy and can't make the same object usable(which we tried but failed)
         *
         * only call on root
         * return usable copy */
        public <T extends Data> T prepareUsableCopy() {
            return data.prepareUsage();
        }

        /** only call on root*/
        public <T extends Data> T endUsage() {
            return data.endUsage();
        }

        public boolean readyForUsage(){
            return data.readyForUsage();
        }


        public <T extends Data> T propagateRoot(Data root){
            return data.propagateRoot(root);
        }

        public Data getRoot(){
            return data.getRoot();
        }

    }


}
