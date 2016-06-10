package de.factoryfx.factory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import de.factoryfx.factory.attribute.Attribute;
import de.factoryfx.factory.attribute.ReferenceAttribute;
import de.factoryfx.factory.attribute.ReferenceListAttribute;
import de.factoryfx.factory.jackson.ObjectMapperBuilder;
import de.factoryfx.factory.merge.MergeResult;
import de.factoryfx.factory.merge.MergeResultEntry;
import de.factoryfx.factory.merge.attribute.AttributeMergeHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class FactoryBase<E extends LiveObject, T extends FactoryBase<E,T>> {

    @FunctionalInterface
    interface TriConsumer<A, B, C> {
        void accept(A a, B b, C c);
    }

    public void visitChildFactoriesFlat(Consumer<FactoryBase<?,?>> consumer) {
        visitAttributesFlat((attributeVariableName, attribute) -> {
            attribute.visit(new Attribute.AttributeVisitor() {
                @Override
                public void value(Attribute<?> value) {

                }

                @Override
                public void reference(ReferenceAttribute<?> reference) {
                    reference.getOptional().ifPresent((factoryBase)->consumer.accept(factoryBase));
                }

                @Override
                public void referenceList(ReferenceListAttribute<?> referenceList) {
                    referenceList.forEach(new Consumer<FactoryBase<?, ?>>() {
                        @Override
                        public void accept(FactoryBase<?, ?> factoryBase) {
                            consumer.accept(factoryBase);
                        }
                    });
                }
            });
        });
    }

    public <A> void  visitAttributesDualFlat(T modelBase, BiConsumer<Attribute<A>, Attribute<A>> consumer) {
        Field[] fields = getFields();
        for (Field field : fields) {
            try {
                Object fieldValue = field.get(this);
                if (fieldValue instanceof Attribute) {
                    consumer.accept((Attribute<A>) field.get(this), (Attribute<A>) field.get(modelBase));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FunctionalInterface
    public interface FactoryVisitor{
        void accept(String attributeVariableName, Attribute<?> attribute);
    }

    public <A> void visitAttributesFlat(FactoryVisitor consumer) {
        Field[] fields = getFields();
        for (Field field : fields) {
            try {
                Object fieldValue = field.get(this);
                if (fieldValue instanceof Attribute) {
                    consumer.accept(field.getName(),(Attribute) fieldValue);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void visitAttributesFlat(Consumer<Attribute<?>> consumer) {
        Field[] fields = getFields();
        for (Field field : fields) {
            try {
                Object fieldValue = field.get(this);
                if (fieldValue instanceof Attribute) {
                    consumer.accept((Attribute) fieldValue);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void visitAttributesTripleFlat(Optional<?> modelBase1, Optional<?> modelBase2, TriConsumer<Attribute<?>, Optional<Attribute<?>>, Optional<Attribute<?>>> consumer) {
        Field[] fields = getFields();
        for (Field field : fields) {
            try {
//                fields[f].setAccessible(true);
                Object fieldValue = field.get(this);
                if (fieldValue instanceof Attribute) {

                    Attribute<?> value1 = null;
                    if (modelBase1.isPresent()) {
                        value1 = (Attribute<?>) field.get(modelBase1.get());
                    }
                    Attribute<?> value2 = null;
                    if (modelBase2.isPresent()) {
                        value2 = (Attribute<?>) field.get(modelBase2.get());
                    }
                    consumer.accept((Attribute<?>) field.get(this), Optional.ofNullable(value1), Optional.ofNullable(value2));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }










    public void collectModelEntitiesTo(Set<FactoryBase<?,?>> allModelEntities) {
        allModelEntities.add(this);
        visitAttributesFlat(attribute -> attribute.collectChildren(allModelEntities));
    }

    public T copy() {
        return ObjectMapperBuilder.build().copy(this).reconstructMetadataDeepRoot();
    }

    /**copy including one the references first level of nested references*/
    public T copyOneLevelDeep(){
        return copyOneLevelDeep(0);
    }

    private T copyOneLevelDeep(final int level){
        if (level>1){
            return null;
        }
        T result = newInstance();
        result.setId(this.getId());
        this.visitAttributesDualFlat(result, (thisAttribute, copyAttribute) -> {
            Object value = thisAttribute.get();
            if (value instanceof FactoryBase){
                value=((FactoryBase)value).copyOneLevelDeep(level+1);
            }
            if (thisAttribute instanceof ReferenceListAttribute){
                final ObservableList<FactoryBase> referenceList = FXCollections.observableArrayList();
                ((ReferenceListAttribute)thisAttribute).get().forEach(factory -> referenceList.add(((FactoryBase)factory).copyOneLevelDeep(level+1)));
                value=referenceList;
            }

            copyAttribute.set(value);
        });
        return result;
    }


    @SuppressWarnings("unchecked")
    public void fixDuplicateObjects(Function<String, Optional<FactoryBase<?,?>>> getCurrentEntity) {
        visitAttributesFlat(attribute -> attribute.fixDuplicateObjects(getCurrentEntity));
    }

    public HashMap<FactoryBase<?,?>, FactoryBase<?,?>> getChildToParentMap(Set<FactoryBase<?,?>> allModelEntities) {
        HashMap<FactoryBase<?,?>, FactoryBase<?,?>> result = new HashMap<>();
        for (FactoryBase<?,?> factoryBase : allModelEntities) {
            factoryBase.visitAttributesFlat(attribute -> {
                attribute.visit(new Attribute.AttributeVisitor() {
                    @Override
                    public void value(Attribute<?> value) {

                    }

                    @Override
                    public void reference(ReferenceAttribute<?> referenc) {
                        result.put(((ReferenceAttribute<?>) attribute).get(), factoryBase);
                    }

                    @Override
                    public void referenceList(ReferenceListAttribute<?> referenceList) {
                        for (FactoryBase<?,?> factoryBaseRef : referenceList.get()) {
                            result.put(factoryBaseRef, factoryBase);
                        }
                    }
                });

            });
        }
        return result;
    }


    @JsonIgnore
    public String getDescriptiveName() {
        return getClass().getSimpleName();
    }

    public List<FactoryBase<?,?>> getMassPathTo(HashMap<FactoryBase<?,?>, FactoryBase<?,?>> childToParent, FactoryBase<?,?> target) {
        List<FactoryBase<?,?>> path = new ArrayList<>();
        Optional<FactoryBase<?,?>> pathElement = Optional.ofNullable(childToParent.get(target));
        while (pathElement.isPresent()) {
            path.add(pathElement.get());
            pathElement = Optional.ofNullable(childToParent.get(pathElement.get()));
        }
        Collections.reverse(path);
        return path;
    }

    public Optional<FactoryBase<?,?>> getParent(Set<FactoryBase<?,?>> allModelEntities, FactoryBase child) {
        return Optional.ofNullable(getChildToParentMap(allModelEntities).get(child));
    }

    /**Slow. for multiple calls use getMassPathTo*/
    public List<FactoryBase<?,?>> getPathTo(FactoryBase<?,?> target) {
        HashSet<FactoryBase<?,?>> allModelEntities = new HashSet<>();
        collectModelEntitiesTo(allModelEntities);
        return getMassPathTo(getChildToParentMap(allModelEntities), target);
    }

    @SuppressWarnings("unchecked")
    public void merge(Optional<FactoryBase<?,T>> originalValue, Optional<FactoryBase<?,T>> newValue, MergeResult mergeResult) {

        this.visitAttributesTripleFlat(originalValue, newValue, (currentAttribute, originalAttribute, newAttribute) -> {
            AttributeMergeHelper<?> attributeMergeHelper = currentAttribute.createMergeHelper();
            boolean mergeable = attributeMergeHelper.isMergeable(originalAttribute, newAttribute);
            MergeResultEntry<T> mergeResultEntry = new MergeResultEntry<>(FactoryBase.this, currentAttribute,  currentAttribute.getDisplayText(), newAttribute.map((attribute)->attribute.getDisplayText()).orElse(""));
            if (mergeable) {
                if (newAttribute.isPresent()) {
                    if (!attributeMergeHelper.equalValues(newAttribute.get())) {
                        mergeResult.addMergeExecutions(() -> attributeMergeHelper.merge(originalAttribute, newAttribute.get()));
                        mergeResult.addMergeInfo(mergeResultEntry);
                    }
                }
            } else {
                mergeResult.addConflictInfos(mergeResultEntry);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private T newInstance() {
        try {
            return (T) getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * after deserialization from json only the value is present and metadata are missing
     * we create a copy which contains the metadata and than copy the meta data in teh original object
     */
    public T reconstructMetadataDeepRoot() {
        T copy = this.newInstance();


        Field[] fields = getFields();
        for (Field field : fields) {
            try {
                if (Attribute.class.isAssignableFrom(field.getType())){
                    Object value=null;
                    if (field.get(this)!=null){
                        value= ((Attribute)field.get(this)).get();
                    }
                    field.setAccessible(true);
                    field.set(this,field.get(copy));

                    ((Attribute)field.get(this)).set(value);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        this.visitChildFactoriesFlat(factoryBase -> factoryBase.reconstructMetadataDeepRoot());

        return (T) this;
    }

    @JsonIgnore
    private static final Map<Class<?>, Field[]> fields = new HashMap<>();
    @JsonIgnore
    private Field[] instanceFields;
    private String id;

    public FactoryBase() {
        initFieldsCache();
    }


    @JsonIgnore
    private Field[] getFields() {
        return instanceFields;
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

    private void initFieldsCache() {
        synchronized (fields) {
            Field[] f = fields.get(getClass());
            if (f == null) {
                f = getClass().getFields();
                ArrayList<Field> removeStatic = new ArrayList<>();
                for (Field ff : f) {
                    if (!Modifier.isStatic(ff.getModifiers())) {
                        removeStatic.add(ff);
                    }
                }
                fields.put(getClass(), removeStatic.toArray(new Field[removeStatic.size()]));
            }
            instanceFields = f;
        }
    }



    private E createdLiveObjects;
    public E create() {
        Optional<E> previousLiveObject = Optional.ofNullable(createdLiveObjects);
        if (!changedDeep()) {
            return createdLiveObjects;
        } else{
            E liveObject = createImp(previousLiveObject);
            createdLiveObjects=liveObject;
            changed=false;
            return liveObject;
        }
    }

    protected abstract E createImp(Optional<E> previousLiveObject);

    public void collectLiveObjects(Map<String,LiveObject> liveObjects){

        //order important deep first
        this.visitChildFactoriesFlat(factory -> factory.collectLiveObjects(liveObjects));

        if (createdLiveObjects!=null){
            liveObjects.put(getId(),createdLiveObjects);
        }
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
        this.visitChildFactoriesFlat(factoryBase -> changedDeep = changedDeep || factoryBase.changedDeep());
        return changedDeep;
    }

    LoopProtector loopProtector = new LoopProtector();
    public void loopDetector(){
        loopProtector.enter();
        try {
            this.visitChildFactoriesFlat(factory -> factory.loopDetector());
        } finally {
            loopProtector.exit();
        }
    }

    @JsonIgnore
    public String getDisplayText(){
        return getDescriptiveName()+":"+getId();
    }

}
