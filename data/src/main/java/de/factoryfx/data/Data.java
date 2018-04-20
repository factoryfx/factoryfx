package de.factoryfx.data;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.google.common.base.Strings;
import de.factoryfx.data.attribute.*;
import de.factoryfx.data.merge.AttributeDiffInfo;
import de.factoryfx.data.merge.MergeResult;
import de.factoryfx.data.validation.AttributeValidation;
import de.factoryfx.data.validation.Validation;
import de.factoryfx.data.validation.ValidationError;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")// minimal class don't work always
public class Data {

    private String id;

    private Supplier<String> idSupplier;
    public String getId() {
        if (id == null) {
            if (idSupplier != null) {
                id = idSupplier.get();
            } else {
                id = UUID.randomUUID().toString();
            }
        }
        return id;
    }

    public void setId(String value) {
        id = value;
    }


    private void setIdSupplier(Supplier<String> idSupplier){
        this.idSupplier=idSupplier;
    }

    @JsonIgnore
    private static final Map<Class<?>, Field[]> fields = new ConcurrentHashMap<>();

    @JsonIgnore
    private Field[] getFields() {
        Field[] instanceFields = fields.get(getClass());
        if (instanceFields==null) {
            final List<Field> fieldsOrdered = getFieldsOrdered(getClass());
            instanceFields = fieldsOrdered.toArray(new Field[fieldsOrdered.size()]);
            fields.put(getClass(), instanceFields);
        }
        return instanceFields;
    }

    private List<Field> getFieldsOrdered(Class<?> clazz) {
        ArrayList<Field> fields = new ArrayList<>();
        Class<?> parent = clazz.getSuperclass();
        if (parent!=null){// skip Object
            fields.addAll(getFieldsOrdered(parent));
        }
        Stream.of(clazz.getDeclaredFields()).filter(f->Modifier.isPublic(f.getModifiers())).filter(f->!Modifier.isStatic(f.getModifiers())).forEach(fields::add);
        return fields;
    }

    @FunctionalInterface
    public interface TriAttributeVisitor {
        void accept(String attributeName, Attribute<?,?> attribute1, Attribute<?,?> attribute2, Attribute<?,?> attribute3);
    }

    @FunctionalInterface
    public interface BiAttributeVisitor {
        void accept(String attributeName, Attribute<?,?> attribute1, Attribute<?,?> attribute2);
    }

    @FunctionalInterface
    public interface AttributeVisitor{
        void accept(String attributeVariableName, Attribute<?,?> attribute);
    }

    private void visitAttributesFlat(AttributeVisitor consumer) {
//        for (AttributeAndName attribute: getAttributes()){
//            consumer.accept(attribute.name,attribute.attribute);
//        }

        Field[] fields = getFields();
        for (Field field : fields) {
            try {
                if (Attribute.class.isAssignableFrom(field.getType())) {
                    consumer.accept(field.getName(),(Attribute<?,?>) field.get(this));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void visitAttributesDualFlat(Data data, BiAttributeVisitor consumer) {
        Field[] fields = getFields();
        for (Field field : fields) {
            try {
                if (Attribute.class.isAssignableFrom(field.getType())) {
                    consumer.accept(field.getName(),(Attribute<?,?>) field.get(this), (Attribute<?,?>) field.get(data));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void visitAttributesTripleFlat(Data data1, Data data2, TriAttributeVisitor consumer) {
        Field[] fields = getFields();
        for (Field field : fields) {
            try {
                if (Attribute.class.isAssignableFrom(field.getType())) {
                    consumer.accept(field.getName(),(Attribute<?,?>) field.get(this), (Attribute<?,?>) field.get(data1), (Attribute<?,?>) field.get(data2));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Map<String,Data> collectChildDataMap() {
        List<Data> dataList = collectChildrenDeep();
        HashMap<String, Data> result = new HashMap<>();
        for (Data factory: dataList){
            result.put(factory.getId(),factory);
        }
        return result;
    }

    /** collect set with all nested children and itself*/
    private List<Data> collectChildrenDeep() {
        ArrayList<Data> dataList = new ArrayList<>();
        collectModelEntitiesTo(dataList);
        dataList.forEach(d->d.collected=false);
        return dataList;
    }


    boolean collected=false;
    void collectModelEntitiesTo(List<Data> allModelEntities) {
        if (!collected){
            allModelEntities.add(this);
            collected=true;
            visitAttributesFlat((attributeVariableName, attribute) -> {
                attribute.internal_visit(data -> data.collectModelEntitiesTo(allModelEntities));
            });
        }
    }


    private Supplier<Data> newInstanceSupplier=null;
    private void setNewInstanceSupplier(Supplier<Data> newInstanceSupplier){
        this.newInstanceSupplier=newInstanceSupplier;
    }
    Data newInstance() {
        final Data result;
        if (newInstanceSupplier!=null){
            result=newInstanceSupplier.get();
        } else {
            try {
                Constructor constructor = Data.this.getClass().getDeclaredConstructor();
                constructor.setAccessible(true);
                result = (Data) constructor.newInstance(new Object[0]);
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private Set<Data> fixDuplicateObjects() {
        Map<String, Data> idToDataMap = collectChildDataMap();
        final Set<Data> all = new HashSet<>(idToDataMap.values());
        for (Data data: all){
            data.visitAttributesFlat((attributeVariableName, attribute) -> attribute.internal_fixDuplicateObjects(idToDataMap));
        }
        return all;
    }

    //remove parents that are not in the merge result
    private void cleanUpParents(Set<Data> all) {
        for (Data data: all){
            data.parents.removeIf(parent -> !all.contains(parent));
        }
    }

    private Supplier<String> displayTextProvider;

    @JsonIgnore
    @SuppressWarnings("unchecked")
    private String getDisplayText(){
        if (displayTextProvider==null){
            return Data.this.getClass().getSimpleName()+":"+getId();
        }
        return displayTextProvider.get();
    }

    private void setDisplayTextProvider(Supplier<String> displayTextProvider){
        this.displayTextProvider=displayTextProvider;
    }

    /** validate attributes without visiting child factories*/
    @SuppressWarnings("unchecked")
    private List<ValidationError> validateFlat(){
        final ArrayList<ValidationError> result = new ArrayList<>();
        final Map<Attribute<?,?>, List<ValidationError>> attributeListMap = validateFlatMapped();
        for (Map.Entry<Attribute<?,?>, List<ValidationError>> entry: attributeListMap.entrySet()){
            result.addAll(entry.getValue());
        }

        return result;
    }

    /** validate attributes without visiting child factories
     */
    private Map<Attribute<?,?>,List<ValidationError>> validateFlatMapped(){
        Map<Attribute<?,?>,List<ValidationError>> result= new HashMap<>();

        visitAttributesFlat((attributeVariableName, attribute) -> {
            final ArrayList<ValidationError> validationErrors = new ArrayList<>();
            result.put(attribute, validationErrors);
            validationErrors.addAll(attribute.internal_validate(this));
        });

        if (dataValidations!=null){
            for (AttributeValidation<?> validation: dataValidations){
                final Map<Attribute<?,?>, List<ValidationError>> validateResult = validation.validate(this);
                for (Map.Entry<Attribute<?,?>, List<ValidationError>> entry: validateResult.entrySet()){
                    result.get(entry.getKey()).addAll(entry.getValue());
                }
            }
        }
        return result;
    }

    private List<AttributeValidation<?>> dataValidations;
    private <T> void addValidation(Validation<T> validation, Attribute<?,?>... dependencies){
        if (dataValidations==null){
            dataValidations = new ArrayList<>();
        }
        for ( Attribute<?,?> dependency: dependencies){
            dataValidations.add(new AttributeValidation<>(validation,dependency));
        }
    }

    @SuppressWarnings("unchecked")
    private void merge(Data originalValue, Data newValue, MergeResult mergeResult, Function<String,Boolean> permissionChecker) {
        this.visitAttributesTripleFlat(originalValue, newValue, (attributeName, currentAttribute, originalAttribute, newAttribute) -> {
            if (!currentAttribute.internal_ignoreForMerging()){
                if (currentAttribute.internal_hasMergeConflict(originalAttribute, newAttribute)) {
                    mergeResult.addConflictInfo(new AttributeDiffInfo(Data.this.getId(), attributeName));
                } else {
                    if (currentAttribute.internal_isMergeable(originalAttribute, newAttribute)) {
                        final AttributeDiffInfo attributeDiffInfo = new AttributeDiffInfo(attributeName,Data.this.getId());
                        if (currentAttribute.internal_hasWritePermission(permissionChecker)){
                            mergeResult.addMergeInfo(attributeDiffInfo);
                            mergeResult.addMergeExecutions(() -> currentAttribute.internal_merge(newAttribute));
                        } else {
                            mergeResult.addPermissionViolationInfo(attributeDiffInfo);
                        }
                    }
                }
            }
        });
    }

    private HashMap<Data, Data> getChildToParentMap(List<Data> allModelEntities) {
        HashMap<Data, Data> result = new HashMap<>();
        for (Data factoryBase : allModelEntities) {
            factoryBase.visitAttributesFlat((attributeVariableName, attribute) -> {
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

    @SuppressWarnings("unchecked")
    private <T extends Data> T semanticCopy() {
        T result = (T)newInstance();
//        result.setId(this.getId());
        this.visitAttributesDualFlat(result, (attributeName, attribute1, attribute2) -> attribute1.internal_semanticCopyToUnsafe(attribute2));

        this.fixDuplicateObjects();
        return result;
    }

    /**copy including one the references first level of nested references*/
    private <T extends Data> T copyOneLevelDeep(){
        return copy(1,null,null);
    }

    /**copy without nested references, only value attributes are copied*/
    private <T extends Data> T copyZeroLevelDeep(){
        return copy(0,null,null);
    }

    private <T extends Data> T copy() {
        return copy(Integer.MAX_VALUE,null,null);
    }


    @SuppressWarnings("unchecked")
    private <T extends Data> T copy(int level, Consumer<Data> newDataConsumer, Consumer<Attribute<?, ?>> newAttributesConsumer) {
        ArrayList<Attribute<?, ?>> newAttributes = new ArrayList<>();
        ArrayList<Data> oldDataList = new ArrayList<>();
        Data newRoot = copyDeep(0, level, null,oldDataList);

        for (Data oldData : oldDataList) {
            oldData.copy.visitAttributesFlat((attributeVariableName, attribute) -> {
                oldData.copy.root = newRoot;
                attribute.internal_prepareUsage(newRoot, oldData.copy);
                if (newAttributesConsumer != null) {
                    newAttributesConsumer.accept(attribute);
                }
                newAttributes.add(attribute);
            });
            if (newDataConsumer!=null){
               newDataConsumer.accept(oldData.copy);
            }
            oldData.copy=null;//clenup
        }

        if (newRoot!=null){
            newRoot.afterPreparedUsage(newAttributes);
        }

        return (T) newRoot;
    }

    private Data copy;
    private Data copyDeep(final int level, final int maxLevel, final Data parent, final List<Data> oldData){
        if (level>maxLevel){
            return null;
        }
        if (copy==null){
            copy = newInstance();
            copy.id=this.id;
            copy.isUsable=true;//only possible to created copies form usable data therefore copy is always usable

            this.visitAttributesDualFlat(copy, (name, thisAttribute, copyAttribute) -> {
//                newAttributes.add(copyAttribute);
                if (thisAttribute!=null){//cause jackson decided it's a good idea to override the final field with null
                    thisAttribute.internal_copyToUnsafe(copyAttribute,(data)->{
                        if (data==null){
                            return null;
                        }
                        return data.copyDeep(level + 1, maxLevel,copy,oldData);
                    });
                }
            });

            oldData.add(this);
        }
        if (parent!=null){
            copy.parents.add(parent);
        }
        return copy;
    }


    private boolean readyForUsage(){
        return root!=null;
    }


    private void endUsage() {
        for (Data data: collectChildrenDeep()){
            data.visitAttributesFlat((attributeVariableName, attribute) -> {
                attribute.internal_endUsage();
            });
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends Data> T afterPreparedUsage(Collection<Attribute<?,?>> newAttributesForAfterInitialise){

        for (Attribute<?,?> attribute: newAttributesForAfterInitialise){
            attribute.internal_afterPreparedUsage();
        }
        return (T)this;
    }

    @SuppressWarnings("unchecked")
    <T extends Data> T propagateRootAndParent(Data root, Data parent){
        this.parents.add(parent);
        final List<Data> childrenDeep = collectChildrenDeep();
        final ArrayList<Attribute<?,?>> attributes=new ArrayList<>();
        for (Data data: childrenDeep){
            data.root=root;
            data.visitAttributesFlat((attributeVariableName, attribute) -> {
                attribute.internal_prepareUsage(root,data);
            });
        }

        afterPreparedUsage(attributes);
        return (T)this;
    }

    /**
     * after serialisation or programmatically creation this mus be called first before using the object<br>
     * to:<br>
     * -fix jackson wrong deserialization (metadata ==null)<br>
     * -propagate root/parent node to all children (for validation etc)<br>
     * -init ids
     *<br>
     * unfortunately we must create a copy and can't make the same object usable(which we tried but failed)<br>
     *<br>
     * only call on root<br>
     *<br>
     * @param <T> type
     * @return usable copy
     *
     */
    private <T extends Data> T prepareUsableCopy(Consumer<Data> newData, Consumer<Attribute<?,?>> newAttributes) {

        /*
          after deserialization from json only the value is present and metadata are missing
          we create a copy which contains the metadata and than copy then transfer the value in the new  copy (which is what conveniently copy does)
         */
        T usableCopy = this.copy(Integer.MAX_VALUE, data -> {
            //init ids, id could depend on parents, usable copy must have init ids else a copy is broken;
            data.getId();
            data.setUsable();
            if (newData!=null){
                newData.accept(data);
            }
        }, newAttributes);


        return usableCopy;
    }

    private boolean isUsable=false;
    @JsonIgnore
    private boolean isUsable(){
        return isUsable;
    }
    private void setUsable(){
        isUsable=true;
    }

    private Set<Data> parents=new HashSet<>();

    private Set<Data> getParents(){
        return parents;
    }

    private Data root;
    private Data getRoot(){
        return root;
    }

    private Function<List<Attribute<?,?>>,List<AttributeGroup>> attributeListGroupedSupplier;
    private void setAttributeListGroupedSupplier(Function<List<Attribute<?,?>>,List<AttributeGroup>> attributeListGroupedSupplier){
        this.attributeListGroupedSupplier=attributeListGroupedSupplier;
    }
    private List<AttributeGroup> attributeListGrouped(){
        if (attributeListGroupedSupplier==null){
            return Collections.singletonList(new AttributeGroup("Data", attributeList()));
        }
        return attributeListGroupedSupplier.apply(attributeList());
    }

    private List<Attribute<?,?>> attributeList(){
        ArrayList<Attribute<?,?>> result = new ArrayList<>();
        this.visitAttributesFlat((attributeVariableName, attribute) -> {
            result.add(attribute);
        });
        return result;
    }

    private Function<String,Boolean> matchSearchTextFunction;

    private void setMatchSearchTextFunction(Function<String,Boolean> matchSearchTextFunction) {
        this.matchSearchTextFunction=matchSearchTextFunction;
    }

    private boolean matchSearchText(String text) {
        if (matchSearchTextFunction==null){
            return Strings.isNullOrEmpty(text) || Strings.nullToEmpty(getDisplayText()).toLowerCase().contains(text.toLowerCase());
        }
        return matchSearchTextFunction.apply(text);
    }

    private List<Attribute<?,?>> displayTextDependencies= Collections.emptyList();
    private void setDisplayTextDependencies(List<Attribute<?,?>> displayTextDependencies) {
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
        for (Attribute<?,?> attribute: data.displayTextDependencies){
            attribute.internal_addListener(attributeChangeListener);
            attribute.internal_visit(data1 -> addDisplayTextListeners(data1,attributeChangeListener));
        }
    }


    private boolean hasCustomDisplayText(){
        return displayTextProvider!=null;
    }

    private DataUtility dataUtility;
    /**public utility api
     * @return the api
     */
    public DataUtility utility(){
        if (dataUtility==null){
            dataUtility = new DataUtility(this);
        }
        return dataUtility;
    }

    public static class DataUtility {
        private final Data data;

        public DataUtility(Data data) {
            this.data = data;
        }


        /** semantic copy can be configured on the attributes, unlike internal copy which always create complete copy with same ids
         *
         * @param <T> type
         * @return self
         */
        public <T extends Data> T semanticCopy(){
            return data.semanticCopy();
        }

        /**
         * see: {@link Data#prepareUsableCopy}
         * @param <T> type
         * @return usableCopy
         */
        public <T extends Data> T prepareUsableCopy() {
            return data.prepareUsableCopy(null,null);
        }

    }

    private final DataConfiguration dataConfiguration = new DataConfiguration(this);
    /** data configurations api. Should be used in the default constructor
     * @return the configuration api*/
    protected DataConfiguration config(){
        return dataConfiguration;
    }

    public static class DataConfiguration {
        private final Data data;

        public DataConfiguration(Data data) {
            this.data = data;
        }
        /**
         * short readable text describing the factory
         * @param displayTextProvider displayTextProvider
         */
        public void setDisplayTextProvider(Supplier<String> displayTextProvider){
            data.setDisplayTextProvider(displayTextProvider);
        }

        /**
         * short readable text describing the factory
         * @param displayTextProvider custom displayText function
         * @param dependencies attributes which affect the display text
         */
        public void setDisplayTextProvider(Supplier<String> displayTextProvider, Attribute<?,?>... dependencies){
            data.setDisplayTextProvider(displayTextProvider);
            data.setDisplayTextDependencies(Arrays.asList(dependencies));
        }

        /**
         * @see  #setDisplayTextDependencies(Attribute[])
         * @param attributes the attributes affecting the display text
         */
        public void setDisplayTextDependencies(List<Attribute<?,?>> attributes){
            data.setDisplayTextDependencies(attributes);
        }

        /** set the attributes that affect the display text<br>
         *  used for live update in gui
         *
         * @param attributes the attributes affecting the display text
         * */
        public void setDisplayTextDependencies(Attribute<?,?>... attributes){
            data.setDisplayTextDependencies(Arrays.asList(attributes));
        }

        /**
         *  grouped iteration over attributes e.g. used in gui editor where each group is a new Tab
         *
         * @param attributeListGroupedSupplier function with parameter containing all attributes
         * */
        public void setAttributeListGroupedSupplier(Function<List<Attribute<?,?>>,List<AttributeGroup>> attributeListGroupedSupplier){
            this.data.setAttributeListGroupedSupplier(attributeListGroupedSupplier);
        }

        /**
         *  new Instance configuration default in over reflection over default constructor
         *  used for copies
         *
         * @param newInstanceSupplier newInstanceSupplier
         * */
        public void setNewInstanceSupplier(Supplier<Data> newInstanceSupplier){
            this.data.setNewInstanceSupplier(newInstanceSupplier);
        }

        /**
         *  define match logic for full-text search e.g. in tables
         *
         * @param matchSearchTextFunction matchSearchTextFunction
         * */
        public void setMatchSearchTextFunction(Function<String,Boolean> matchSearchTextFunction){
            data.setMatchSearchTextFunction(matchSearchTextFunction);
        }


        /**
         * data validation
         * @param validation validation function
         * @param dependencies attributes which affect the validation
         * @param <T> this
         */
        public <T> void addValidation(Validation<T> validation, Attribute<?,?>... dependencies){
            data.addValidation(validation,dependencies);
        }

        /**
         * use id derived from attributes instead of uuid
         *
         * @param customIdSupplier id supplier
         */
        public void attributeId(Supplier<String> customIdSupplier){
            data.setIdSupplier(customIdSupplier);
        }

    }

    private final Internal internal = new Internal(this);
    /** <b>internal methods should be only used from the framework.</b>
     *  They may change in the Future.
     *  There is no fitting visibility in java therefore this workaround.
     * @return the internal api
     */
    public Internal internal(){
        return internal;
    }

    public static class Internal  {
        private final Data data;

        public Internal(Data data) {
            this.data = data;
        }

        public boolean matchSearchText(String newValue) {
            return data.matchSearchText(newValue);
        }

        public <T extends Data> T semanticCopy() {
            return data.semanticCopy();
        }

        public void visitAttributesDualFlat(Data modelBase, BiAttributeVisitor consumer) {
            data.visitAttributesDualFlat(modelBase,consumer);
        }

        public void visitAttributesFlat(AttributeVisitor consumer) {
            data.visitAttributesFlat(consumer);
        }


        public List<AttributeGroup> attributeListGrouped(){
            return data.attributeListGrouped();
        }

        public Map<String,Data> collectChildDataMap() {
            return data.collectChildDataMap();
        }

        public List<Data> collectChildrenDeep() {
            return data.collectChildrenDeep();
        }


        public void collectModelEntitiesTo(List<Data> allModelEntities) {
            data.collectModelEntitiesTo(allModelEntities);
        }


        /**fix all data with same id should be same object
         * only call on root
         * */
        public void fixDuplicateData() {
            data.fixDuplicateObjects();
        }

        /**
         * -fix all data with same id should be same object
         * -remove parents that are no not tin the tree
         * only call on root
         * */
        public void fixDuplicatesAndParents() {
            Set<Data> data = this.data.fixDuplicateObjects();
            this.data.cleanUpParents(data);
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

        public Map<Attribute<?,?>,List<ValidationError>> validateFlatMapped(){
            return data.validateFlatMapped();
        }

        public void merge(Data originalValue, Data newValue, MergeResult mergeResult, Function<String,Boolean> permissionChecker) {
            data.merge(originalValue,newValue,mergeResult,permissionChecker);
        }
        public List<Data> getPathFromRoot() {
            return data.root.getMassPathTo(data.root.getChildToParentMap(data.root.collectChildrenDeep()), data);
        }

        public List<Data> getPathFromRoot(HashMap<Data, Data> childToParentMap) {
            return data.root.getMassPathTo(childToParentMap, data);
        }

        public <T extends Data> T copy() {
            this.checkUsable();
            return data.copy();
        }

        /** copy a root data element
         * @param <T> type
         * @return root copy
         */
        public <T extends Data> T copyFromRoot() {
            return data.copy();
        }

        public <T extends Data> T copyOneLevelDeep(){
            return data.copyOneLevelDeep();
        }

        public <T extends Data> T copyZeroLevelDeep(){
            return data.copyZeroLevelDeep();
        }

        /**
         * see: {@link Data#prepareUsableCopy}
         * @param <T> type
         * @return usableCopy
         */
        public <T extends Data> T prepareUsableCopy() {
            return data.prepareUsableCopy(null,null);
        }

        public <T extends Data> T prepareUsableCopy(Consumer<Data> newData, Consumer<Attribute<?,?>> newAttributes) {
            return data.prepareUsableCopy(newData, newAttributes);
        }

        /** only call on root*/
        public void endUsage() {
            data.endUsage();
        }

        public boolean readyForUsage(){
            return data.readyForUsage();
        }


        /**
         *
         * @param root root
         * @param parent parent
         * @param <T> type
         * @return root
         */
        public <T extends Data> T propagateRootAndParent(Data root, Data parent){
            return data.propagateRootAndParent(root,parent);
        }

        public Data getRoot(){
            return data.getRoot();
        }

        /** use getParents instead
         * @return parent*/
        @Deprecated
        public Data getParent(){
            if (data.getParents().isEmpty()){
                return null;
            }
            return data.getParents().iterator().next();
        }

        public Set<Data> getParents(){
            return data.getParents();
        }

        public HashMap<Data, Data> getChildToParentMap() {
            return data.getChildToParentMap(data.root.collectChildrenDeep());
        }

        public boolean hasCustomDisplayText(){
            return data.hasCustomDisplayText();
        }

        public boolean isUsable() {
            return data.isUsable();
        }

        /**
         * see {@link Data#prepareUsableCopy }
         */
        public void checkUsable() {
            if (!isUsable()){
                throw new IllegalStateException("passed data is not a usableCopy use prepareUsableCopy(); e.g.:\n  data=data.utility().prepareUsableCopy();");
            }
        }

    }

}
