package io.github.factoryfx.factory;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Throwables;
import io.github.factoryfx.data.Data;
import io.github.factoryfx.factory.log.FactoryLogEntry;
import io.github.factoryfx.factory.log.FactoryLogEntryEventType;
import io.github.factoryfx.factory.log.FactoryLogEntryTreeItem;
import io.github.factoryfx.server.Microservice;
import org.slf4j.LoggerFactory;

/**
 * @param <L> liveobject created from this factory
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FactoryBase<L,R extends FactoryBase<?,R>> extends Data{

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FactoryBase.class);

    public FactoryBase() {

    }

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


    @SuppressWarnings("unchecked")
    private FactoryDictionary<FactoryBase<?, R>> getFactoryDictionary(){
        return (FactoryDictionary<FactoryBase<?, R>>)FactoryDictionary.getFactoryDictionary(getClass());
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

    private void determineRecreationNeed(Set<Data> changedData){
        for (Data data : changedData) {
            ((FactoryBase)data).needRecreation=true;
            if (((FactoryBase)data).needsCreatePropagation()){
                Set<Data> parents = data.internal().getParents();
                while (!parents.isEmpty()){
                    Set<Data> grandParents = new HashSet<>();
                    for (Data parent : parents) {
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
    private void visitChildFactoriesAndViewsFlat(Consumer<FactoryBase<?,?>> consumer, long iterationRun) {
        if (this.iterationRun==iterationRun){
            return;
        }
        this.iterationRun=iterationRun;

        getFactoryDictionary().visitChildFactoriesAndViewsFlat(this,consumer);
    }

    private void visitChildFactoriesAndViewsFlatWithoutIterationCheck(Consumer<FactoryBase<?,?>> consumer) {
        getFactoryDictionary().visitChildFactoriesAndViewsFlat(this,consumer);
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

    @SuppressWarnings("unchecked")
    private R getRoot(){
        return (R)this.internal().getRoot();
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


    /** <b>internal methods should be only used from the framework.</b>
     *  They may change in the Future.
     *  There is no fitting visibility in java therefore this workaround.
     * @return internal factory api
     */
    public FactoryInternal<L,R> internalFactory(){
        return new FactoryInternal<>(this);
    }

    public static class FactoryInternal<L,R  extends FactoryBase<?,R>> {
        private final FactoryBase<L,R> factory;

        public FactoryInternal(FactoryBase<L, R> factory) {
            this.factory = factory;
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
        public void determineRecreationNeedFromRoot(Set<Data> changedData) {
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

    Supplier<L> creator=null;
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



    public UtilityFactory<L,R> utilityFactory(){
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
    }




}