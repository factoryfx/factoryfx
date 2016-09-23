package de.factoryfx.factory;

import java.util.HashMap;
import java.util.Map;
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
import de.factoryfx.data.attribute.ReferenceListAttribute;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public abstract class FactoryBase<L extends LiveObject> extends Data {

    /**copy including one the references first level of nested references*/
    public <T extends FactoryBase<L>> T copyOneLevelDeep(){
        return copyOneLevelDeep(0,new HashMap<>());
    }

    @SuppressWarnings("unchecked")
    private <T extends FactoryBase<L>> T copyOneLevelDeep(final int level, HashMap<String,FactoryBase> identityPreserver){
        if (level>1){
            return null;
        }
        T result= (T) identityPreserver.get(this.getId());
        if (result==null){
            result = (T)newInstance();
            result.setId(this.getId());
            this.visitAttributesDualFlat(result, (thisAttribute, copyAttribute) -> {
                Object value = thisAttribute.get();
                if (value instanceof FactoryBase){
                    value=((FactoryBase)value).copyOneLevelDeep(level+1,identityPreserver);
                }
                if (thisAttribute instanceof ReferenceListAttribute){
                    final ObservableList<FactoryBase> referenceList = FXCollections.observableArrayList();
                    ((ReferenceListAttribute)thisAttribute).get().forEach(factory -> referenceList.add(((FactoryBase)factory).copyOneLevelDeep(level+1,identityPreserver)));
                    value=referenceList;
                }

                copyAttribute.set(value);
            });
            identityPreserver.put(result.getId(),result);
        }
        return result;
    }

    private String id;

    public FactoryBase() {

    }

    public String getId() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        return id;
    }

    public void setId(String value) {
        id = value;
    }





    private L createdLiveObjects;
    public L instance() {
        Optional<L> previousLiveObject = Optional.ofNullable(createdLiveObjects);
        if (!changedDeep() && createdLiveObjects!=null) {//TODO is the createdLiveObjects==null correct? is is used if new Factory is transitive added (Limitation of the mergerdiff)
            return createdLiveObjects;
        } else{
            L liveObject = createImp(previousLiveObject);
            createdLiveObjects=liveObject;
            changed=false;
            return liveObject;
        }
    }

    protected abstract L createImp(Optional<L> previousLiveObject);

    public void collectLiveObjects(Map<String,LiveObject> liveObjects){

        this.visitChildFactoriesFlat(factory -> cast(factory).collectLiveObjects(liveObjects));

        if (createdLiveObjects!=null){
            liveObjects.put(getId(),createdLiveObjects);
        }
    }

    @JsonIgnore
    public Optional<L> getCreatedLiveObject(){
        return Optional.ofNullable(createdLiveObjects);
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
        this.visitChildFactoriesFlat(factoryBase -> changedDeep = changedDeep || cast(factoryBase).changedDeep());
        return changedDeep;
    }

    private LoopProtector loopProtector = new LoopProtector();
    public void loopDetector(){
        loopProtector.enter();
        try {
            this.visitChildFactoriesFlat(factory -> cast(factory).loopDetector());
        } finally {
            loopProtector.exit();
        }
    }

    //TODO this works as long als tree elements extends FactoryBase, how to enforce that?
    @SuppressWarnings("unchecked")
    private FactoryBase<?> cast(Data data){
        return (FactoryBase<?>)data;
    }

    @SuppressWarnings("unchecked")
    public Set<FactoryBase<?>> collectChildFactories(){
        return super.collectChildrenDeep().stream().map(this::cast).collect(Collectors.toSet());
    }



//    @JsonIgnore
//    public static final FactoryMetadata metadata=new FactoryMetadata();

}
