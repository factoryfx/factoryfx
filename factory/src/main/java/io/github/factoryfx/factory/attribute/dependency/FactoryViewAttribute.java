package io.github.factoryfx.factory.attribute.dependency;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import io.github.factoryfx.factory.attribute.*;
import io.github.factoryfx.factory.FactoryBase;

/**
 * Allows to programmatically define a dependency<br>
 * This should only be used in exceptional cases. e.g. if the customer demand an unsuitable destructure<br>
 * It should not be used to model singletons. (instead us the {@link io.github.factoryfx.factory.builder.FactoryTreeBuilder})
 *
 * @param <R> root
 * @param <L> live object factory
 * @param <F> factory
 */
@JsonIgnoreType
public class FactoryViewAttribute<R extends FactoryBase<?,R>,L, F extends FactoryBase<L,R>>  extends Attribute<F, FactoryViewAttribute<R,L, F>> implements RunLaterAble, FactoryChildrenEnclosingAttribute {

    R root;
    protected final Function<R, F> view;

    public FactoryViewAttribute(Function<R, F> view) {
        this.view=view;
    }

    public L instance() {
        if (get() == null) {
            return null;
        }
        return get().internal().instance();
    }

    @Override
    public void internal_merge(F newFactory) {
        //nothing
    }

    @Override
    public boolean internal_mergeMatch(AttributeMatch<F> value) {
        final F thisValue = this.get();
        return internal_referenceEquals(thisValue,value.get());
    }

    @Override
    public <RL extends FactoryBase<?,RL>> void internal_fixDuplicateObjects(Map<UUID, FactoryBase<?,RL>> idToDataMap) {
        //nothing
    }

    @Override
    public F get() {
        return view.apply(root);
    }

    @Override
    public void set(F value) {
        //nothing
    }

    @SuppressWarnings("unchecked")
    @Override
    public void internal_addBackReferences(FactoryBase<?,?> root, FactoryBase<?,?> parent) {
        this.root=(R)root;
    }

    @Override
    public void internal_copyTo(AttributeCopy<F> copyAttribute,Function<FactoryBase<?,?>,FactoryBase<?,?>> newCopyInstanceProvider, int level, int maxLevel, List<FactoryBase<?, ?>> oldData, FactoryBase<?, ?> parent, FactoryBase<?, ?> root) {

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
        F previousValue;
        @Override
        public void run() {
            super.run();
            while(tracking){
                F currentValue = get();
                if (
                        (previousValue==null && currentValue!=null) ||
                                (previousValue!=null && currentValue==null) ||
                                (previousValue!=null && currentValue!=null && !previousValue.idEquals(currentValue))
                ){
                    for (AttributeChangeListener<F, FactoryViewAttribute<R,L, F>> listener: new ArrayList<>(listeners)){
                        runLater(()-> listener.changed(FactoryViewAttribute.this,currentValue));
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
    FactoryViewAttribute.DirtyTrackingThread dirtyTracking;

    List<AttributeChangeListener<F, FactoryViewAttribute<R,L, F>>> listeners;
    @Override
    public void internal_addListener(AttributeChangeListener<F, FactoryViewAttribute<R,L, F>> listener) {
        if (listeners==null){
            listeners= Collections.synchronizedList(new ArrayList<>());
        }
        listeners.add(listener);
        if (dirtyTracking==null){
            dirtyTracking = new FactoryViewAttribute.DirtyTrackingThread();
            dirtyTracking.setDaemon(true);
            dirtyTracking.start();
        }
    }
    @Override
    public void internal_removeListener(AttributeChangeListener<F, FactoryViewAttribute<R,L, F>> listener) {
        if (listeners!=null){
            for (AttributeChangeListener<F, FactoryViewAttribute<R,L, F>> listenerItem: new ArrayList<>(listeners)){
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
    public Optional<F> getOptional(){
        return Optional.ofNullable(get());
    }

    @Override
    public String getDisplayText() {
        String referenceDisplayText = "empty";
        F value = get();
        if (value !=null){
            referenceDisplayText=value.internal().getDisplayText();
        }
        return referenceDisplayText;
    }

    @Override
    public void internal_reset() {
        //nothing
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
    public void internal_visitChildren(Consumer<FactoryBase<?, ?>> consumer, boolean includeViews) {
        if (includeViews){
            F factory = get();
            if (factory != null) {
                consumer.accept(factory);
            }
        }
    }

}