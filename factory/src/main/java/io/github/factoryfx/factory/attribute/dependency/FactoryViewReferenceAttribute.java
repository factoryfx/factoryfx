package io.github.factoryfx.factory.attribute.dependency;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.attribute.AttributeChangeListener;
import io.github.factoryfx.factory.attribute.RunLaterAble;
import io.github.factoryfx.factory.FactoryBase;

@JsonIgnoreType
public class FactoryViewReferenceAttribute<R extends FactoryBase<?,R>,L, T extends FactoryBase<L,?>>  extends Attribute<T,FactoryViewReferenceAttribute<R,L,T>> implements RunLaterAble, RootAwareAttribute<R,Attribute<T,FactoryViewReferenceAttribute<R,L,T>>> {

    R root;
    protected final Function<R,T> view;

    public FactoryViewReferenceAttribute(Function<R,T> view) {
        this.view=view;
    }

    public L instance() {
        if (get() == null) {
            return null;
        }
        return get().internal().instance();
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
        return thisValue.idEquals(value);
    }

    @Override
    public void internal_fixDuplicateObjects(Map<String, FactoryBase<?,?>> idToDataMap) {
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
    public void internal_addBackReferences(R root, FactoryBase<?, R> parent) {
        this.root=root;
    }

    @Override
    public void internal_copyTo(Attribute<T, FactoryViewReferenceAttribute<R, L, T>> copyAttribute, int level, int maxLevel, List<FactoryBase<?, R>> oldData, FactoryBase<?, R> parent, R root) {
        //nothing
    }

    @Override
    public void internal_semanticCopyTo(FactoryViewReferenceAttribute<R,L,T> copyAttribute) {
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
                                (previousValue!=null && currentValue!=null && !previousValue.idEquals(currentValue))
                ){
                    for (AttributeChangeListener<T,FactoryViewReferenceAttribute<R,L,T>> listener: new ArrayList<>(listeners)){
                        runLater(()-> listener.changed(FactoryViewReferenceAttribute.this,currentValue));
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
    FactoryViewReferenceAttribute.DirtyTrackingThread dirtyTracking;

    List<AttributeChangeListener<T,FactoryViewReferenceAttribute<R,L,T>>> listeners;
    @Override
    public void internal_addListener(AttributeChangeListener<T,FactoryViewReferenceAttribute<R,L,T>> listener) {
        if (listeners==null){
            listeners= Collections.synchronizedList(new ArrayList<>());
        }
        listeners.add(listener);
        if (dirtyTracking==null){
            dirtyTracking = new FactoryViewReferenceAttribute.DirtyTrackingThread();
            dirtyTracking.setDaemon(true);
            dirtyTracking.start();
        }
    }
    @Override
    public void internal_removeListener(AttributeChangeListener<T,FactoryViewReferenceAttribute<R,L,T>> listener) {
        if (listeners!=null){
            for (AttributeChangeListener<T,FactoryViewReferenceAttribute<R,L,T>> listenerItem: new ArrayList<>(listeners)){
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