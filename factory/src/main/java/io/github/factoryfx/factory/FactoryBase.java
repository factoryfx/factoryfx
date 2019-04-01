package io.github.factoryfx.factory;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.fasterxml.jackson.annotation.*;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.attribute.AttributeChangeListener;
import io.github.factoryfx.factory.attribute.AttributeGroup;
import io.github.factoryfx.factory.attribute.dependency.RootAwareAttribute;
import io.github.factoryfx.factory.log.FactoryLogEntry;
import io.github.factoryfx.factory.log.FactoryLogEntryEventType;
import io.github.factoryfx.factory.log.FactoryLogEntryTreeItem;
import io.github.factoryfx.factory.merge.AttributeDiffInfo;
import io.github.factoryfx.factory.merge.MergeResult;
import io.github.factoryfx.factory.metadata.FactoryMetadata;
import io.github.factoryfx.factory.metadata.FactoryMetadataManager;
import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadata;
import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadataDictionary;
import io.github.factoryfx.factory.validation.AttributeValidation;
import io.github.factoryfx.factory.validation.Validation;
import io.github.factoryfx.factory.validation.ValidationError;
import io.github.factoryfx.server.Microservice;
import org.slf4j.LoggerFactory;

/**
 * @param <L> liveobject created from this factory
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", resolver = DataObjectIdResolver.class)
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")// minimal class don't work always
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FactoryBase<L,R extends FactoryBase<?,R>> {

    @JsonProperty
    Object id;

    private Supplier<String> idSupplier;
    private static final Random r = new Random();

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FactoryBase.class);

    @JsonIgnore
    private L createdLiveObject;
    @JsonIgnore
    private boolean started=false;
    @JsonIgnore
    boolean needRecreation =false;
    @JsonIgnore
    private L previousLiveObject;

    @JsonProperty
    private String treeBuilderName;

    public FactoryBase() {

    }


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
    public boolean idEquals(FactoryBase<?,?> data){
        if (id instanceof UUID && data.id instanceof UUID){
            return id.equals(data.id);
        } else {
            return getId().equals(data.getId());
        }
    }

    private FactoryMetadata<R,L, FactoryBase<L, R>> metadata;
    @SuppressWarnings("unchecked")
    private FactoryMetadata<R,L,FactoryBase<L,R>> getFactoryMetadata(){
        if (metadata==null){
            metadata = FactoryMetadataManager.getMetadata(getClass());
        }
        return metadata;
    }

    @FunctionalInterface
    public interface TriAttributeVisitor {
        void accept(String attributeVariableName, Attribute<?,?> attribute1, Attribute<?,?> attribute2, Attribute<?,?> attribute3);
    }

    @FunctionalInterface
    public interface BiAttributeVisitor {
        void accept(String attributeVariableName, Attribute<?,?> attribute1, Attribute<?,?> attribute2);
    }

    private void visitAttributesFlat(AttributeVisitor consumer) {
        getFactoryMetadata().visitAttributesFlat(this,consumer);
    }

    private void visitAttributesDualFlat(FactoryBase<L,R> data, BiAttributeVisitor consumer) {
        getFactoryMetadata().visitAttributesDualFlat(this,data,consumer);
    }

    private void visitAttributesTripleFlat(FactoryBase<L,R> other1, FactoryBase<L,R> other2, TriAttributeVisitor consumer) {
        getFactoryMetadata().visitAttributesTripleFlat(this,other1,other2,consumer);
    }

    private Map<String,FactoryBase<?,R>> collectChildDataMap() {
        List<FactoryBase<?,R>> dataList = collectChildrenDeep();
        HashMap<String, FactoryBase<?,R>> result = Maps.newHashMapWithExpectedSize(dataList.size());
        for (FactoryBase<?,R> factory: dataList){
            result.put(factory.getId(),factory);
        }
        return result;
    }

    /** collect set with all nested children and itself*/
    private List<FactoryBase<?,R>> collectChildrenDeep() {
        ArrayList<FactoryBase<?,R>> dataList = new ArrayList<>();
        collectModelEntitiesTo(dataList,this.iterationRun+1);
        return dataList;
    }

    void collectModelEntitiesTo(List<FactoryBase<?,R>> allModelEntities, long dataIterationRun) {
        allModelEntities.add(this);
        visitChildFactoriesAndViewsFlat(child -> child.collectModelEntitiesTo(allModelEntities,dataIterationRun),dataIterationRun);
    }

    @SuppressWarnings("unchecked")
    private FactoryBase<L,R> newCopyInstance(FactoryBase<L,R> data) {
        return (FactoryBase<L, R>) getFactoryMetadata().newCopyInstance(data);
    }

    private Set<FactoryBase<?,?>> collectChildrenDeepFromNode() {
        HashSet<FactoryBase<?,?>> result = new HashSet<>();
        collectChildFactoriesDeepFromNode(result);
        return result;
    }

    private void collectChildFactoriesDeepFromNode(Set<FactoryBase<?,?>> collected) {
        if (collected.add(this)){
            visitChildFactoriesAndViewsFlatWithoutIterationCheck(child -> child.collectChildFactoriesDeepFromNode(collected));
        }
    }

    private List<FactoryBase<?,R>> fixDuplicateObjects() {
        Map<String, FactoryBase<?, R>> idToDataMapTyped = collectChildDataMap();
        Map<String, FactoryBase<?,?>> idToDataMap = new HashMap<>();
        for (Map.Entry<String, FactoryBase<?, R>> stringFactoryBaseEntry : idToDataMapTyped.entrySet()) {
            idToDataMap.put(stringFactoryBaseEntry.getKey(),stringFactoryBaseEntry.getValue());
        }
        final List<FactoryBase<?,R>> all = new ArrayList<>(idToDataMapTyped.values());
        for (FactoryBase<?,R> data: all){
            data.visitAttributesFlat((attributeVariableName, attribute) -> attribute.internal_fixDuplicateObjects(idToDataMap));
        }
        return all;
    }

    private Supplier<String> displayTextProvider;

    @JsonIgnore
    private String getDisplayText(){
        if (displayTextProvider==null){
//            return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN,Data.this.getClass().getSimpleName()).replace("-"," ");
            return FactoryBase.this.getClass().getSimpleName();
        }
        return displayTextProvider.get();
    }

    private void setDisplayTextProvider(Supplier<String> displayTextProvider){
        this.displayTextProvider=displayTextProvider;
    }

    /** validate attributes without visiting child factories*/
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

    private void merge(FactoryBase<L,R> originalValue, FactoryBase<L,R> newValue, MergeResult mergeResult, Function<String,Boolean> permissionChecker) {
        this.visitAttributesTripleFlat(originalValue, newValue, (attributeName, currentAttribute, originalAttribute, newAttribute) -> {
            if (!currentAttribute.internal_ignoreForMerging()){
                if (currentAttribute.internal_hasMergeConflict(originalAttribute, newAttribute)) {
                    mergeResult.addConflictInfo(new AttributeDiffInfo(FactoryBase.this.getId(), attributeName));
                } else {
                    if (currentAttribute.internal_isMergeable(originalAttribute, newAttribute)) {
                        final AttributeDiffInfo attributeDiffInfo = new AttributeDiffInfo(attributeName,FactoryBase.this.getId());
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


    @SuppressWarnings("unchecked")
    private <F extends FactoryBase<L,R>> F semanticCopy() {
        F result = (F)newCopyInstance(this);
        this.visitAttributesDualFlat(result, (attributeName, attribute1, attribute2) -> attribute1.internal_semanticCopyToUnsafe(attribute2));
        return result;
    }

    /**copy including one the references first level of nested references*/
    private <T extends FactoryBase<?,?>> T copyOneLevelDeep(){
        return copy(1);
    }

    /**copy without nested references, only value attributes are copied*/
    private <T extends FactoryBase<?,?>> T copyZeroLevelDeep(){
        return copy(0);
    }

    private <T extends FactoryBase<?,?>> T copy() {
        return copy(Integer.MAX_VALUE);
    }


    @SuppressWarnings("unchecked")
    private <T extends FactoryBase<?,?>> T copy(int level) {
        ArrayList<FactoryBase<?,R>> oldDataList = new ArrayList<>();
        FactoryBase<?,?> newRoot = copyDeep(0, level,oldDataList,null,(R)this);

        for (FactoryBase<?,?> oldData: oldDataList) {
            oldData.copy=null;//cleanup
        }

        return (T) newRoot;
    }

    @FunctionalInterface
    public interface DataCopyProvider{
        <R extends FactoryBase<?,R>,T extends FactoryBase<?,R>> T copy(T original);
    }

    FactoryBase<L,R> copy;


    @SuppressWarnings("unchecked")
    private <F extends FactoryBase<? extends L,R>> F copyDeep(final int level, final int maxLevel, final List<FactoryBase<?,R>> oldData, FactoryBase<?,R> parent, R root){
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
                if (thisAttribute instanceof RootAwareAttribute<?,?>){
                    ((RootAwareAttribute<R,?>)thisAttribute).internal_copyToUnsafe(copyAttribute,level + 1, maxLevel,oldData,this,root);
                } else {
                    thisAttribute.internal_copyToUnsafe(copyAttribute);
                }

                if (copyAttribute instanceof RootAwareAttribute<?,?>){
                    ((RootAwareAttribute<R,?>)copyAttribute).internal_addBackReferences((R) root.copy,copy);
                }
//                copyAttribute.internal_addBackReferences(root.copy,copy);
            });

            oldData.add(this);

            //add BackReferences
            if (parent!=null){
                copy.addParent(parent.copy);
            }
            copy.root=(R)root.copy;
            copy.creatorMock=this.creatorMock;

        }
        return (F)copy;
    }


    private boolean readyForUsage(){
        return root!=null;
    }


    private void endUsage() {
        for (FactoryBase<?,?> data: collectChildrenDeep()){
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
    @SuppressWarnings("unchecked")
    private void addBackReferences() {
        this.root=(R)this;
        addBackReferences(getRoot(),null,this.iterationRun+1);
    }


    private void addBackReferences(final R root, final FactoryBase<?,?> parent, long dataIterationRun){
        getFactoryMetadata().setAttributeReferenceClasses(this);
        addParent(parent);
        this.root=root;
        getFactoryMetadata().addBackReferencesToAttributes(this,root);
        this.visitChildFactoriesAndViewsFlat(data -> data.addBackReferences(getRoot(), FactoryBase.this,dataIterationRun),dataIterationRun);
    }

    private void addBackReferencesForSubtree(R root, FactoryBase<?,?> parent, HashSet<FactoryBase<?,?>> visited){
        getFactoryMetadata().setAttributeReferenceClasses(this);
        addParent(parent);
        this.root=root;
        getFactoryMetadata().addBackReferencesToAttributes(this,root);

        if (visited.add(this)) {//use HashSet instead of iteration counter to avoid iterationCounter mix up
            visitChildFactoriesAndViewsFlatWithoutIterationCheck(child -> child.addBackReferencesForSubtree(root,this,visited));
        }
    }

    private Set<FactoryBase<?,?>> parents;
    private FactoryBase<?,?> parent;

    private void addParent(FactoryBase<?,?> parent){
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
    private Set<FactoryBase<?,?>> getParents(){
        if (parents==null){
            if (parent==null){
                return Collections.emptySet();
            }
            return Set.of(parent);
        } else {
            return parents;
        }
    }

    private R root;
    private R getRoot(){
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
    private void addDisplayTextListeners(FactoryBase<?,?>  data, AttributeChangeListener attributeChangeListener){
        for (Attribute<?,?> attribute: data.displayTextDependencies){
            attribute.internal_addListener(attributeChangeListener);
        }
    }

    private Object storeDisplayTextObservable;
    private void storeDisplayTextObservable(Object simpleStringProperty) {
        storeDisplayTextObservable=simpleStringProperty;
    }


    /** data configurations api. Should be used in the default constructor
     * @return the configuration api*/
    protected DataConfiguration config(){
        return new DataConfiguration(this);
    }

    public static class DataConfiguration {
        private final FactoryBase<?,?>  data;

        public DataConfiguration(FactoryBase<?,?>  data) {
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
    public Internal<L,R> internal(){
        return new Internal<>(this);
    }

    public static class Internal<L,R extends FactoryBase<?,R>>  {
        private final FactoryBase<L,R>  factory;

        public Internal(FactoryBase<L,R>  data) {
            this.factory = data;
        }

        public boolean matchSearchText(String newValue) {
            return factory.matchSearchText(newValue);
        }

        @SuppressWarnings("unchecked")
        public void visitAttributesDualFlat(FactoryBase<?,?>  modelBase, BiAttributeVisitor consumer) {
            factory.visitAttributesDualFlat((FactoryBase<L,R>)modelBase,consumer);
        }

        public void visitAttributesFlat(AttributeVisitor consumer) {
            factory.visitAttributesFlat(consumer);
        }


        public List<AttributeGroup> attributeListGrouped(){
            return factory.attributeListGrouped();
        }

        public Map<String,FactoryBase<?,R> > collectChildDataMap() {
            factory.assertRoot();
            return factory.collectChildDataMap();
        }

        /**
         * @return all data including root and no duplicates
         * */
        public List<FactoryBase<?,R> > collectChildrenDeep() {
            factory.assertRoot();
            return factory.collectChildrenDeep();
        }


        /**fix all data with same id should be same object
         * only call on root
         * */
        public void fixDuplicateData() {
            factory.assertRoot();
            factory.fixDuplicateObjects();
        }

        /**
         * -fix all data with same id should be same object
         * -remove parents that are no not tin the tree
         * only call on root
         * */
        public void fixDuplicatesAndAddBackReferences() {
            factory.assertRoot();
            List<FactoryBase<?,R> > dataList = this.factory.fixDuplicateObjects();
            for (FactoryBase<?,?>  data : dataList) {
                data.resetBackReferencesFlat();
            }
            this.factory.addBackReferences();
        }

        public String getDisplayText(){
            return factory.getDisplayText();
        }

        //TODO cleanup the hack (goal is to remove javafx dependency )
        public void storeDisplayTextObservable(Object simpleStringProperty){
            this.factory.storeDisplayTextObservable(simpleStringProperty);
        }

        public Object getDisplayTextObservable(){
            return this.factory.storeDisplayTextObservable;
        }

        public List<ValidationError> validateFlat(){
            return factory.validateFlat();
        }

        public <F extends FactoryBase<L,R>> void  merge(F  originalValue, F  newValue, MergeResult mergeResult, Function<String,Boolean> permissionChecker) {
            factory.merge(originalValue,newValue,mergeResult,permissionChecker);
        }
        public List<FactoryBase<?,?> > getPathFromRoot() {
            return factory.getPathFromRoot();
        }

        public <T extends FactoryBase<?,?> > T copy() {
            return factory.copy();
        }

        public <T extends FactoryBase<L,R> > T copyOneLevelDeep(){
            return factory.copyOneLevelDeep();
        }

        public <T extends FactoryBase<?,?> > T copyZeroLevelDeep(){
            return factory.copyZeroLevelDeep();
        }

        public <F extends FactoryBase<L,R>> F copyDeep(final int level, final int maxLevel, final List<FactoryBase<?,R>> oldData, FactoryBase<?,R> parent, R root){
            return factory.copyDeep(level,maxLevel,oldData,parent,root);
        }

        /**
         * see: {@link FactoryBase#addBackReferences}
         * @param <T> type
         * @return usableCopy
         */
        @SuppressWarnings("unchecked")
        public <T extends FactoryBase<L,R>> T addBackReferences() {
            if (!this.factory.hasBackReferencesFlat()){
                this.factory.addBackReferences();
            }
            return (T)factory;
        }

        public void serFactoryTreeBuilderBasedAttributeSetupForRoot(FactoryTreeBuilderBasedAttributeSetup<?,R,?> setup) {
            this.factory.serFactoryTreeBuilderBasedAttributeSetupForRoot(setup);
        }


        /** only call on root*/
        public void endUsage() {
            factory.endUsage();
        }

        public boolean readyForUsage(){
            return factory.readyForUsage();
        }


        /**
         * @param root root
         * @param parent parent
         */
        public void addBackReferencesForSubtree(R root, FactoryBase<?,?> parent){
            factory.addBackReferencesForSubtree(root,parent,new HashSet<>());
        }

        public void addBackReferencesForSubtreeUnsafe(R root, FactoryBase<?,?> parent){
            factory.addBackReferencesForSubtree(root,parent,new HashSet<>());
        }

        /** use getParents instead
         * @return parent*/
        @Deprecated
        public FactoryBase<?,?>  getParent(){
            if (factory.getParents().isEmpty()){
                return null;
            }
            return factory.getParents().iterator().next();
        }

        public Set<FactoryBase<?,?> > getParents(){
            return factory.getParents();
        }

        public void addDisplayTextListeners(AttributeChangeListener attributeChangeListener){
            factory.addDisplayTextListeners(factory, attributeChangeListener);
        }

        public boolean hasBackReferencesFlat() {
            return factory.hasBackReferencesFlat();
        }

        public void resetIterationCounterFlat() {
            factory.iterationRun=0;
        }

        public void assertRoot(){
            factory.assertRoot();
        }

        /**
         * collect child from middle node, slower than FromRoot but work from all nodes
         * @return children including itself
         */
        public Set<FactoryBase<?,?> > collectChildrenDeepFromNode() {
            return factory.collectChildrenDeepFromNode();
        }

        public DataStorageMetadataDictionary createDataStorageMetadataDictionaryFromRoot(){
            assertRoot();
            return factory.createDataStorageMetadataDictionaryFromRoot();
        }

        public R getRoot() {
            return factory.getRoot();
        }

        public FactoryTreeBuilderBasedAttributeSetup getFactoryTreeBuilderBasedAttributeSetup() {
            return factory.factoryTreeBuilderBasedAttributeSetup;
        }

        /** create and prepare the liveobject
         * @return liveobject*/
        public L create(){
            return factory.create();
        }

        public FactoryLogEntryTreeItem createFactoryLogTree() {
            long iterationRun=factory.iterationRun+1;
            return factory.createFactoryLogEntryTree(iterationRun);
        }

        public FactoryLogEntry createFactoryLogEntry(){
            return factory.createFactoryLogEntryFlat();
        }

        /**
         * determine which live objects needs recreation
         * @param changedData changedData
         * */
        public void determineRecreationNeedFromRoot(Set<FactoryBase<?,?>> changedData) {
            factory.determineRecreationNeed(changedData);
        }

        public void resetLog() {
            factory.resetLog();
        }

        /** start the liveObject e.g open a port*/
        public void start() {
            factory.start();
        }

        /**
         * destroy liveobject form a removed factory
         * */
        public void destroyRemoved() {
            factory.destroyRemoved();
        }

        /**
         * destroy the old liveobject in updated factories
         * */
        public void destroyUpdated() {
            factory.destroyUpdated();
        }

        public void cleanUpAfterCrash() {
            try {
                destroyRemoved();
            } catch (Exception e) {
                logger.info("exception trying to cleanup after crash",e);
            }
            try {
                destroyUpdated();
            } catch (Exception e) {
                logger.info("exception trying to cleanup after crash",e);
            }
        }

        public L instance() {
            return factory.instance();
        }

        public  void loopDetector() {
            factory.loopDetector();
        }

        public List<FactoryBase<?,?>> collectChildFactoriesDeepFromRoot(){
            return factory.collectChildFactoriesDeep();
        }

        /**
         *        h
         *      / | \
         *     /  e  \
         *    d       g
         *   /|\      |
         *  / | \     f
         * a  b  c
         * @return breadth-first order: hdegabcf
         * */
        public List<FactoryBase<?,?>> getFactoriesInDestroyOrder(){
            return factory.getFactoriesInDestroyOrder();
        }

        /**
         *        h
         *      / | \
         *     /  e  \
         *    d       g
         *   /|\      |
         *  / | \     f
         * a  b  c
         * @return postorder: abcdefgh
         **/
        public List<FactoryBase<?,?>> getFactoriesInCreateAndStartOrder(){
            return factory.getFactoriesInCreateAndStartOrder();
        }

        public HashMap<String,FactoryBase<?,?>> collectChildFactoriesDeepMapFromRoot(){
            final List<FactoryBase<?,?>> factoryBases = collectChildFactoriesDeepFromRoot();
            HashMap<String, FactoryBase<?,?>> result = new HashMap<>();
            for (FactoryBase<?,?> factory: factoryBases){
                result.put(factory.getId(),factory);
            }
            return result;
        }

        public String debugInfo() {
            return factory.debugInfo();
        }


        public void setMicroservice(Microservice<?,R,?> microservice) {
            factory.setMicroservice(microservice);
        }

        public L getLiveObject() {
            return factory.createdLiveObject;
        }

        public String logStartDisplayTextDeep(){
            return factory.logStartDisplayTextDeep();
        }

        public String logUpdateDisplayTextDeep(){
            return factory.logUpdateDisplayTextDeep();
        }

        public String getFactoryDisplayText() {
            return factory.getFactoryDescription();
        }

        /**
         * @param treeBuilderName name used in treebuilder
         */
        public void setTreeBuilderName(String treeBuilderName){
            factory.treeBuilderName=treeBuilderName;
        }

        public String getTreeBuilderName(){
            return factory.treeBuilderName;
        }
    }

    FactoryTreeBuilderBasedAttributeSetup<?, R, ?> factoryTreeBuilderBasedAttributeSetup;
    private void serFactoryTreeBuilderBasedAttributeSetupForRoot(FactoryTreeBuilderBasedAttributeSetup<?, R, ?> factoryTreeBuilderBasedAttributeSetup) {
        this.factoryTreeBuilderBasedAttributeSetup=factoryTreeBuilderBasedAttributeSetup;
    }

    @SuppressWarnings("unchecked")
    private DataStorageMetadataDictionary createDataStorageMetadataDictionaryFromRoot() {
        HashMap<Class<FactoryBase<?,R>>,Long> dataClassesToCount = new HashMap<>();


        for (FactoryBase<?,R> data : this.collectChildrenDeep()) {
            Long counter=dataClassesToCount.get(data.getClass());
            if (counter==null){
                counter=0L;
            }
            counter++;
            dataClassesToCount.put((Class<FactoryBase<?, R>>) data.getClass(),counter);
        }

        List<DataStorageMetadata> dataStorageMetadataList= new ArrayList<>();
        ArrayList<Class<? extends FactoryBase<?,R>>> sortedClasses = new ArrayList<>(dataClassesToCount.keySet());
        sortedClasses.sort(Comparator.comparing(Class::getName));
        for (Class clazz : sortedClasses) {
            if (!Modifier.isAbstract(clazz.getModifiers())){
                dataStorageMetadataList.add(FactoryMetadataManager.getMetadata(clazz).createDataStorageMetadata(dataClassesToCount.get(clazz)));
            }
        }

        sortedClasses.sort(Comparator.comparing(Class::getName));
        return new DataStorageMetadataDictionary(dataStorageMetadataList,this.getClass().getName());
    }

    //TODO model path with multiple parents
    private List<FactoryBase<?,?>> getPathFromRoot() {
        ArrayList<FactoryBase<?,?>> result = new ArrayList<>();
        FactoryBase<?,?> current= this;
        while (!current.getParents().isEmpty()) {
            FactoryBase<?,?> parent = current.getParents().iterator().next();
            result.add(parent);
            current= parent;

        }
        Collections.reverse(result);
        return result;
    }

    private void assertRoot() {
        if (this.root!=this){
            throw new IllegalStateException("can only be called from root this.root="+this.root);
        }
    }






    private FactoryLogEntry createFactoryLogEntry(){
        FactoryLogEntry factoryLogEntry = new FactoryLogEntry(this);
        factoryLogEntry.logCreate(createDurationNs);
        factoryLogEntry.logRecreate(recreateDurationNs);
        factoryLogEntry.logStart(startDurationNs);
        factoryLogEntry.logDestroy(destroyDurationNs);
        return factoryLogEntry;
    }


    private L instance() {
        if (needRecreation){
            previousLiveObject = this.createdLiveObject;
            this.createdLiveObject = reCreate(previousLiveObject);
            needRecreation=false;
            if (needsCreatePropagation()){
                started=false;
            }

        } else {
            if (createdLiveObject==null){
                createdLiveObject = create();
            }
        }
        return createdLiveObject;
    }

    private static class MeasuredActionResult<T>{
        public final T result;
        public final long time;

        private MeasuredActionResult(T result, long time) {
            this.result = result;
            this.time = time;
        }
    }

    <U> MeasuredActionResult<U> timeMeasuringAction(Supplier<U> action){
        long start=System.nanoTime();
        U result = action.get();
        long passedTimeNs = System.nanoTime() - start;
        return new MeasuredActionResult<>(result,passedTimeNs);
    }

    private long timeMeasuringAction(Runnable action){
        return timeMeasuringAction(() -> {
            action.run();
            return null;
        }).time;
    }

    L createTemplateMethod(){
        if (creator==null){
            throw new IllegalStateException("no creator defined: "+getClass());
        }
        return creator.get();
    }

    private L create(){
        if (creatorMock!=null){
            return creatorMock.apply(this);
        }

        MeasuredActionResult<L> actionResult = timeMeasuringAction(this::createTemplateMethod);
        logCreate(actionResult.time);
        return actionResult.result;
    }

    private L reCreate(L previousLiveObject) {
        if (updater!=null){
            long time = timeMeasuringAction(() -> updater.accept(previousLiveObject) );
            logUpdate(time);
            return previousLiveObject;
        }
        if (reCreatorWithPreviousLiveObject!=null){
            MeasuredActionResult<L> actionResult = timeMeasuringAction(() -> reCreatorWithPreviousLiveObject.apply(previousLiveObject));
            logRecreate(actionResult.time);
            return actionResult.result;
        }

        MeasuredActionResult<L> actionResult = timeMeasuringAction(this::createTemplateMethod);
        logRecreate(actionResult.time);
        return actionResult.result;
    }

    private void start() {
        if (!started && starterWithNewLiveObject!=null && createdLiveObject!=null){//createdLiveObject is null e.g. if object ist not instanced in the parent factory
            logStart(timeMeasuringAction(() -> {
                starterWithNewLiveObject.accept(createdLiveObject);
                started = true;
            }));
        }
    }

    private void destroyUpdated() {
        if (previousLiveObject!=null && destroyerWithPreviousLiveObject!=null && needsCreatePropagation()){
            logDestroy(timeMeasuringAction(()-> {
                destroyerWithPreviousLiveObject.accept(previousLiveObject);
            }));
        }
        previousLiveObject=null;
    }

    private void destroyRemoved() {
        if (createdLiveObject!=null && destroyerWithPreviousLiveObject!=null){
            logDestroy(timeMeasuringAction(()-> {
                destroyerWithPreviousLiveObject.accept(createdLiveObject);
            }));
        }
        createdLiveObject=null;
    }

    private void determineRecreationNeed(Set<FactoryBase<?,?> > changedData){
        for (FactoryBase<?,?> data : changedData) {
            ((FactoryBase)data).needRecreation=true;
            if (((FactoryBase)data).needsCreatePropagation()){
                Set<FactoryBase<?,?>> parents = data.internal().getParents();
                while (!parents.isEmpty()){
                    Set<FactoryBase<?,?>> grandParents = new HashSet<>();
                    for (FactoryBase<?,?> parent : parents) {
                        ((FactoryBase) parent).needRecreation = true;
                        if (((FactoryBase)parent).needsCreatePropagation()) {
                            grandParents.addAll(parent.internal().getParents());
                        }
                    }
                    parents = grandParents;
                }
            }
        }
    }

    private boolean needsCreatePropagation() {
        return updater==null;
    }


    private void loopDetector(){
        long iterationRun=this.iterationRun+1;
        loopDetector(this,new ArrayDeque<>(),iterationRun);
    }

    private void loopDetector(FactoryBase<?,?> factory, ArrayDeque<FactoryBase<?, ?>> stack, final long iterationRun){
        if (factory.iterationRun==iterationRun){
            if (stack.contains(factory)){
                throw new IllegalStateException("Factories contains a cycle, circular dependencies are not supported cause it indicates a design flaw.");
            }
        } else {
            stack.push(factory);
            factory.visitChildFactoriesAndViewsFlat(child -> {
                loopDetector(child,stack,iterationRun);
            },iterationRun);
            stack.pop();
        }
    }

    private List<FactoryBase<?,?>> collectChildFactoriesDeep(){
        long iterationRun=this.iterationRun+1;
        final List<FactoryBase<?,?>> result = new ArrayList<>();
        collectChildFactoriesDeep(this,result,iterationRun);
        return result;
    }

    private void collectChildFactoriesDeep(FactoryBase<?,?> factory, List<FactoryBase<?, ?>> result, final long iterationRun){
        result.add(factory);
        factory.visitChildFactoriesAndViewsFlat(child -> {
            collectChildFactoriesDeep(child,result,iterationRun);
        },iterationRun);
    }

    private List<FactoryBase<?,?>> getFactoriesInDestroyOrder(){
        long iterationRun=this.iterationRun+1;
        final List<FactoryBase<?, ?>> result = new ArrayList<>();
        result.add(this);
        getFactoriesInDestroyOrder(this,result,iterationRun);
        return result;
    }

    private void getFactoriesInDestroyOrder(FactoryBase<?,?> factory, List<FactoryBase<?, ?>> result, final long iterationRun){
        int size=result.size();
        factory.visitChildFactoriesAndViewsFlat(result::add,iterationRun);
        for (int i = size; i < result.size(); i++) {//fori loop cause performance optimization
           getFactoriesInDestroyOrder(result.get(i),result,iterationRun);
        }
        //factory.visitChildFactoriesAndViewsFlat(child -> getFactoriesInDestroyOrder(child,result,iterationRun), iterationRun);
    }

    private List<FactoryBase<?,?>> getFactoriesInCreateAndStartOrder(){
        long iterationRun=this.iterationRun+1;
        final List<FactoryBase<?,?>> result = new ArrayList<>();
        getFactoriesInCreateAndStartOrder(this,result,iterationRun);
        return result;
    }

    private void getFactoriesInCreateAndStartOrder(FactoryBase<?,?> factory, List<FactoryBase<?,?>> result, final long iterationRun){
        factory.visitChildFactoriesAndViewsFlat(child -> {
            getFactoriesInCreateAndStartOrder(child,result,iterationRun);
        },iterationRun);
        result.add(factory);
    }

    long iterationRun;
    private void visitChildFactoriesAndViewsFlat(Consumer<FactoryBase<?,R>> consumer, long iterationRun) {
        if (this.iterationRun==iterationRun){
            return;
        }
        this.iterationRun=iterationRun;

        getFactoryMetadata().visitChildFactoriesAndViewsFlat(this,consumer);
    }

    private void visitChildFactoriesAndViewsFlatWithoutIterationCheck(Consumer<FactoryBase<?,R>> consumer) {
        getFactoryMetadata().visitChildFactoriesAndViewsFlat(this,consumer);
    }


    private String debugInfo(){
        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("ID:\n  ");
            stringBuilder.append(getId());
            stringBuilder.append("\nAttributes:\n");
            this.internal().visitAttributesFlat((attributeVariableName, attribute) -> {
                stringBuilder.append("  ").append(attribute.internal_getPreferredLabelText(Locale.ENGLISH)).append(": ").append(attribute.getDisplayText()).append("\n");
            });
            return stringBuilder.toString().trim();
        } catch (Exception e) {
            return "can't create debuginfo text cause:\n"+ Throwables.getStackTraceAsString(e);
        }
    }
    

    private FactoryLogEntryTreeItem createFactoryLogEntryTree(long iterationRun) {
        ArrayList<FactoryLogEntryTreeItem> children = new ArrayList<>();
        this.visitChildFactoriesAndViewsFlat(flatChild -> children.add(flatChild.createFactoryLogEntryTree(iterationRun)),iterationRun);
        return new FactoryLogEntryTreeItem(this.createFactoryLogEntry(), children);
    }

    private FactoryLogEntry createFactoryLogEntryFlat(){
        return this.createFactoryLogEntry();
    }

    Microservice<?, R, ?> microservice;
    private void setMicroservice(Microservice<?, R, ?> microservice) {
        this.microservice = microservice;
    }
    private Microservice<?, R, ?> getMicroservice() {
        return getRoot().microservice;
    }


    @JsonIgnore
    private long createDurationNs;
    private void logCreate(long createDurationNs){
        this.createDurationNs=createDurationNs;
    }


    @JsonIgnore
    private long recreateDurationNs;
    private void logRecreate(long recreateDurationNs){
        this.recreateDurationNs=recreateDurationNs;
    }

    @JsonIgnore
    private long updateDurationNs;
    private void logUpdate(long updateDurationNs){
        this.updateDurationNs=updateDurationNs;
    }

    @JsonIgnore
    private long startDurationNs;
    private void logStart(long startDurationNs){
        this.startDurationNs=startDurationNs;
    }

    @JsonIgnore
    private long destroyDurationNs;
    private void logDestroy(long destroyDurationNs){
        this.destroyDurationNs=destroyDurationNs;
    }

    private void resetLog() {
        this.createDurationNs=0;
        this.recreateDurationNs=0;
        this.startDurationNs=0;
        this.destroyDurationNs=0;
        this.updateDurationNs=0;
    }

    private static final int PRINTED_COUNTER_LIMIT=500;
    private static class PrintedCounter{
        private int printedCounter;
        public void inc(){
            printedCounter++;
        }
        public boolean limitReached(){
            return printedCounter >= PRINTED_COUNTER_LIMIT;
        }
    }
    private void logDisplayTextDeep(StringBuilder stringBuilder, long deep, String prefix, boolean isTail, PrintedCounter printedCounter, long iterationRun){
        if (printedCounter.limitReached()) {
            return;
        }
        if (deep>0){
            stringBuilder.append(prefix).append(isTail ? "└── " : "├── ");
        }
//        if (!printed.add(this)){
//            stringBuilder.append("@").append(this.getId()).append("\n");
//            return;
//        }
        printedCounter.inc();

        stringBuilder.append(getFactoryDescription());
        stringBuilder.append(": ");
        stringBuilder.append(eventsDisplayText());
        stringBuilder.append("\n");

        int counter=0;

        List<FactoryBase<?,?>> children = new ArrayList<>();
        visitChildFactoriesAndViewsFlat(children::add,iterationRun);
        for (FactoryBase<?,?> child: children){
            child.logDisplayTextDeep(stringBuilder, deep+1, prefix + (isTail ? "    " : "│   "), counter==children.size()-1,printedCounter,iterationRun);
            counter++;
        }

    }

    private String logStartDisplayTextDeep(){
        StringBuilder stringBuilder = new StringBuilder("\n");
        stringBuilder.append("Application started:\n");
        logDisplayText(stringBuilder);
        return stringBuilder.toString();
    }

    private String logUpdateDisplayTextDeep(){
        StringBuilder stringBuilder = new StringBuilder("\n");
        stringBuilder.append("Application updated:\n");
        logDisplayText(stringBuilder);
        return stringBuilder.toString();
    }

    private void logDisplayText(StringBuilder stringBuilder) {
        PrintedCounter printedCounter = new PrintedCounter();
        long iterationRun = this.iterationRun + 1;
        logDisplayTextDeep(stringBuilder, 0, "", true, printedCounter, iterationRun);
        if (printedCounter.limitReached()) {
            stringBuilder.append("... (aborted log after " + PRINTED_COUNTER_LIMIT + " factories)");
        }
    }

    @JsonIgnore
    private String getFactoryDescription(){
        return getClass().getSimpleName();
    }

    private String formatNsPeriod(long periodNs){
        return periodNs+ "ns("+periodNs/1000000+"ms)";
    }

    private String eventsDisplayText() {
        StringBuilder result = new StringBuilder();
        if (createDurationNs!=0) {
            result.append(FactoryLogEntryEventType.CREATE);
            result.append(" ");
            result.append(formatNsPeriod(createDurationNs));
            result.append(",");
        }
        if (recreateDurationNs!=0) {
            result.append(FactoryLogEntryEventType.RECREATE);
            result.append(" ");
            result.append(formatNsPeriod(recreateDurationNs));
            result.append(",");
        }
        if (startDurationNs!=0) {
            result.append(FactoryLogEntryEventType.START);
            result.append(" ");
            result.append(formatNsPeriod(startDurationNs));
            result.append(",");
        }
        if (destroyDurationNs!=0) {
            result.append(FactoryLogEntryEventType.DESTROY);
            result.append(" ");
            result.append(formatNsPeriod(destroyDurationNs));
            result.append(",");
        }
        if (updateDurationNs!=0) {
            result.append(FactoryLogEntryEventType.UPDATE);
            result.append(" ");
            result.append(formatNsPeriod(updateDurationNs));
            result.append(",");
        }
        return result.toString();
    }

    Supplier<L> creator=null;
    Function<FactoryBase<L, R>, L> creatorMock=null;
    Consumer<L> updater=null;
    Function<L,L> reCreatorWithPreviousLiveObject=null;
    Consumer<L> starterWithNewLiveObject=null;
    Consumer<L> destroyerWithPreviousLiveObject=null;
    void setCreator(Supplier<L> creator){
        this.creator=creator;
    }

    private void setReCreator(Function<L,L> reCreatorWithPreviousLiveObject ) {
        this.reCreatorWithPreviousLiveObject=reCreatorWithPreviousLiveObject;
    }

    private void setUpdater(Consumer<L> updater) {
        this.updater=updater;
    }

    private void setStarter(Consumer<L> starterWithNewLiveObject) {
        this.starterWithNewLiveObject=starterWithNewLiveObject;
    }

    private void setDestroyer(Consumer<L> destroyerWithPreviousLiveObject) {
        this.destroyerWithPreviousLiveObject=destroyerWithPreviousLiveObject;
    }

    private void mock(Function<FactoryBase<L, R>, L> creatorMock) {
        this.creatorMock = creatorMock;
    }


    /** life cycle configurations api<br>
     *<br>
     * Update Order<br>
     * 1. recreate for changed, create for new<br>
     * 2. destroy removed and updated<br>
     * 3. start new<br>
     *<br>
     * The goal is to keep the time between destroy and start as short as possible cause that's essentially the application downtime.
     * Therefore slow operation should be executed in create.<br>
     *  <br>
     * Once usable resources like ports should be claimed in start and released in destroy
     *
     *<br>
     * should be used in the default constructor
     *
     * @return configuration api
     * */
    protected LifeCycleConfig<L,R> configLifeCycle(){
        return new LifeCycleConfig<>(this);
    }

    public static class LifeCycleConfig<L,R  extends FactoryBase<?,R>> {
        private final FactoryBase<L,R> factory;

        public LifeCycleConfig(FactoryBase<L, R> factory) {
            this.factory = factory;
        }

        /**create and prepare the liveObject
         * @param creator creator*/
        public void setCreator(Supplier<L> creator){
            factory.setCreator(creator);
        }

        /**the factory data has changed therefore a new liveobject is needed.<br>
         * previousLiveObject can be used to pass runtime status from previous object (e.g request counter).<br>
         * passed previous liveobject is never null
         *
         * @param reCreatorWithPreviousLiveObject reCreatorWithPreviousLiveObject*/
        public void setReCreator(Function<L,L> reCreatorWithPreviousLiveObject ) {
            factory.setReCreator(reCreatorWithPreviousLiveObject);
        }

        /**the factory data has changed therefore bud you want to reuse the liveObject and only update it.<br>
         * (that means that parents do not have to be recreated.)
         *
         * @param updater updater*/
         public void setUpdater(Consumer<L> updater ) {
                factory.setUpdater(updater);
         }

        /** start the liveObject e.g open a port
         * @param starterWithNewLiveObject starterWithNewLiveObject*/
        public void setStarter(Consumer<L> starterWithNewLiveObject) {
            factory.setStarter(starterWithNewLiveObject);
        }

        /** finally free liveObject e.g close a port
         * @param destroyerWithPreviousLiveObject destroyerWithPreviousLiveObject*/
        public void setDestroyer(Consumer<L> destroyerWithPreviousLiveObject) {
            factory.setDestroyer(destroyerWithPreviousLiveObject);
        }
    }



    public UtilityFactory<L,R> utility(){
        return new UtilityFactory<>(this);
    }

    public static class UtilityFactory<L,R  extends FactoryBase<?,R>> {
        private final FactoryBase<L,R> factory;

        public UtilityFactory(FactoryBase<L,R> factory) {
            this.factory = factory;
        }

        public Microservice<?,R,?> getMicroservice(){
            return factory.getMicroservice();
        }

        public R getRoot(){
            return factory.getRoot();
        }

        /** semantic copy can be configured on the attributes, unlike copy which always create complete copy with same ids
         *
         * @param <F> type
         * @return self
         */
        public <F extends FactoryBase<L,R> > F semanticCopy(){
            return factory.semanticCopy();
        }

        /**
         * copy with same ids and data
         * @param <T> self
         * @return scopy
         */
        public <T extends FactoryBase<?,?> > T copy(){
            return factory.copy();
        }

        @SuppressWarnings("unchecked")
        public <F extends FactoryBase<L,R>> void mock(Function<F,L> creatorMock){
            factory.mock(factory -> creatorMock.apply((F)factory));
        }
    }



}
