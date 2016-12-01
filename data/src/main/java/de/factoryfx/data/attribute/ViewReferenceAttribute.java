package de.factoryfx.data.attribute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import de.factoryfx.data.Data;
import de.factoryfx.data.merge.attribute.AttributeMergeHelper;
import de.factoryfx.data.merge.attribute.NopMergeHelper;
import javafx.application.Platform;

@JsonIgnoreType
public class ViewReferenceAttribute<R extends Data, P extends Data, T extends Data> extends Attribute<T> {

    private P parent;
    private R root;
    private final BiFunction<R,P,T> view;

    public ViewReferenceAttribute(AttributeMetadata attributeMetadata, BiFunction<R,P,T> view) {
        super(attributeMetadata);
        this.view=view;
    }

    @Override
    public void collectChildren(Set<Data> allModelEntities) {
        //nothing
    }

    @Override
    public AttributeMergeHelper<?> createMergeHelper() {
        return new NopMergeHelper();
    }

    @Override
    public void fixDuplicateObjects(Function<Object, Optional<Data>> getCurrentEntity) {
        //nothing
    }

    @Override
    public T get() {
        return view.apply(root,parent);
    }

    @Override
    public void set(T value) {
        //nothing
    }

    @Override
    public void copyTo(Attribute<T> copyAttribute, Function<Data,Data> dataCopyProvider) {
        //nothing
    }

    @Override
    public void semanticCopyTo(Attribute<T> copyAttribute, Function<Data,Data> dataCopyProvider) {
        //nothing
    }


    //** so we don't need to initialise javax toolkit*/
    Consumer<Runnable> runlaterExecutor=(r)-> Platform.runLater(r);
    void setRunlaterExecutorForTest(Consumer<Runnable> runlaterExecutor){
        this.runlaterExecutor=runlaterExecutor;
    }

    public void runLater(Runnable runnable){
        runlaterExecutor.accept(runnable);
    }

    class DirtyTrackingThread extends Thread{
        volatile boolean tracking=true;
        T previousValue;
        @Override
        public void run() {
            super.run();
            while(tracking){
                T currentValue = get();
                if (
                     (previousValue==null && currentValue!=null) ||
                     (previousValue!=null && currentValue==null) ||
                     (previousValue!=null && currentValue!=null && !previousValue.getId().equals(currentValue.getId()))
                   ){
                    for (AttributeChangeListener<T> listener: new ArrayList<>(listeners)){
                        runLater(()-> listener.changed(ViewReferenceAttribute.this,currentValue));
                    }
                }
                previousValue= currentValue;
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        public void stopTracking() {
            tracking=false;
        }
    }
    DirtyTrackingThread dirtyTracking;

    List<AttributeChangeListener<T>> listeners= Collections.synchronizedList(new ArrayList<>());
    @Override
    public void addListener(AttributeChangeListener<T> listener) {
        listeners.add(listener);
        if (dirtyTracking==null){
            dirtyTracking = new DirtyTrackingThread();
            dirtyTracking.setDaemon(true);
            dirtyTracking.start();
        }
    }
    @Override
    public void removeListener(AttributeChangeListener<T> listener) {
        for (AttributeChangeListener<T> listenerItem: new ArrayList<>(listeners)){
            if (listenerItem.unwrap()==listener){
                listeners.remove(listenerItem);
            }
        }
        if (listeners.isEmpty()){
            dirtyTracking.stopTracking();
            dirtyTracking=null;
        }
    }

    @JsonIgnore
    public Optional<T> getOptional(){
        return Optional.ofNullable(get());
    }

    @Override
    public String getDisplayText() {
        String referenceDisplayText = "empty";
        T value = get();
        if (value !=null){
            referenceDisplayText=value.internal().getDisplayText();
        }
        return referenceDisplayText;
    }

    @Override
    public void visit(AttributeVisitor attributeVisitor) {
        //nothing
    }

    @JsonIgnore
    @Override
    public AttributeTypeInfo getAttributeType() {
        return new AttributeTypeInfo(Data.class,null,null, AttributeTypeInfo.AttributeTypeCategory.REFERENCE);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void prepareUsage(Data root, Data parent){
        this.parent=(P)parent;
        this.root=(R)root;;
    }

    @Override
    public void endUsage() {
        if (dirtyTracking!=null) {
            dirtyTracking.stopTracking();
        }
        listeners.clear();
    }
}
