package de.factoryfx.factory;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
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

    public abstract LiveCycleController<L,V> createLifecycleController();
    LiveCycleController<L, V> lifecycleController;
    private LiveCycleController<L,V> getLifecycleController(){
        if (lifecycleController==null){
            lifecycleController = createLifecycleController();
        }
        return lifecycleController;
    }

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
    public L instance() {
        if (!changedDeep() && createdLiveObject !=null) {//TODO is the createdLiveObject==null correct? is is used if new Factory is transitive added (Limitation of the mergerdiff)
            return createdLiveObject;
        } else{
            LiveCycleController<L, V> lifecycleController = getLifecycleController();
            if (lifecycleController==null){
                throw new IllegalStateException("lifecycleController is null for:"+getClass());
            }

            if (createdLiveObject==null){
                createdLiveObject = lifecycleController.create();
            } else {
                L previousLiveObject = this.createdLiveObject;
                this.createdLiveObject = lifecycleController.reCreate(previousLiveObject);
                lifecycleController.destroy(previousLiveObject);//TODO check if this is the wrong destroy order (parent are destroyed before the children)
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

    @JsonIgnore
    //intented for test only
    protected Optional<L> getCreatedLiveObject(){
        return Optional.ofNullable(createdLiveObject);
    }

    @JsonIgnore
    private boolean changed=true;

    /**set from Merger used to determine which live Objects needs update*/
    public void markChanged() {
        changed=true;
    }

    public void unMarkChanged() {
        changed=false;
    }

    @JsonIgnore
    private boolean changedDeep;
    public boolean changedDeep(){
        if (changed){
            return true;
        }
        changedDeep=false;
        this.internal().visitChildFactoriesFlat(factoryBase -> changedDeep = changedDeep || cast(factoryBase).map(FactoryBase::changedDeep).orElse(false));
        return changedDeep;
    }

    private LoopProtector loopProtector = new LoopProtector();
    public void loopDetector(){
        loopProtector.enter();
        try {
            this.internal().visitChildFactoriesFlat(factory -> cast(factory).ifPresent(FactoryBase::loopDetector));
        } finally {
            loopProtector.exit();
        }
    }

    //TODO this works as long als tree elements extends FactoryBase, how to enforce that?
    @SuppressWarnings("unchecked")
    private Optional<FactoryBase<?,V>> cast(Data data){
        if (data instanceof FactoryBase)
            return Optional.of((FactoryBase<?,V>)data);
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public Set<FactoryBase<?,V>> collectChildFactoriesDeep(){
        return super.internal().collectChildrenDeep().stream().map(this::cast).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toSet());
    }

    public void start(){
        getLifecycleController().start(createdLiveObject);
    }

    public void destroy(){
        getLifecycleController().destroy(createdLiveObject);
    }

    public void runtimeQuery(V visitor){
        getLifecycleController().runtimeQuery(visitor,createdLiveObject);
    }

}
