package de.factoryfx.factory;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import de.factoryfx.data.Data;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use=JsonTypeInfo.Id.MINIMAL_CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
/**
 *  L liveobject created from this factory
 *  V runtime visitor
 */
public abstract class FactoryBase<L,V> extends Data {

//    public abstract LiveCycleController<L,V> createLifecycleController();
//    LiveCycleController<L, V> lifecycleController;
//    private LiveCycleController<L,V> getLifecycleController(){
//        if (lifecycleController==null){
//            lifecycleController = createLifecycleController();
//        }
//        return lifecycleController;
//    }

    private String id;

    public FactoryBase() {

    }

    @Override
    public String getId() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        return id;
    }

    public void setId(String value) {
        id = value;
    }
    public void setId(Object value) {
        id = (String)value;
    }



    private L createdLiveObject;
    private L instance() {
        if (!changedDeep() && createdLiveObject !=null) {//TODO is the createdLiveObject==null correct? is is used if new Factory is transitive added (Limitation of the mergerdiff)
            return createdLiveObject;
        } else{
            if (createdLiveObject==null){
                try {
                    createdLiveObject = create();
                } catch (NullPointerException npe){
                    throw new RuntimeException(this.debugInfo(), npe);
                }
            } else {
                L previousLiveObject = this.createdLiveObject;
                this.createdLiveObject = reCreate(previousLiveObject);
                destroy(previousLiveObject);//TODO check if this is the wrong destroy order (parent are destroyed before the children)
            }
            changed=false;
            return createdLiveObject;
        }
    }

//    protected abstract L createImp(Optional<L> previousLiveObject, LifecycleNotifier<V> lifecycle);

//    public void collectLiveObjects(Map<String,LiveObject> liveObjects){
//
//        this.visitChildFactoriesFlat(factory -> cast(factory).collectLiveObjects(liveObjects));
//
//        if (createdLiveObject!=null){
//            liveObjects.put(getId(),createdLiveObject);
//        }
//    }

    /**intented for test only*/
    @JsonIgnore
    protected Optional<L> getCreatedLiveObject(){
        return Optional.ofNullable(createdLiveObject);
    }

    @JsonIgnore
    private boolean changed=true;

    /**set from Merger used to determine which live Objects needs update*/
    private void markChanged() {
        changed=true;
    }

    private void unMarkChanged() {
        changed=false;
    }

    @JsonIgnore
    private boolean changedDeep;
    boolean changedDeep(){
        if (changed){
            return true;
        }
        changedDeep=false;
        this.internal().visitChildFactoriesFlat(factoryBase -> changedDeep = changedDeep || cast(factoryBase).map(FactoryBase::changedDeep).orElse(false));
        return changedDeep;
    }

    private LoopProtector loopProtector = new LoopProtector();
    private void loopDetector(){
        loopProtector.enter();
        try {
            this.internal().visitChildFactoriesFlat(factory -> cast(factory).ifPresent(FactoryBase::loopDetector));
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

    @SuppressWarnings("unchecked")
    private Set<FactoryBase<?,V>> collectChildFactoriesDeep(){
        return super.internal().collectChildrenDeep().stream().map(this::cast).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toSet());
    }



    private String debugInfo(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Attributes:\n");
        this.internal().visitAttributesFlat((attributeVariableName, attribute) -> {
            stringBuilder.append(attributeVariableName+": "+attribute.get()+"\n");
        });
        return stringBuilder.toString();
    }


    L create(){
        if (creator!=null){
            return creator.get();
        }
        throw new IllegalStateException("no creator defined");
    }

    private L reCreate(L previousLiveObject) {
        if (reCreatorWithPreviousLiveObject!=null){
            return reCreatorWithPreviousLiveObject.apply(previousLiveObject);
        }
        return create();
    }

    private void start() {
        if (starterWithNewLiveObject!=null && createdLiveObject!=null){//createdLiveObject is null e.g. if object ist not instanced in the parent factory
            starterWithNewLiveObject.accept(createdLiveObject);
        }
    }

    private void destroy(L previousLiveObject) {
        if (destroyerWithPreviousLiveObject!=null  && previousLiveObject!=null){
            destroyerWithPreviousLiveObject.accept(previousLiveObject);
        }
    };

    private void runtimeQuery(V visitor) {
        if (executorWidthVisitorAndCurrentLiveObject!=null){
            executorWidthVisitorAndCurrentLiveObject.accept(visitor,createdLiveObject);
        }
    };

    FactoryInternal<L,V> factoryInternal = new FactoryInternal<>(this);
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

        /** create and prepare the liveObject*/
        public L create(){
            return factory.create();
        }

        /** start the liveObject e.g open a port*/
        public void start() {
            factory.start();
        }

        /** start the liveObject e.g open a port*/
        public void destroy() {
            factory.destroy(factory.createdLiveObject);
        }

        /** execute visitor to get runtime informations from the liveobject*/
        public void runtimeQuery(V visitor) {
            factory.runtimeQuery(visitor);
        };

        public L instance() {
           return factory.instance();
        }

        public void markChanged() {
            factory.markChanged();
        }

        public void unMarkChanged() {
            factory.unMarkChanged();
        }

        public  void loopDetector() {
            factory.loopDetector();
        }

        public Set<FactoryBase<?,V>> collectChildFactoriesDeep(){
            return factory.collectChildFactoriesDeep();
        }

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

    LiveCycleConfig<L,V> liveCycleConfig = new LiveCycleConfig<>(this);
    /** live cycle configurations api */
    public LiveCycleConfig<L,V> configLiveCycle(){
        return liveCycleConfig;
    }

    public static class LiveCycleConfig<L,V> {
        private final FactoryBase<L,V> factory;

        public LiveCycleConfig(FactoryBase<L, V> factory) {
            this.factory = factory;
        }

        /** create and prepare the liveObject*/
        public void setCreator(Supplier<L> creator){
            factory.setCreator(creator);
        }

        /**the factory data has changed therefore a new liveobject is needed.
         * previousLiveObject can be used to reuse resources like conenction pools*/
        public void setReCreator(Function<L,L> reCreatorWithPreviousLiveObject ) {
            factory.setReCreator(reCreatorWithPreviousLiveObject);
        }

        /** start the liveObject e.g open a port*/
        public void setStarter(Consumer<L> starterWithNewLiveObject) {
            factory.setStarter(starterWithNewLiveObject);
        }

        /** free liveObject e.g close a port*/
        public void setDestroyer(Consumer<L> destroyerWithPreviousLiveObject) {
            factory.setDestroyer(destroyerWithPreviousLiveObject);
        }

        /** execute visitor to get runtime informations from the liveobject*/
        public void setRuntimeQueryExecutor(BiConsumer<V,L> executorWidthVisitorAndCurrentLiveObject) {
            factory.setRuntimeQueryExecutor(executorWidthVisitorAndCurrentLiveObject);
        }
    }
}
