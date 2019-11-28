package io.github.factoryfx.factory;

import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import io.github.factoryfx.factory.attribute.*;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.metadata.AttributeMetadata;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;

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

/**
 *
 * @param <L> liveobject created from this factory
 * @param <R> root factory
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", resolver = DataObjectIdResolver.class)
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")// minimal class don't work always
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FactoryBase<L,R extends FactoryBase<?,R>> {

    @JsonProperty
    UUID id;

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
    @JsonProperty
    private boolean treeBuilderClassUsed;


    public FactoryBase() {

    }


    public UUID getId() {
        if (id == null) {
            id = new UUID(r.nextLong(), r.nextLong());
            //TODO does this increase collision probability?
            // Secure random in UUID.randomUUID().toString() is too slow and it doesn't matter if the random is predictable
        }
        return id;
    }

    public void setId(UUID id) {
        this.id=id;
    }


    /**
     * compares ids
     * @param factory factory
     * @return true if id are equals
     * */
    public boolean idEquals(FactoryBase<?,?> factory){
        return getId().equals(factory.getId());
    }
    private FactoryMetadata<R, FactoryBase<?, R>> metadata;
    @SuppressWarnings("unchecked")
    private FactoryMetadata<R,FactoryBase<?,R>> getFactoryMetadata(){
        if (metadata==null){
            metadata = FactoryMetadataManager.getMetadata(getClass());
        }
        return metadata;
    }

    @FunctionalInterface
    public interface TriAttributeVisitor<V> {
        void accept(String attributeVariableName, AttributeMerger<V> attribute1, AttributeMerger<V> attribute2, AttributeMerger<V> attribute3);
    }

    @FunctionalInterface
    public interface BiCopyAttributeVisitor<V> {
        /**
         * @param attribute1 attribute from factory1
         * @param attribute2 attribute from factory2
         * @return true: continue visit, false abort
         */
        boolean accept(AttributeCopy<V> attribute1, AttributeCopy<V> attribute2);
    }

    @FunctionalInterface
    public interface AttributeMatchVisitor<V> {
        /**
         *
         * @param attributeVariableName attributeVariableName
         * @param attribute1 attribute from factory1
         * @param attribute2 attribute from factory2
         * @return true: continue visit, false abort
         */
        boolean accept(String attributeVariableName, AttributeMatch<V> attribute1, AttributeMatch<V> attribute2);
    }

    private void visitAttributesFlat(AttributeVisitor consumer) {
        getFactoryMetadata().visitAttributesFlat(this, consumer);
    }

    private void visitAttributesMetadataFlat(AttributeMetadataVisitor consumer) {
        getFactoryMetadata().visitAttributeMetadata(consumer);
    }


    private void visitFactoryEnclosingAttributesFlat(FactoryEnclosingAttributeVisitor consumer) {
        getFactoryMetadata().visitFactoryEnclosingAttributesFlat(this,consumer);
    }

    private <V> void visitAttributesForCopy(FactoryBase<?,R> data, BiCopyAttributeVisitor<V> consumer) {
        getFactoryMetadata().visitAttributesForCopy(this,data,consumer);
    }

    private <V> void visitAttributesForMatch(FactoryBase<?,R> data, AttributeMatchVisitor<V> consumer) {
        getFactoryMetadata().visitAttributesForMatch(this,data,consumer);
    }

    private <V> void visitAttributesTripleFlat(FactoryBase<?,R> other1, FactoryBase<?,R> other2, TriAttributeVisitor<V> consumer) {
        getFactoryMetadata().visitAttributesTripleFlat(this,other1,other2,consumer);
    }

    private Map<UUID,FactoryBase<?,R>> collectChildDataMap() {
        this.ensureTreeIsFinalised();
        Map<UUID, FactoryBase<?,R>> result;
        if (treeChildrenCounter >0) {
            result = Maps.newLinkedHashMapWithExpectedSize(treeChildrenCounter);//Maps.newLinkedHashMapWithExpectedSize(treeChildrenCounter);
        } else {
            result = new LinkedHashMap<>();// LinkedHashMap surprisingly is faster than HashMap in this case;
        }
        for (FactoryBase<?, R> child : collectChildrenDeep()) {
            result.put(child.getId(),child);
        }
        return result;
    }

    List<FactoryBase<?,R>> collectedTo;
    private List<FactoryBase<?,R>> collectChildrenDeep(){
        this.ensureTreeIsFinalised();
        ArrayList<FactoryBase<?, R>> result = new ArrayList<>();
        ArrayDeque<FactoryBase<?,R>> stack = new ArrayDeque<>();
        stack.push(this);
        while (!stack.isEmpty()) {
            FactoryBase<?,R> factory = stack.pop();

            result.add(factory);
            factory.collectedTo=result;

            for (FactoryBase<?, R> child : factory.finalisedChildrenFlat) {
                if (child.collectedTo!=result) {
                    stack.push(child);
                }
            }
        }
        for (FactoryBase<?, R> child : result) {
            child.collectedTo=null;
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private <R extends FactoryBase<?,R>> Set<FactoryBase<?,R>> collectionChildrenDeepFromNonFinalizedTree(){
        HashSet<FactoryBase<?, R>> result = new HashSet<>();
        ArrayDeque<FactoryBase<?,R>> stack = new ArrayDeque<>();
        stack.push((FactoryBase<?,R>)this);
        while (!stack.isEmpty()) {
            FactoryBase<?,R> factory = stack.pop();
            if (result.add(factory)){
                factory.collectionChildrenDeepFromNonFinalizedTreeGenericWorkaround(e -> stack.push((FactoryBase<?,R>)e));
            }
        }
        return result;
    }
    private void collectionChildrenDeepFromNonFinalizedTreeGenericWorkaround(Consumer<FactoryBase<?,?>> consumer){
        getFactoryMetadata().visitChildFactoriesAndViewsFlat(this, consumer, false);
    }

    private FactoryBase<?,R> newCopyInstance(FactoryBase<L,R> data) {
        return getFactoryMetadata().newCopyInstance(data);
    }

    private void fixDuplicateObjects() {
        boolean needIdFixing=false;

        List<FactoryBase<?, R>> childrenDeep = collectChildrenDeep();
        Map<UUID, FactoryBase<?, R>> idToDataMap = Maps.newHashMapWithExpectedSize(childrenDeep.size());
        for (FactoryBase<?, R> child : childrenDeep) {
            if (idToDataMap.put(child.id,child)!=null){
                needIdFixing=true;
                break;
            }
        }

        if (needIdFixing){
            for (FactoryBase<?, R> child : childrenDeep) {
                FactoryBase<?, R> factory = idToDataMap.get(child.getId());
                if (factory!=null) {
                    if (child.createdLiveObject!=null){///prefer instantiate factories
                        idToDataMap.put(child.getId(),child);
                    }
                } else {
                    idToDataMap.put(child.getId(),child);
                }
            }

            for (FactoryBase<?,R> factory: idToDataMap.values()){
                factory.visitFactoryEnclosingAttributesFlat((attributeVariableName, attribute) -> attribute.internal_fixDuplicateObjects(idToDataMap));
            }
            this.needReFinalisation();
        }
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

        visitAttributesFlat((attributeMetadata, attribute) -> {
            final ArrayList<ValidationError> validationErrors = new ArrayList<>();
            result.put(attribute, validationErrors);
            validationErrors.addAll(attribute.internal_validate (this,attributeMetadata.attributeVariableName));
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

    private void merge(FactoryBase<?,R> originalValue, FactoryBase<?,R> newValue, MergeResult<R> mergeResult, Function<String,Boolean> permissionChecker) {
        this.visitAttributesTripleFlat(originalValue, newValue, (attributeName, currentMerger, originalMerger, newMerger) -> {
            //for performance to execute compare only once
            boolean newMergerMatchOriginalMerger= originalMerger.internal_mergeMatch(newMerger);
            boolean currentMergerMatchOriginalMerger= originalMerger.internal_mergeMatch(currentMerger);
            boolean currentMergerMatchNewMerger= currentMerger.internal_mergeMatch(newMerger);


            if (attributeHasMergeConflict(newMergerMatchOriginalMerger,currentMergerMatchOriginalMerger, currentMergerMatchNewMerger)) {
                mergeResult.addConflictInfo(new AttributeDiffInfo(attributeName,FactoryBase.this.getId()));
            } else {
                if (attributeIsMergeable(currentMergerMatchOriginalMerger,currentMergerMatchNewMerger)) {
                    final AttributeDiffInfo attributeDiffInfo = new AttributeDiffInfo(attributeName,FactoryBase.this.getId());
                    if (currentMerger.internal_hasWritePermission(permissionChecker)){
                        mergeResult.addMergeInfo(attributeDiffInfo);
                        mergeResult.addMergeExecutions(() -> currentMerger.internal_merge(newMerger.get()),this);
                    } else {
                        mergeResult.addPermissionViolationInfo(attributeDiffInfo);
                    }
                }
            }
        });
    }

    /**
     *
     * @param newMergerMatchOriginalMerger newMergerMatchOriginalMerger
     * @param currentMergerMatchOriginalMerger currentMergerMatchOriginalMerger
     * @param currentMergerMatchNewMerger currentMergerMatchNewMerger
     * @return true if merge conflict
     */
    private boolean attributeHasMergeConflict(boolean newMergerMatchOriginalMerger, boolean currentMergerMatchOriginalMerger, boolean currentMergerMatchNewMerger) {
        return !newMergerMatchOriginalMerger && !currentMergerMatchOriginalMerger && !currentMergerMatchNewMerger;
    }

    /**
     * check if merge should be executed e.g. not if values ar equals
     * @param currentMergerMatchOriginalMerger currentMergerMatchOriginalMerger
     * @param currentMergerMatchNewMerger currentMergerMatchNewMerger
     * @return true if merge should be executed
     * */
    private boolean attributeIsMergeable(boolean currentMergerMatchOriginalMerger, boolean currentMergerMatchNewMerger) {
        if (!currentMergerMatchOriginalMerger || currentMergerMatchNewMerger) {
            return false ;
        }
        return true;
    }


    @SuppressWarnings("unchecked")
    private <F extends FactoryBase<L,R>> F semanticCopy() {
        F result = (F)newCopyInstance(this);
        this.visitAttributesForCopy(result, (original, copy) -> {
            original.internal_semanticCopyTo(copy);
            return true;
        });
        return result;
    }

    private <T extends FactoryBase<?,?>> T copyOneLevelDeep(){
        return copy(1);
    }

    private <T extends FactoryBase<?,?>> T copyZeroLevelDeep(){
        return copy(0);
    }

    private <T extends FactoryBase<?,?>> T copy() {
        return copy(Integer.MAX_VALUE);
    }


    //TODO check synchronized performance impact (synchronized is required cause copy stored in factory)
    @SuppressWarnings("unchecked")
    private synchronized <T extends FactoryBase<?,?>> T copy(int level) {
        ArrayList<FactoryBase<?,?>> oldDataList;
        if (treeChildrenCounter >0){
            oldDataList = new ArrayList<>(treeChildrenCounter);
        } else {
            oldDataList = new ArrayList<>();
        }

        FactoryBase<?,?> copy = copyDeep(0, level,oldDataList,null,(R)this);
        copy.treeChildrenCounter =oldDataList.size();

        for (FactoryBase<?,?> oldData: oldDataList) {
//            oldData.copy.finalizeChildren();
            oldData.copy=null;//cleanup
        }
        copy.needReFinalisation =true;
        return (T) copy;
    }


    FactoryBase<?,R> copy;
    @SuppressWarnings("unchecked")
    private <F extends FactoryBase<? extends L,R>> F copyDeep(final int level, final int maxLevel, final List<FactoryBase<?,?>> oldData, FactoryBase<?,R> parent, R root){
        if (level>maxLevel){
            return null;
        }
        if (copy==null){
            copy = newCopyInstance(this);
            if (this.id==null){
                getId();
            }
            copy.id = this.id;

            this.visitAttributesForCopy(copy, (thisAttribute, copyAttribute) -> {
                thisAttribute.internal_copyTo(copyAttribute,level + 1, maxLevel,oldData,this,root);
                copyAttribute.internal_addBackReferences(root.copy,copy);
                return true;
            });

            oldData.add(this);

            copy.root=(R)root.copy;
            copy.creatorMock=this.creatorMock;
            copy.treeBuilderName=this.treeBuilderName;
            copy.treeBuilderClassUsed=this.treeBuilderClassUsed;
        }
        return (F)copy;
    }

    private void endEditingDeepFromRoot() {
        for (FactoryBase<?,?> data: collectChildrenDeep()){
            data.visitAttributesFlat((attributeVariableName, attribute) -> attribute.internal_endUsage());
        }
    }

    private void endEditingFlat() {
        visitAttributesFlat((attributeVariableName, attribute) -> attribute.internal_endUsage());
    }


    private void ensureTreeIsFinalised() {
        if (this.root!=null){
            ((FactoryBase<?, R>)this.root).finalise();
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
    int treeChildrenCounter;
    long backReferencesIterationRun=0;
    @SuppressWarnings("unchecked")
    private void finalise(){
        R root = (R)this;

        if (!needReFinalisation){
            return;
        }

        ArrayDeque<FactoryBase<?,R>> stack = new ArrayDeque<>();
        stack.push(root);
        long backReferencesIterationRun = root.backReferencesIterationRun+1;
        while (!stack.isEmpty()) {
            FactoryBase<?,R> factory = stack.pop();
            factory.backReferencesIterationRun=backReferencesIterationRun;

            factory.root=root;
            factory.root.treeChildrenCounter++;
            factory.getFactoryMetadata().addBackReferencesToAttributesUnsafe(factory,root);

            factory.finalizeChildren();
            for (FactoryBase<?, R> child : factory.finalisedChildrenFlat) {
                if (child.backReferencesIterationRun!=backReferencesIterationRun) {
                    child.resetParents();
                    stack.push(child);
                }
                child.addParent(factory);
            }
        }
        needReFinalisation =false;
    }

    boolean needReFinalisation =true;
    private void needReFinalisation(){
        this.root.needReFinalisation =true;
    }

    long rootDeepIterationRun=0;
    private void setRootDeep(R root) {
        ArrayDeque<FactoryBase<?,R>> stack = new ArrayDeque<>();
        stack.push(this);
        long rootDeepIterationRun = root.rootDeepIterationRun+1;
        while (!stack.isEmpty()) {
            FactoryBase<?,R> factory = stack.pop();
            factory.rootDeepIterationRun=rootDeepIterationRun;
            factory.root=root;
            factory.getFactoryMetadata().addBackReferencesToAttributesUnsafe(factory,root);

            factory.finalizeChildren();
            for (FactoryBase<?, R> child : factory.finalisedChildrenFlat) {
                if (child.rootDeepIterationRun!=rootDeepIterationRun) {
                    stack.push(child);
                }
            }
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
            } else {
                parents.add(parent);
            }
        }
    }

    private void resetParents(){
        this.parents=null;
        this.parent=null;
    }

    @JsonIgnore
    private Set<FactoryBase<?,?>> getParents(){
        this.ensureTreeIsFinalised();
        if (parents==null){
            if (parent==null){
                return Collections.emptySet();
            }
            return Set.of(parent);
        } else {
            return parents;
        }
    }

    R root;
    private R getRoot(){
        return root;
    }

    private Function<Function<Attribute<?,?>,AttributeAndMetadata>,List<AttributeGroup>> attributeListGroupedSupplier;

    /**
     * editing hint to group attributes in groups. (usually tabs)
     * @param attributeListGroupedSupplier function
     */
    private void setAttributeListGroupedSupplier(Function<Function<Attribute<?,?>,AttributeAndMetadata>,List<AttributeGroup>> attributeListGroupedSupplier){
        this.attributeListGroupedSupplier=attributeListGroupedSupplier;
    }
    private List<AttributeGroup> attributeListGrouped(){
        if (attributeListGroupedSupplier==null){
            return Collections.singletonList(new AttributeGroup("Data", attributeList()));
        }

        Map<Attribute<?,?>,AttributeMetadata> attributeToMetadata = new HashMap<>();
        this.visitAttributesFlat((attributeMetadata, attribute) -> attributeToMetadata.put(attribute,attributeMetadata));
        return attributeListGroupedSupplier.apply(attribute -> new AttributeAndMetadata(attribute, attributeToMetadata.get(attribute)));
    }

    private List<AttributeAndMetadata> attributeList(){
        ArrayList<AttributeAndMetadata> result = new ArrayList<>();
        this.visitAttributesFlat((attributeMetadata, attribute) -> result.add(new AttributeAndMetadata(attribute,attributeMetadata)));
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

    private List<Attribute<?,?>> displayTextDependencies;
    private void setDisplayTextDependencies(List<Attribute<?,?>> displayTextDependencies) {
        this.displayTextDependencies = displayTextDependencies;
    }


    @SuppressWarnings("unchecked")
    private void addDisplayTextListeners(AttributeChangeListener attributeChangeListener){
        if (displayTextDependencies!=null){
            for (Attribute<?,?> attribute: this.displayTextDependencies){
                attribute.internal_addListener(attributeChangeListener);
            }
        }
    }

    private Object storeDisplayTextObservable;
    private void storeDisplayTextObservable(Object simpleStringProperty) {
        storeDisplayTextObservable=simpleStringProperty;
    }

    @JsonIgnore
    private boolean isCreatedWithBuilderTemplate() {
        return this.treeBuilderClassUsed || this.treeBuilderName!=null;
    }

    private AttributeMetadata getAttributeMetadata(Attribute<?,?> attribute){
        return getFactoryMetadata().getAttributeMetadata(this,attribute);
    }

    /** data configurations api. Should be used in the default constructor
     * @return the configuration api*/
    protected DataConfiguration config(){
        return new DataConfiguration(this);
    }

    public static class DataConfiguration {
        private final FactoryBase<?,?>  factory;

        public DataConfiguration(FactoryBase<?,?>  factory) {
            this.factory = factory;
        }
        /**
         * short readable text describing the factory
         * @param displayTextProvider displayTextProvider
         */
        public void setDisplayTextProvider(Supplier<String> displayTextProvider){
            factory.setDisplayTextProvider(displayTextProvider);
        }

        /**
         * short readable text describing the factory
         * @param displayTextProvider custom displayText function
         * @param dependencies attributes which affect the display text
         */
        public void setDisplayTextProvider(Supplier<String> displayTextProvider, Attribute<?,?>... dependencies){
            factory.setDisplayTextProvider(displayTextProvider);
            factory.setDisplayTextDependencies(Arrays.asList(dependencies));
        }

        /**
         * @see  #setDisplayTextDependencies(Attribute[])
         * @param attributes the attributes affecting the display text
         */
        public void setDisplayTextDependencies(List<Attribute<?,?>> attributes){
            factory.setDisplayTextDependencies(attributes);
        }

        /** set the attributes that affect the display text<br>
         *  used for live update in gui
         *
         * @param attributes the attributes affecting the display text
         * */
        public void setDisplayTextDependencies(Attribute<?,?>... attributes){
            factory.setDisplayTextDependencies(Arrays.asList(attributes));
        }

        /**
         *  grouped iteration over attributes e.g. used in gui editor where each group is a new Tab
         *
         * @param attributeListGroupedSupplier function with parameter containing all attributes
         * */
        public void setAttributeListGroupedSupplier(Function<Function<Attribute<?,?>,AttributeAndMetadata>,List<AttributeGroup>> attributeListGroupedSupplier){
            this.factory.setAttributeListGroupedSupplier(attributeListGroupedSupplier);
        }


        /**
         *  define match logic for full-text search e.g. in tables
         *
         * @param matchSearchTextFunction matchSearchTextFunction
         * */
        public void setMatchSearchTextFunction(Function<String,Boolean> matchSearchTextFunction){
            factory.setMatchSearchTextFunction(matchSearchTextFunction);
        }


        /**
         * factory validation
         * @param validation validation function
         * @param dependencies attributes which affect the validation
         * @param <T> this
         */
        public <T> void addValidation(Validation<T> validation, Attribute<?,?>... dependencies){
            factory.addValidation(validation,dependencies);
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

        public Internal(FactoryBase<L,R>  factory) {
            this.factory = factory;
        }

        public boolean matchSearchText(String newValue) {
            return factory.matchSearchText(newValue);
        }

        public <V> void visitAttributesForMatch(FactoryBase<?,R>  modelBase, AttributeMatchVisitor<V> consumer) {
            factory.visitAttributesForMatch(modelBase,consumer);
        }


        public void visitAttributesFlat(AttributeVisitor consumer) {
            factory.visitAttributesFlat(consumer);
        }

        public <F extends FactoryBase<?,?>> void visitAttributesMetadata(AttributeMetadataVisitor consumer) {
            factory.visitAttributesMetadataFlat(consumer);
        }

        public List<AttributeGroup> attributeListGrouped(){
            return factory.attributeListGrouped();
        }

        public Map<UUID,FactoryBase<?,R> > collectChildFactoryMap() {
            factory.assertRoot();
            return factory.collectChildDataMap();
        }

        /**
         * @return all data including root and no duplicates
         * */
        public List<FactoryBase<?,R> > collectChildrenDeep() {
            return factory.collectChildrenDeep();
        }


        /**
         * can be used inside a view
         * @param <R> root factory
         * @return all data including root and no duplicates
         */
        public <R extends FactoryBase<?,R>> Set<FactoryBase<?,R>> collectionChildrenDeepFromNonFinalizedTree() {
            return factory.collectionChildrenDeepFromNonFinalizedTree();
        }

        /**
         * -fix all factories with same id should be same object
         * */
        public void fixDuplicateFactories() {
            factory.assertRoot();
            factory.fixDuplicateObjects();
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

        public <F extends FactoryBase<?,R>> void  merge(F  originalValue, F  newValue, MergeResult<R> mergeResult, Function<String,Boolean> permissionChecker) {
            factory.merge(originalValue,newValue,mergeResult,permissionChecker);
        }
        public List<FactoryBase<?,?> > getPathFromRoot() {
            return factory.getPathFromRoot();
        }

        public <T extends FactoryBase<?,?> > T copy() {
            return factory.copy();
        }

        @SuppressWarnings("unchecked")
        public <F extends FactoryBase<L,R>> F copyDeep(final int level, final int maxLevel, final List<FactoryBase<?,?>> oldData, FactoryBase<?,?> parent, FactoryBase<?,?> root){
            return factory.copyDeep(level,maxLevel,oldData,(FactoryBase<?,R>)parent,(R)root);
        }

        /**
         * see: {@link FactoryBase#finalise}
         * @param <T> type
         * @return usableCopy
         */
        @SuppressWarnings("unchecked")
        public <T extends FactoryBase<L,R>> T finalise() {
            factory.finalise();
            return (T)factory;
        }

        public void serFactoryTreeBuilderBasedAttributeSetupForRoot(FactoryTreeBuilderBasedAttributeSetup<R> setup) {
            this.factory.serFactoryTreeBuilderBasedAttributeSetupForRoot(setup);
        }


        /** only call on root*/
        public void endEditingDeepFromRoot() {
            factory.endEditingDeepFromRoot();
        }

        /** end edit for this factory*/
        public void endEditingFlat() {
            factory.endEditingFlat();
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
            factory.addDisplayTextListeners(attributeChangeListener);
        }

        public void assertRoot(){
            factory.assertRoot();
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

        /**
         * determine which live objects needs recreation
         * @param changedFactories changed factories
         * */
        public void determineRecreationNeedFromRoot(Set<FactoryBase<?,R>> changedFactories) {
            factory.determineRecreationNeed(changedFactories);
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
        public List<FactoryBase<?,R>> getFactoriesInDestroyOrder(){
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
        public List<FactoryBase<?,R>> getFactoriesInCreateAndStartOrder(){
            return factory.getFactoriesInCreateAndStartOrder();
        }

        public String debugInfo() {
            return factory.debugInfo();
        }


        public void setMicroservice(Microservice<?,R> microservice) {
            factory.setMicroservice(microservice);
        }

        public void setFactoryTreeBuilder(FactoryTreeBuilder<?,R> factoryTreeBuilder) {
            factory.setFactoryTreeBuilder(factoryTreeBuilder);
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

        public void setRootDeep(R root) {
            factory.setRootDeep(root);
        }

        @SuppressWarnings("unchecked")
        public void setRootDeepUnchecked(FactoryBase<?,?> root) {
            factory.setRootDeep((R)root);
        }

        public void needRecalculationForBackReferences() {
            factory.needReFinalisation();
        }

        public AttributeAndMetadata getAttribute(String attributeVariableName){
            return factory.getAttribute(attributeVariableName);
        }

        public void fixDuplicateFactoriesFlat(HashMap<UUID, FactoryBase<?, R>> idToFactoryMap){
            factory.visitFactoryEnclosingAttributesFlat((attributeVariableName, attribute) -> attribute.internal_fixDuplicateObjects(idToFactoryMap));
        }

        public boolean isCreatedWithBuilderTemplate() {
            return factory.isCreatedWithBuilderTemplate();
        }

        public void setTreeBuilderClassUsed(boolean used) {
            factory.treeBuilderClassUsed=used;
        }

        public boolean isTreeBuilderClassUsed() {
            return factory.treeBuilderClassUsed;
        }

        public List<AttributeAndMetadata> attributeList(){
            return factory.attributeList();
        }

        public AttributeMetadata getAttributeMetadata(Attribute<?,?> attribute){
            return factory.getAttributeMetadata(attribute);
        }

    }



    FactoryTreeBuilderBasedAttributeSetup<R> factoryTreeBuilderBasedAttributeSetup;
    private void serFactoryTreeBuilderBasedAttributeSetupForRoot(FactoryTreeBuilderBasedAttributeSetup<R> factoryTreeBuilderBasedAttributeSetup) {
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

    L createTemplateMethod(){
        if (creator==null){
            throw new IllegalStateException("no creator defined: "+getClass());
        }
        return creator.get();
    }

    @SuppressWarnings("unchecked")
    private L create(){
        if (creatorMock!=null){
            return (L) creatorMock.apply(this);
        }
        logCreate();
        return createTemplateMethod();
    }

    private L reCreate(L previousLiveObject) {
        if (updater!=null){
            updater.accept(previousLiveObject);
            logUpdate();
            return previousLiveObject;
        }
        if (reCreatorWithPreviousLiveObject!=null){
            logRecreate();
            return reCreatorWithPreviousLiveObject.apply(previousLiveObject);
        }

        logRecreate();
        return createTemplateMethod();
    }

    private void start() {
        if (!started) {
            logStart();
            if (starterWithNewLiveObject != null && createdLiveObject != null) {//createdLiveObject is null e.g. if object ist not instanced in the parent factory
                starterWithNewLiveObject.accept(createdLiveObject);
            }
            started = true;
        }
    }

    private void destroyUpdated() {
        if (previousLiveObject!=null && destroyerWithPreviousLiveObject!=null && needsCreatePropagation()){
            destroyerWithPreviousLiveObject.accept(previousLiveObject);
            logDestroy();
        }
        previousLiveObject=null;
    }

    private void destroyRemoved() {
        if (createdLiveObject!=null && destroyerWithPreviousLiveObject!=null){
            destroyerWithPreviousLiveObject.accept(createdLiveObject);
            logDestroy();
        }
        createdLiveObject=null;
    }

    private void determineRecreationNeed(Set<FactoryBase<?,R> > changedFactories){
        for (FactoryBase<?,?> factory : changedFactories) {
            factory.needRecreation=true;
            if (factory.needsCreatePropagation()){
                Set<FactoryBase<?,?>> parents = factory.internal().getParents();
                while (!parents.isEmpty()){
                    Set<FactoryBase<?,?>> grandParents = new HashSet<>();
                    for (FactoryBase<?,?> parent : parents) {
                        parent.needRecreation = true;
                        if (parent.needsCreatePropagation()) {
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


    private long loopDetectorIterationRun;//this is used like a visited boolean flag, long is used to avoid the need for a reset
    private void loopDetector(){
        this.ensureTreeIsFinalised();
        long iterationRun=this.loopDetectorIterationRun+1;
        try {
            loopDetector(iterationRun);
        } finally {
            this.loopDetectorIterationRun=iterationRun;
        }
    }

    private void loopDetector(final long iterationRun){
        if (iterationRun==loopDetectorIterationRun){
            throw new IllegalStateException("Factories contains a cycle, circular dependencies are not supported cause it indicates a design flaw.");
        }
        this.loopDetectorIterationRun=iterationRun;
        for (FactoryBase<?, R> child : this.finalisedChildrenFlat) {
            child.loopDetector(iterationRun);
        }
        this.loopDetectorIterationRun=-1;
    }

    private List<FactoryBase<?,R>> addedToGetFactoriesInDestroyOrder;
    private List<FactoryBase<?,R>> getFactoriesInDestroyOrder(){
        this.ensureTreeIsFinalised();
        final List<FactoryBase<?,R>> result = treeChildrenCounter >0 ? new ArrayList<>(treeChildrenCounter):new ArrayList<>();
        result.add(this);
        getFactoriesInDestroyOrder(this,result);
        for (FactoryBase<?, R> child : result) {
            child.addedToGetFactoriesInDestroyOrder=null;
        }
        return result;
    }

    private void getFactoriesInDestroyOrder(FactoryBase<?,R> factory, List<FactoryBase<?, R>> result){
        int size=result.size();
        for (FactoryBase<?, R> child : factory.finalisedChildrenFlat) {
            if (child.addedToGetFactoriesInDestroyOrder!=result){
                result.add(child);
                child.addedToGetFactoriesInDestroyOrder=result;
            }
        }

        for (int i = size; i < result.size(); i++) {//fori loop cause performance optimization
            getFactoriesInDestroyOrder(result.get(i),result);
        }
    }

    private List<FactoryBase<?,R>> addedToGetFactoriesInCreateAndStartOrder;
    private List<FactoryBase<?,R>> getFactoriesInCreateAndStartOrder(){
        this.ensureTreeIsFinalised();
        final List<FactoryBase<?,R>> result = treeChildrenCounter >0 ? new ArrayList<>(treeChildrenCounter):new ArrayList<>();
        getFactoriesInCreateAndStartOrder(this,result);
        for (FactoryBase<?, R> child : result) {
            child.addedToGetFactoriesInCreateAndStartOrder=null;
        }
        return result;
    }

    private void getFactoriesInCreateAndStartOrder(FactoryBase<?,R> factory, List<FactoryBase<?,R>> result){
        for (FactoryBase<?, R> child : factory.finalisedChildrenFlat) {
            if (child.addedToGetFactoriesInCreateAndStartOrder!=result) {
                child.addedToGetFactoriesInCreateAndStartOrder=result;
                getFactoriesInCreateAndStartOrder(child, result);
            }
        }
        result.add(factory);
    }






    @JsonIgnore()
    List<FactoryBase<?,R>> finalisedChildrenFlat;
    List<FactoryBase<?,R>> addedTo;
    @SuppressWarnings("unchecked")
    void finalizeChildren() {
        finalisedChildrenFlat = new ArrayList<>();
        getFactoryMetadata().visitChildFactoriesAndViewsFlat(this, childUntyped->{
            FactoryBase<?,R> child = (FactoryBase<?,R>)childUntyped;
            if (child!=null){
                if (child.addedTo!= finalisedChildrenFlat){
                    finalisedChildrenFlat.add(child);
                    child.addedTo= finalisedChildrenFlat;
                }
            }
        }, true);

        for (FactoryBase<?, R> child : finalisedChildrenFlat) {
            child.addedTo=null;
        }
    }


    private String debugInfo(){
        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Factory:\n  ");
            stringBuilder.append(getClass());
            stringBuilder.append("\n");
            stringBuilder.append("ID:\n  ");
            stringBuilder.append(getId());
            stringBuilder.append("\nAttributes:\n");
            this.internal().visitAttributesFlat((attributeVariableName, attribute) -> {
                stringBuilder.append("  ").append(attributeVariableName).append(": ").append(attribute.getDisplayText()).append("\n");
            });
            return stringBuilder.toString().trim();
        } catch (Exception e) {
            return "can't create debug info text cause:\n"+ Throwables.getStackTraceAsString(e);
        }
    }

    Microservice<?, R> microservice;
    private void setMicroservice(Microservice<?, R> microservice) {
        this.microservice = microservice;
    }
    private Microservice<?, R> getMicroservice() {
        return getRoot().microservice;
    }

    FactoryTreeBuilder<?, R> factoryTreeBuilder;
    private void setFactoryTreeBuilder(FactoryTreeBuilder<?, R> factoryTreeBuilder) {
        this.factoryTreeBuilder =  factoryTreeBuilder;
    }

    private FactoryTreeBuilder<?, R> getFactoryTreeBuilder() {
        return getRoot().factoryTreeBuilder;
    }

    @JsonIgnore
    private boolean createLog;
    private void logCreate(){
        this.createLog=true;
    }


    @JsonIgnore
    private boolean recreateLog;
    private void logRecreate(){
        this.recreateLog=true;
    }

    @JsonIgnore
    private boolean updateLog;
    private void logUpdate(){
        this.updateLog=true;
    }

    @JsonIgnore
    private boolean startLog;
    private void logStart(){
        this.startLog=true;
    }

    @JsonIgnore
    private boolean destroyLog;
    private void logDestroy(){
        this.destroyLog=true;
    }

    private Long logId;

    private void resetLog() {
        this.createLog=false;
        this.recreateLog=false;
        this.startLog=false;
        this.destroyLog=false;
        this.updateLog=false;
        this.logId=null;
    }

    private static final long PRINTED_COUNTER_LIMIT=500;
    private static class PrintedCounter{
        private long printedCounter;
        public void inc(){
            printedCounter++;
        }
        public boolean limitReached(){
            return printedCounter >= PRINTED_COUNTER_LIMIT;
        }
    }

    private void logDisplayText(StringBuilder stringBuilder) {
        this.ensureTreeIsFinalised();
        PrintedCounter printedCounter = new PrintedCounter();

        stringBuilder.append("CREATE:+ REUSE:= RECREATE:<> START:^ DESTROY:- UPDATE:~\n");
        logDisplayTextDeep(stringBuilder, 0, "", true, printedCounter);
        if (printedCounter.limitReached()) {
            stringBuilder.append("... (aborted log after " + PRINTED_COUNTER_LIMIT + " factories)");
        }
    }


    private void logDisplayTextDeep(StringBuilder stringBuilder, long deep, String prefix, boolean isTail, PrintedCounter printedCounter){
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

        if (this.logId==null) {
            this.logId=printedCounter.printedCounter;
        }

        stringBuilder.append(getFactoryDescription());
        stringBuilder.append(" lifecycle: ");
        stringBuilder.append(eventsDisplayText());
        stringBuilder.append("\n");

        int counter=0;


        for (FactoryBase<?,?> child: finalisedChildrenFlat){
            child.logDisplayTextDeep(stringBuilder, deep+1, prefix + (isTail ? "    " : "│   "), counter==finalisedChildrenFlat.size()-1,printedCounter);
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
    @JsonIgnore
    private String getFactoryDescription(){
        String displayText="";
        if (displayTextProvider!=null){
            displayText=getDisplayText()+", ";
        }
        return getClass().getSimpleName()+ "("+displayText+"logId:"+logId+")";
    }

    private String eventsDisplayText() {
        StringJoiner sj = new StringJoiner(",");
        if (createLog) { sj.add("+"); }
        if (!recreateLog && !updateLog && !startLog) { sj.add("="); }
        if (recreateLog) { sj.add("<>"); }
        if (startLog) { sj.add("^"); }
        if (destroyLog) { sj.add("-"); }
        if (updateLog) { sj.add("~"); }
        return sj.toString();
    }

    Supplier<L> creator=null;
    Function<FactoryBase<?, R>, ?> creatorMock=null;
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

    private void mock(Function<FactoryBase<?, R>, L> creatorMock) {
        this.creatorMock = creatorMock;
    }


    private AttributeAndMetadata getAttribute(String attributeVariableNameParam){
        AttributeAndMetadata[] result = new AttributeAndMetadata[1];
        visitAttributesFlat((attributeMetadata, attribute) -> {
            if (attributeMetadata.attributeVariableName.equals(attributeVariableNameParam)){
                result[0]=new AttributeAndMetadata(attribute,attributeMetadata);
            }
        });
        return result[0];
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
        public void setUpdater(Consumer<L> updater) {
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

        public Microservice<?,R> getMicroservice(){
            return factory.getMicroservice();
        }

        public FactoryTreeBuilder<?,R> getFactoryTreeBuilder(){
            return factory.getFactoryTreeBuilder();
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

        /**
         * overrides the factory creator with a mock<br>
         *
         * to access the factory attributes you have to specify the factory class like this<br>
         *
         * <pre>{@code
         *      exampleFactoryA.utility().<Factory>mock(f->f.reference ...
         * }</pre>
         *
         * @param creatorMock mock function, factory as parameter
         * @param <F> Factory
         */
        @SuppressWarnings("unchecked")
        public <F extends FactoryBase<L,?>> void mock(Function<F,L> creatorMock){
            factory.mock(factory -> creatorMock.apply((F)factory));
        }

        /**
         * copy including one the references first level of nested references
         * @param <F>  Factory
         * @return copy
         */
        public <F extends FactoryBase<L,R> > F copyOneLevelDeep(){
            return factory.copyOneLevelDeep();
        }

        /**
         * copy without nested references, only value attributes are copied
         * @param <F>  Factory
         * @return copy
         */
        public <F extends FactoryBase<L,R> > F copyZeroLevelDeep(){
            return factory.copyZeroLevelDeep();
        }
    }



}
