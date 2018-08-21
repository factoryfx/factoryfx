package de.factoryfx.data;

import com.fasterxml.jackson.annotation.*;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import de.factoryfx.data.attribute.*;
import de.factoryfx.data.merge.AttributeDiffInfo;
import de.factoryfx.data.merge.MergeResult;
import de.factoryfx.data.validation.AttributeValidation;
import de.factoryfx.data.validation.Validation;
import de.factoryfx.data.validation.ValidationError;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")// minimal class don't work always
public class Data {

    @JsonProperty
    Object id;

    private Supplier<String> idSupplier;
    private static final Random r = new Random();


    public String getId() {
        if (id == null) {
            if (idSupplier != null) {
                id = idSupplier.get();
            } else {
                id = new UUID(r.nextLong(), r.nextLong());
                //TODO does this increase collision probability?
                // Secure random in UUID.randomUUID().toString() is too slow and it doesn't matter if the random is predictable
            }
        }
        return id.toString();
    }

    public void setId(String value) {
        id = value;
    }


    private void setIdSupplier(Supplier<String> idSupplier){
        this.idSupplier=idSupplier;
    }

    /**
     * equal using id, performance optimization if both id are UUID
     *
     * @param data data
     * @return true if equals*/
    public boolean idEquals(Data data){
        if (id instanceof UUID && data.id instanceof UUID){
            return id.equals(data.id);
        } else {
            return getId().equals(data.getId());
        }
    }

    @SuppressWarnings("unchecked")
    private DataDictionary<Data> getDataDictionary(){
        return (DataDictionary<Data>)DataDictionary.getDataDictionary(getClass());
    }

    @FunctionalInterface
    public interface TriAttributeVisitor {
        void accept(String attributeName, Attribute<?,?> attribute1, Attribute<?,?> attribute2, Attribute<?,?> attribute3);
    }

    @FunctionalInterface
    public interface BiAttributeVisitor {
        void accept(String attributeName, Attribute<?,?> attribute1, Attribute<?,?> attribute2);
    }

    private void visitAttributesFlat(AttributeVisitor consumer) {
        getDataDictionary().visitAttributesFlat(this,consumer);
    }

    @SuppressWarnings("unchecked")
    private void visitAttributesDualFlat(Data data, BiAttributeVisitor consumer) {
        getDataDictionary().visitAttributesDualFlat(this,data,consumer);
    }

    private void visitAttributesTripleFlat(Data other1, Data other2, TriAttributeVisitor consumer) {
        getDataDictionary().visitAttributesTripleFlat(this,other1,other2,consumer);
    }

    private Map<String,Data> collectChildDataMap() {
        List<Data> dataList = collectChildrenDeep();
        HashMap<String, Data> result = Maps.newHashMapWithExpectedSize(dataList.size());
        for (Data factory: dataList){
            result.put(factory.getId(),factory);
        }
        return result;
    }

    /** collect set with all nested children and itself*/
    private List<Data> collectChildrenDeep() {
        ArrayList<Data> dataList = new ArrayList<>();
        collectModelEntitiesTo(dataList,this.dataIterationRun+1);
        return dataList;
    }

    @JsonIgnore
    long dataIterationRun=0;
    private void visitDataChildrenFlat(Consumer<Data> childFactoriesVisitor, long dataIterationRun){
        if (this.dataIterationRun==dataIterationRun){
            return;
        }
        this.dataIterationRun=dataIterationRun;
        getDataDictionary().visitDataChildren(this,childFactoriesVisitor);
    }


    void collectModelEntitiesTo(List<Data> allModelEntities, long dataIterationRun) {
        allModelEntities.add(this);
        visitDataChildrenFlat(child -> child.collectModelEntitiesTo(allModelEntities,dataIterationRun),dataIterationRun);
    }

    private Data newCopyInstance(Data data) {
        return getDataDictionary().newCopyInstance(data);
    }

    @SuppressWarnings("unchecked")
    private List<Data> fixDuplicateObjects() {
        Map<String, Data> idToDataMap = collectChildDataMap();
        final List<Data> all = new ArrayList<>(idToDataMap.values());
        for (Data data: all){
            data.visitAttributesFlat((attributeVariableName, attribute) -> attribute.internal_fixDuplicateObjects(idToDataMap));
        }
        return all;
    }

    private Supplier<String> displayTextProvider;

    @JsonIgnore
    @SuppressWarnings("unchecked")
    private String getDisplayText(){
        if (displayTextProvider==null){
//            return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN,Data.this.getClass().getSimpleName()).replace("-"," ");
            return Data.this.getClass().getSimpleName();
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
            validationErrors.addAll(attribute.internal_validate(this,attributeVariableName));
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

    private HashMap<Data, Data> getChildToParentMap(List<Data> allModelEntities,long dataIterationRun) {
        HashMap<Data, Data> result = new HashMap<>();
        for (Data factoryBase : allModelEntities) {
            factoryBase.visitDataChildrenFlat(child->result.put(child, factoryBase),dataIterationRun);
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
        T result = (T)newCopyInstance(this);
        this.visitAttributesDualFlat(result, (attributeName, attribute1, attribute2) -> attribute1.internal_semanticCopyToUnsafe(attribute2));
        return result;
    }

    /**copy including one the references first level of nested references*/
    private <T extends Data> T copyOneLevelDeep(){
        return copy(1);
    }

    /**copy without nested references, only value attributes are copied*/
    private <T extends Data> T copyZeroLevelDeep(){
        return copy(0);
    }

    private <T extends Data> T copy() {
        return copy(Integer.MAX_VALUE);
    }


    @SuppressWarnings("unchecked")
    private <T extends Data> T copy(int level) {
        ArrayList<Data> oldDataList = new ArrayList<>();
        Data newRoot = copyDeep(0, level,oldDataList,null,this);

        for (Data oldData: oldDataList) {
            oldData.copy=null;//cleanup
        }

        return (T) newRoot;
    }

    private Data copy;
    private Data copyDeep(final int level, final int maxLevel, final List<Data> oldData, Data parent, Data root){
        if (level>maxLevel){
            return null;
        }
        if (copy==null){
            copy = newCopyInstance(this);
            if (this.id==null){
                getId();
            }
            if (this.id instanceof UUID){
                copy.id = this.id;
            } else {
                copy.id = this.getId();
            }


            this.visitAttributesDualFlat(copy, (name, thisAttribute, copyAttribute) -> {
                thisAttribute.internal_copyToUnsafe(copyAttribute,(data)->{
                    if (data==null){
                        return null;
                    }
                    return data.copyDeep(level + 1, maxLevel,oldData,this,root);
                });

                copyAttribute.internal_prepareUsageFlat(root.copy,copy);
            });

            oldData.add(this);

            //add BackReferences
            if (parent!=null){
                copy.addParent(parent.copy);
            }
            copy.root=root.copy;

        }
        return copy;
    }


    private boolean readyForUsage(){
        return root!=null;
    }


    private void endUsage() {
        for (Data data: collectChildrenDeep()){
            data.visitAttributesFlat((attributeVariableName, attribute) -> attribute.internal_endUsage());
        }
    }


    /**
     * after serialisation or programmatically creation this mus be called first before using the object<br>
     * to:<br>
     * -propagate root/parent node to all children (for validation etc)<br>
     * -init ids
     *<br>
     * only call on root<br>
     *<br>
     */
    private void addBackReferences() {
        addBackReferences(this,null,this.dataIterationRun+1);
    }


    private void addBackReferences(final Data root, final Data parent, long dataIterationRun){
        addParent(parent);
        this.root=root;
        getDataDictionary().addBackReferencesToAttributes(this,root);
        this.visitDataChildrenFlat(data -> data.addBackReferences(root, Data.this,dataIterationRun),dataIterationRun);
    }

    private void addBackReferencesForSubtree(Data root, Data parent, HashSet<Data> visited){
        addParent(parent);
        this.root=root;
        getDataDictionary().addBackReferencesToAttributes(this,root);

        if (visited.add(this)) {//use HashSet instead of iteration counter to avoid iterationCounter mix up
            getDataDictionary().visitDataChildren(this, child -> child.addBackReferencesForSubtree(root,this,visited));
        }
    }

    private Set<Data> parents;
    private Data parent;

    private void addParent(Data parent){
        if (this.parent==null || this.parent==parent){
            this.parent=parent;
        } else {
            if (parents==null){
                parents=new HashSet<>();
                parents.add(this.parent);
                parents.add(parent);
                if (parents.size()==1){
                    System.out.println();
                }
            } else {
                parents.add(parent);
            }
        }
    }

    private void resetBackReferencesFlat(){
        this.parents=null;
        this.parent=null;
        this.root=null;
    }

    private boolean hasBackReferencesFlat(){
        return this.root!=null;
    }

    @JsonIgnore
    private Set<Data> getParents(){
        if (parents==null){
            if (parent==null){
                return Collections.emptySet();
            }
            return Set.of(parent);
        } else {
            return parents;
        }
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
        this.visitAttributesFlat((attributeVariableName, attribute) -> result.add(attribute));
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

    @SuppressWarnings("unchecked")
    private void addDisplayTextListeners(Data data, AttributeChangeListener attributeChangeListener){
        for (Attribute<?,?> attribute: data.displayTextDependencies){
            attribute.internal_addListener(attributeChangeListener);
        }
    }

    private Object storeDisplayTextObservable;
    private void storeDisplayTextObservable(Object simpleStringProperty) {
        storeDisplayTextObservable=simpleStringProperty;
    }

    private DataUtility dataUtility;
    /**public utility api
     * @return the api
     */
    public DataUtility utility(){
        return new DataUtility(this);
    }

    public static class DataUtility {
        private final Data data;

        public DataUtility(Data data) {
            this.data = data;
        }


        /** semantic copy can be configured on the attributes, unlike copy which always create complete copy with same ids
         *
         * @param <T> type
         * @return self
         */
        public <T extends Data> T semanticCopy(){
            return data.semanticCopy();
        }

        /**
         * copy with same ids and data
         * @param <T> self
         * @return scopy
         */
        public <T extends Data> T copy(){
            return data.copy();
        }

    }

    /** data configurations api. Should be used in the default constructor
     * @return the configuration api*/
    protected DataConfiguration config(){
        return new DataConfiguration(this);
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


    /** <b>internal methods should be only used from the framework.</b>
     *  They may change in the Future.
     *  There is no fitting visibility in java therefore this workaround.
     * @return the internal api
     */
    public Internal internal(){
        return new Internal(this);
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
            data.assertRoot();
            return data.collectChildDataMap();
        }

        /**
         * @return all data including root and no duplicates
         * */
        public List<Data> collectChildrenDeep() {
            data.assertRoot();
            return data.collectChildrenDeep();
        }


        /**fix all data with same id should be same object
         * only call on root
         * */
        public void fixDuplicateData() {
            data.assertRoot();
            data.fixDuplicateObjects();
        }

        /**
         * -fix all data with same id should be same object
         * -remove parents that are no not tin the tree
         * only call on root
         * */
        public void fixDuplicatesAndAddBackReferences() {
            data.assertRoot();
            List<Data> dataList = this.data.fixDuplicateObjects();
            for (Data data : dataList) {
                data.resetBackReferencesFlat();
            }
            this.data.addBackReferences();
        }

        public String getDisplayText(){
            return data.getDisplayText();
        }

        //TODO cleanup the hack (goal is to remove javafx dependency )
        public void storeDisplayTextObservable(Object simpleStringProperty){
            this.data.storeDisplayTextObservable(simpleStringProperty);
        }

        public Object getDisplayTextObservable(){
            return this.data.storeDisplayTextObservable;
        }

        public List<ValidationError> validateFlat(){
            return data.validateFlat();
        }


        public void merge(Data originalValue, Data newValue, MergeResult mergeResult, Function<String,Boolean> permissionChecker) {
            data.merge(originalValue,newValue,mergeResult,permissionChecker);
        }
        public List<Data> getPathFromRoot() {
            return data.root.getMassPathTo(data.root.getChildToParentMap(data.root.collectChildrenDeep(),data.dataIterationRun+1), data);
        }

        public List<Data> getPathFromRoot(HashMap<Data, Data> childToParentMap) {
            return data.root.getMassPathTo(childToParentMap, data);
        }

        public <T extends Data> T copy() {
            return data.copy();
        }

        public <T extends Data> T copyOneLevelDeep(){
            return data.copyOneLevelDeep();
        }

        public <T extends Data> T copyZeroLevelDeep(){
            return data.copyZeroLevelDeep();
        }

        /**
         * see: {@link Data#addBackReferences}
         * @param <T> type
         * @return usableCopy
         */
        @SuppressWarnings("unchecked")
        public <T extends Data> T addBackReferences() {
            if (!this.data.hasBackReferencesFlat()){
                this.data.addBackReferences();
            }
            return (T)data;
        }

        /** only call on root*/
        public void endUsage() {
            data.endUsage();
        }

        public boolean readyForUsage(){
            return data.readyForUsage();
        }


        /**
         * @param root root
         * @param parent parent
         */
        public void addBackReferencesForSubtree(Data root, Data parent){
            data.addBackReferencesForSubtree(root,parent,new HashSet<>());
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
            return data.getChildToParentMap(data.root.collectChildrenDeep(),data.dataIterationRun+1);
        }

        public void addDisplayTextListeners(AttributeChangeListener attributeChangeListener){
            data.addDisplayTextListeners(data, attributeChangeListener);
        }

        public boolean hasBackReferencesFlat() {
            return data.hasBackReferencesFlat();
        }

        public void resetIterationCounterFlat() {
            data.dataIterationRun=0;
        }

        public void assertRoot(){
            data.assertRoot();
        }
    }

    private void assertRoot() {
        if (this.root!=this){
            throw new IllegalStateException("can only be called from root this.root="+this.root);
        }
    }


}
