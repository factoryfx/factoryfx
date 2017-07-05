package de.factoryfx.factory;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.base.Throwables;
import de.factoryfx.data.AttributeAndName;
import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.factory.atrribute.*;
import de.factoryfx.factory.log.FactoryLogEntry;
import de.factoryfx.factory.log.FactoryLogEntryEvent;
import de.factoryfx.factory.log.FactoryLogEntryEventType;

/**
 * @param <L> liveobject created from this factory
 * @param <V> runtime visitor
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use=JsonTypeInfo.Id.MINIMAL_CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public class FactoryBase<L,V> extends Data implements Iterable<FactoryBase<?, V>>{

    public FactoryBase() {

    }

    @JsonIgnore
    private L createdLiveObject;
    @JsonIgnore
    private boolean started=false;
    @JsonIgnore
    boolean needRecreation =false;
    @JsonIgnore
    private FactoryLogEntry factoryLogEntry=new FactoryLogEntry(this);
    @JsonIgnore
    private L previousLiveObject;

    private void resetLog() {
        factoryLogEntry=new FactoryLogEntry(this);
    }

    private L instance() {
        if (needRecreation){
            previousLiveObject = this.createdLiveObject;
            this.createdLiveObject = reCreate(previousLiveObject);
            needRecreation=false;
            started=false;
        } else {
            if (createdLiveObject==null){
                createdLiveObject = create();
            }
        }
        return createdLiveObject;
    }

    <U> U loggedAction(FactoryLogEntryEventType type, Supplier<U> action){
        long start=System.nanoTime();
        U result = action.get();
        factoryLogEntry.events.add(new FactoryLogEntryEvent(type,System.nanoTime()-start));
        return result;
    }

    private void loggedAction(FactoryLogEntryEventType type, Runnable action){
        loggedAction(type, (Supplier<Void>) () -> {
            action.run();
            return null;
        });
    }

    L create(){
        if (creator==null){
            throw new IllegalStateException("no creator defined: "+getClass());
        }
        return loggedAction(FactoryLogEntryEventType.CREATE, ()-> creator.get());
    }

    private L reCreate(L previousLiveObject) {
        if (reCreatorWithPreviousLiveObject!=null){
        return loggedAction(FactoryLogEntryEventType.RECREATE, ()-> {
                return reCreatorWithPreviousLiveObject.apply(previousLiveObject);
            });
        }
        return create();
    }

    private void start() {
        if (!started && starterWithNewLiveObject!=null && createdLiveObject!=null){//createdLiveObject is null e.g. if object ist not instanced in the parent factory
            loggedAction(FactoryLogEntryEventType.START, ()-> {
                starterWithNewLiveObject.accept(createdLiveObject);
                started=true;
            });
        }
    }

    private void destroy(Set<FactoryBase<?,V>> previousFactories) {
        if (!previousFactories.contains(this) && destroyerWithPreviousLiveObject!=null){
            loggedAction(FactoryLogEntryEventType.DESTROY, ()-> {
                destroyerWithPreviousLiveObject.accept(createdLiveObject);
            });
        }
        if (previousLiveObject!=null && destroyerWithPreviousLiveObject!=null){
            loggedAction(FactoryLogEntryEventType.DESTROY, ()-> {
                destroyerWithPreviousLiveObject.accept(previousLiveObject);
            });
        }
        previousLiveObject=null;
    }

    private void determineRecreationNeed(Set<Data> changedData, ArrayDeque<FactoryBase<?,?>> path){
        path.push(this);

        needRecreation =changedData.contains(this) || createdLiveObject==null;  //null means newly added
        if (needRecreation){
            for (FactoryBase factoryBase: path){
                factoryBase.needRecreation =true;
            }
        }

        visitChildFactoriesAndViewsFlat(child -> {
             child.determineRecreationNeed(changedData,path);
        });
        path.pop();
    }

    private void loopDetector(){
        collectChildFactoriesDeep();
    }

    private Set<FactoryBase<?,V>> collectChildFactoriesDeep(){
        final HashSet<FactoryBase<?, V>> result = new HashSet<>();
        collectChildFactoriesDeep(this,result,new HashSet<>());
        return result;
    }

    private void collectChildFactoriesDeep(FactoryBase<?,V> factory, Set<FactoryBase<?, V>> result, Set<FactoryBase<?, V>> stack){
        if (result.add(factory)){
            stack.add(factory);
            factory.visitChildFactoriesAndViewsFlat(child -> collectChildFactoriesDeep(child,result,stack));
            stack.remove(factory);
        } else {
            if (stack.contains(factory)){
                throw new IllegalStateException("Factories contains a cycle, circular dependencies are not supported cause it indicates a design flaw.");
            }
        }
    }

    private List<FactoryBase<?,V>> collectChildrenFactoriesFlat() {
        List<FactoryBase<?,V>> result = new ArrayList<>();
        this.visited=false;
        this.visitChildFactoriesAndViewsFlat(result::add);
        return result;
    }

    @Override
    public Iterator<FactoryBase<?, V>> iterator() {
        return collectChildrenFactoriesFlat().iterator();
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


    private void runtimeQuery(V visitor) {
        if (executorWidthVisitorAndCurrentLiveObject!=null){
            executorWidthVisitorAndCurrentLiveObject.accept(visitor,createdLiveObject);
        }
    }

    private boolean visited;
    //to avoid visit factories multiple times through views
    private void prepareIterationRun(){
        visited=false;
    }

    @SuppressWarnings("unchecked")
    private void visitChildFactoriesAndViewsFlat(Consumer<FactoryBase<?,V>> consumer) {
        if (visited){
            return;
        }
        visited=true;
        for (AttributeAndName attributeAndName: this.internal().getAttributes()){
            Attribute<?,?> attribute=attributeAndName.attribute;
            if (attribute instanceof FactoryReferenceAttribute) {
                FactoryBase<?, V> factory = (FactoryBase<?, V>)attribute.get();
                if (factory!=null){
                    consumer.accept(factory);
                }
            }
            if (attribute instanceof FactoryReferenceListAttribute) {
                List<?> factories = ((FactoryReferenceListAttribute<?, ?>) attribute).get();
                for (Object factory: factories){
                    consumer.accept((FactoryBase<?, V>)factory);
                }
            }
            if (attribute instanceof FactoryViewReferenceAttribute) {
                FactoryBase<?, V> factory = (FactoryBase<?, V>)attribute.get();
                if (factory!=null){
                    consumer.accept(factory);
                }
            }
            if (attribute instanceof FactoryViewListReferenceAttribute) {
                List<?> factories = ((FactoryViewListReferenceAttribute<?, ?, ?>) attribute).get();
                for (Object factory: factories){
                    consumer.accept((FactoryBase<?, V>)factory);
                }
            }
            if (attribute instanceof FactoryPolymorphicReferenceAttribute) {
                FactoryBase<?, V> factory = (FactoryBase<?, V>)attribute.get();
                if (factory!=null){
                    consumer.accept(factory);
                }
            }

        }
    }

    private void prepareIterationRunFromRoot(){
        internal().collectChildrenDeep().forEach(f -> {
            if (f instanceof FactoryBase){
                ((FactoryBase)f).prepareIterationRun();
            }
        });//intentional collectChildrenDeep (and not not collectFactoryChildrenDeep ) cause it's fastest iteration over all real data entities
    }

    final FactoryInternal<L,V> factoryInternal = new FactoryInternal<>(this);
    /** <b>internal methods should be only used from the framework.</b>
     *  They may change in the Future.
     *  There is no fitting visibility in java therefore this workaround.
     */
    public FactoryInternal<L,V> internalFactory(){
        return factoryInternal;
    }

    public static class FactoryInternal<L,V> {
        private final FactoryBase<L,V> factory;

        public FactoryInternal(FactoryBase<L, V> factory) {
            this.factory = factory;
        }

        /** create and prepare the liveobject*/
        public L create(){
            return factory.create();
        }

        public FactoryLogEntry createFactoryLogEntry() {
            return factory.createFactoryLogEntry(false);
        }

        public FactoryLogEntry createFactoryLogEntryFlat(){
            return factory.createFactoryLogEntry(true);
        }

        /**determine which live objects needs recreation*/
        public void determineRecreationNeedFromRoot(Set<Data> changedData) {
            factory.prepareIterationRunFromRoot();
            factory.determineRecreationNeed(changedData,new ArrayDeque<>());
        }

        public void resetLog() {
            factory.resetLog();
        }

        /** start the liveObject e.g open a port*/
        public void start() {
            factory.start();
        }

        /** start the liveObject e.g open a port*/
        public void destroy(Set<FactoryBase<?,V>> previousFactories) {
            factory.destroy(previousFactories);
        }

        /** execute visitor to get runtime information from the liveobject*/
        public void runtimeQuery(V visitor) {
            factory.runtimeQuery(visitor);
        }

        public L instance() {
           return factory.instance();
        }

        public  void loopDetector() {
            factory.loopDetector();
        }

        public Set<FactoryBase<?,V>> collectChildFactoriesDeepFromRoot(){
            factory.prepareIterationRunFromRoot();
            return factory.collectChildFactoriesDeep();
        }

        public HashMap<String,FactoryBase<?,V>> collectChildFactoriesDeepMapFromRoot(){
            final Set<FactoryBase<?, V>> factoryBases = collectChildFactoriesDeepFromRoot();
            HashMap<String, FactoryBase<?, V>> result = new HashMap<>();
            for (FactoryBase<?, V> factory: factoryBases){
                result.put(factory.getId(),factory);
            }
            return result;
        }

        public List<FactoryBase<?,V>> collectChildrenFactoriesFlat() {
            return factory.collectChildrenFactoriesFlat();
        }

        public String debugInfo() {
            return factory.debugInfo();
        }


    }

    private FactoryLogEntry createFactoryLogEntry(boolean flat) {
        if (factoryLogEntry.hasEvents()){
            if (!flat){
                this.internalFactory().collectChildrenFactoriesFlat().forEach(child -> {
                    factoryLogEntry.children.add(child.createFactoryLogEntry(flat));
                });
                factoryLogEntry.children.removeIf(Objects::isNull);
            }
            return factoryLogEntry;
        }
        return null;
    }

    Supplier<L> creator=null;
    Function<L,L> reCreatorWithPreviousLiveObject=null;
    Consumer<L> starterWithNewLiveObject=null;
    Consumer<L> destroyerWithPreviousLiveObject=null;
    BiConsumer<V,L> executorWidthVisitorAndCurrentLiveObject=null;
    private void setCreator(Supplier<L> creator){
        this.creator=creator;
    }

    private void setReCreator(Function<L,L> reCreatorWithPreviousLiveObject ) {
        this.reCreatorWithPreviousLiveObject=reCreatorWithPreviousLiveObject;
    }

    private void setStarter(Consumer<L> starterWithNewLiveObject) {
        this.starterWithNewLiveObject=starterWithNewLiveObject;
    }

    private void setDestroyer(Consumer<L> destroyerWithPreviousLiveObject) {
        this.destroyerWithPreviousLiveObject=destroyerWithPreviousLiveObject;
    }

    private void setRuntimeQueryExecutor(BiConsumer<V,L> executorWidthVisitorAndCurrentLiveObject) {
        this.executorWidthVisitorAndCurrentLiveObject=executorWidthVisitorAndCurrentLiveObject;
    }

    final LiveCycleConfig<L,V> liveCycleConfig = new LiveCycleConfig<>(this);

    /** live cycle configurations api<br>
     *<br>
     * Update Order<br>
     * 1. recreate for changed, create for new<br>
     * 2. destroy removed, updated<br>
     * 3. start new<br>
     *<br>
     * The goal is to keep the time between destroy and start as short as possible cause that's essentially the application downtime.
     * Therefore slow operation should be executed in create.<br>
     *<br>
     * should be used in the default constructor
     * */
    protected LiveCycleConfig<L,V> configLiveCycle(){
        return liveCycleConfig;
    }

    public static class LiveCycleConfig<L,V> {
        private final FactoryBase<L,V> factory;

        public LiveCycleConfig(FactoryBase<L, V> factory) {
            this.factory = factory;
        }

        /**create and prepare the liveObject*/
        public void setCreator(Supplier<L> creator){
            factory.setCreator(creator);
        }

        /**the factory data has changed therefore a new liveobject is needed.<br>
         * previousLiveObject can be used to reuse resources like connection pools etc.<br>
         * passed old liveobject is never null
         * */
        public void setReCreator(Function<L,L> reCreatorWithPreviousLiveObject ) {
            factory.setReCreator(reCreatorWithPreviousLiveObject);
        }

        /** start the liveObject e.g open a port*/
        public void setStarter(Consumer<L> starterWithNewLiveObject) {
            factory.setStarter(starterWithNewLiveObject);
        }

        /** finally free liveObject e.g close a port*/
        public void setDestroyer(Consumer<L> destroyerWithPreviousLiveObject) {
            factory.setDestroyer(destroyerWithPreviousLiveObject);
        }

        /**execute visitor to get runtime information from the liveObjects*/
        public void setRuntimeQueryExecutor(BiConsumer<V,L> executorWidthVisitorAndCurrentLiveObject) {
            factory.setRuntimeQueryExecutor(executorWidthVisitorAndCurrentLiveObject);
        }
    }
}
