package de.factoryfx.data.attribute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import de.factoryfx.data.Data;

@JsonIgnoreType
public abstract class ViewReferenceAttribute<R extends Data, T extends Data,A extends Attribute<T,A>> extends Attribute<T,A> implements RunLaterAble {

    private R root;
    protected final Function<R,T> view;

    public ViewReferenceAttribute(Function<R,T> view) {
        super();
        this.view=view;
    }

    @Override
    public boolean internal_mergeMatch(T value) {
        final T thisValue = this.get();
        if (thisValue == null && value == null) {
            return true;
        }
        if (thisValue == null || value == null) {
            return false;
        }
        return thisValue.getId().equals(value.getId());
    }

    @Override
    public void internal_fixDuplicateObjects(Map<String, Data> idToDataMap) {
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
    public void internal_copyTo(A copyAttribute, Function<Data,Data> dataCopyProvider) {
        //nothing
    }

    @Override
    public void internal_semanticCopyTo(A copyAttribute) {
        //nothing
    }


    //** so we don't need to initialise javax toolkit in test, Platform.runLater(runnable);*/
    Consumer<Runnable> runlaterExecutor;
    public void setRunlaterExecutor(Consumer<Runnable> runlaterExecutor){
        this.runlaterExecutor=runlaterExecutor;
    }

    public void runLater(Runnable runnable){
        if (runlaterExecutor!=null) {
            runlaterExecutor.accept(runnable);
        }
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
                    for (AttributeChangeListener<T,A> listener: new ArrayList<>(listeners)){
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

    List<AttributeChangeListener<T,A>> listeners;
    @Override
    @SuppressWarnings("unchecked")
    public void internal_addListener(AttributeChangeListener<T,A> listener) {
        if (listeners==null){
            listeners= Collections.synchronizedList(new ArrayList<>());
        }
        listeners.add(listener);
        if (dirtyTracking==null){
            dirtyTracking = new DirtyTrackingThread();
            dirtyTracking.setDaemon(true);
            dirtyTracking.start();
        }
    }
    @Override
    public void internal_removeListener(AttributeChangeListener<T,A> listener) {
        if (listeners!=null){
            for (AttributeChangeListener<T,A> listenerItem: new ArrayList<>(listeners)){
                if (listenerItem.unwrap()==listener){
                    listeners.remove(listenerItem);
                }
            }
            if (listeners.isEmpty() && dirtyTracking != null){
                dirtyTracking.stopTracking();
                dirtyTracking=null;
            }
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
    
    @JsonIgnore
    @Override
    public AttributeTypeInfo internal_getAttributeType() {
        return new AttributeTypeInfo(Data.class,null,null, AttributeTypeInfo.AttributeTypeCategory.REFERENCE);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void internal_prepareUsageFlat(Data root, Data parent){
        this.root=(R)root;
    }

    @Override
    public void internal_endUsage() {
        if (dirtyTracking!=null) {
            dirtyTracking.stopTracking();
        }
        if (listeners!=null){
            listeners.clear();
        }
    }

    @Override
    public boolean internal_ignoreForMerging() {
        return true;
    }


}
