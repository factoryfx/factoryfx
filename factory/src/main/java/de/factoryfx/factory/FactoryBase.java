package de.factoryfx.factory;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.base.Throwables;
import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.ViewListReferenceAttribute;
import de.factoryfx.data.attribute.ViewReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;
import de.factoryfx.factory.atrribute.FactoryViewListReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryViewReferenceAttribute;
import de.factoryfx.factory.log.FactoryLogEntry;
import de.factoryfx.factory.log.FactoryLogEntryEvent;
import de.factoryfx.factory.log.FactoryLogEntryEventType;

/**
 * @param <L> liveobject created from this factory
 * @param <V> runtime visitor
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use=JsonTypeInfo.Id.MINIMAL_CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public abstract class FactoryBase<L,V> extends Data {

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
            } else {
                //TODO make this configurable
//                loggedAction(FactoryLogEntryEventType.REUSE,()->{});
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
            action.run();;
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

        visitChildFactoriesAndViewsFlat(child -> child.determineRecreationNeed(changedData,path));
        path.pop();
    }

    private final LoopProtector loopProtector = new LoopProtector();
    private void loopDetector(){
        loopProtector.enter();
        try {
            this.internalFactory().visitChildFactoriesAndViewsFlat(FactoryBase::loopDetector);
        } finally {
            loopProtector.exit();
        }
    }

    @SuppressWarnings("unchecked")
    private Optional<FactoryBase<?,V>> cast(Data data){
        if (data instanceof FactoryBase)
            return Optional.of((FactoryBase<?,V>)data);
        return Optional.empty();
    }

    private Set<FactoryBase<?,V>> collectChildFactoriesDeep(){
        final HashSet<FactoryBase<?, V>> result = new HashSet<>();
        collectChildFactoriesDeep(this,result);
        return result;
    }

    private void collectChildFactoriesDeep(FactoryBase<?,V> factory, Set<FactoryBase<?, V>> result){
        if (result.add(factory)){
            factory.visitChildFactoriesAndViewsFlat(child -> collectChildFactoriesDeep(child,result));
        }
    }

    private Set<FactoryBase<?,V>> collectChildrenFactoriesFlat() {
        HashSet<FactoryBase<?,V>> result = new HashSet<>();
        this.visitChildFactoriesAndViewsFlat(result::add);
        return result;
    }




    private String debugInfo(){
        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("ID:\n  ");
            stringBuilder.append(getId());
            stringBuilder.append("\nAttributes:\n");
            this.internal().visitAttributesFlat(attribute -> {
                stringBuilder.append("  ").append(attribute.metadata.labelText.getPreferred(Locale.ENGLISH)).append(": ").append(attribute.getDisplayText()).append("\n");
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

    private void visitChildFactoriesAndViewsFlat(Consumer<FactoryBase<?,V>> consumer) {
        this.internal().visitAttributesFlat(attribute -> {
            if (attribute instanceof FactoryReferenceAttribute) {
                ((FactoryReferenceAttribute<?,?>)attribute).getOptional().ifPresent(data -> cast(data).ifPresent(consumer));
            }
            if (attribute instanceof FactoryReferenceListAttribute) {
                ((FactoryReferenceListAttribute<?,?>)attribute).forEach(data -> cast(data).ifPresent(consumer));
            }
            if (attribute instanceof FactoryViewReferenceAttribute) {
                ((ViewReferenceAttribute<?,?>)attribute).getOptional().ifPresent(data -> cast(data).ifPresent(consumer));
            }
            if (attribute instanceof FactoryViewListReferenceAttribute) {
                ((ViewListReferenceAttribute<?,?>)attribute).get().forEach(data -> cast(data).ifPresent(consumer));
            }
        });
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
        public void determineRecreationNeed(Set<Data> changedData) {
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

        public void visitChildFactoriesAndViewsFlat(Consumer<FactoryBase<?,V>> consumer) {
            factory.visitChildFactoriesAndViewsFlat(consumer);
        }

        public L instance() {
           return factory.instance();
        }

        public  void loopDetector() {
            factory.loopDetector();
        }

        public Set<FactoryBase<?,V>> collectChildFactoriesDeep(){
            return factory.collectChildFactoriesDeep();
        }

        public HashMap<String,FactoryBase<?,V>> collectChildFactoriesDeepMap(){
            final Set<FactoryBase<?, V>> factoryBases = factory.collectChildFactoriesDeep();
            HashMap<String, FactoryBase<?, V>> result = new HashMap<>();
            for (FactoryBase<?, V> factory: factoryBases){
                result.put(factory.getId(),factory);
            }
            return result;
        }

        public Set<FactoryBase<?,V>> collectChildrenFactoriesFlat() {
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

    /** live cycle configurations api
     *
     * Update Order
     * 1. recreate for changed, create for new
     * 2. destroy removed, updated
     * 3. start new
     *
     * The goal is to keep the time between destroy and start as short as possible cause that's essentially the application downtime.
     * Therefore slow operation should be executed in create.
     * */
    public LiveCycleConfig<L,V> configLiveCycle(){
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
