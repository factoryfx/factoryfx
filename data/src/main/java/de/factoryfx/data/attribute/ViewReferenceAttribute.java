package de.factoryfx.data.attribute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import de.factoryfx.data.Data;
import de.factoryfx.data.merge.attribute.AttributeMergeHelper;
import de.factoryfx.data.merge.attribute.NopMergeHelper;
import javafx.application.Platform;

@JsonIgnoreType
public class ViewReferenceAttribute<R extends Data, T extends Data> extends Attribute<T> {

    private R root;
    private final Function<R,T> view;

    public ViewReferenceAttribute(AttributeMetadata attributeMetadata, Function<R,T> view) {
        super(attributeMetadata);
        this.view=view;
    }

    @Override
    public void internal_collectChildren(Set<Data> allModelEntities) {
        //nothing
    }

    @Override
    public AttributeMergeHelper<?> internal_createMergeHelper() {
        return new NopMergeHelper();
    }

    @Override
    public void internal_fixDuplicateObjects(Map<Object, Data> idToDataMap) {
        //nothing
    }

    @Override
    public T get() {
        return view.apply(root);
    }

    @Override
    public void set(T value) {
        //nothing
    }

    @Override
    public void internal_copyTo(Attribute<T> copyAttribute, Function<Data,Data> dataCopyProvider) {
        //nothing
    }

    @Override
    public void internal_semanticCopyTo(Attribute<T> copyAttribute, Function<Data,Data> dataCopyProvider) {
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
    public void internal_addListener(AttributeChangeListener<T> listener) {
        listeners.add(listener);
        if (dirtyTracking==null){
            dirtyTracking = new DirtyTrackingThread();
            dirtyTracking.setDaemon(true);
            dirtyTracking.start();
        }
    }
    @Override
    public void internal_removeListener(AttributeChangeListener<T> listener) {
        for (AttributeChangeListener<T> listenerItem: new ArrayList<>(listeners)){
            if (listenerItem.unwrap()==listener){
                listeners.remove(listenerItem);
            }
        }
        if (listeners.isEmpty() && dirtyTracking != null){
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
    public void internal_visit(AttributeVisitor attributeVisitor) {
        //nothing
    }

    @JsonIgnore
    @Override
    public AttributeTypeInfo internal_getAttributeType() {
        return new AttributeTypeInfo(Data.class,null,null, AttributeTypeInfo.AttributeTypeCategory.REFERENCE);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void internal_prepareUsage(Data root){
        this.root=(R)root;;
    }

    @Override
    public void internal_endUsage() {
        if (dirtyTracking!=null) {
            dirtyTracking.stopTracking();
        }
        listeners.clear();
    }
}
