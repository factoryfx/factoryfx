package io.github.factoryfx.factory.attribute.dependency;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.attribute.AttributeChangeListener;
import io.github.factoryfx.factory.attribute.CollectionAttributeUtil;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.RunLaterAble;

@JsonIgnoreType
public class FactoryViewListReferenceAttribute<R extends FactoryBase<?,R>,L, T extends FactoryBase<L,R>> extends Attribute<List<T>,FactoryViewListReferenceAttribute<R,L,T>> implements RunLaterAble, RootAwareAttribute<R,FactoryViewListReferenceAttribute<R,L,T>> {


    private R root;
    protected final Function<R,List<T>> view;

    public FactoryViewListReferenceAttribute(Function<R,List<T>> view) {
        super();
        this.view=view;
    }

    public List<L> instances(){
        ArrayList<L> result = new ArrayList<>();
        for(T item: get()){
            result.add(item.internal().instance());
        }
        return result;
    }

    public boolean add(T data){
        return get().add(data);
    }

    public L instance(Predicate<T> filter){
        Optional<T> any = get().stream().filter(filter).findAny();
        return any.map(t -> t.internal().instance()).orElse(null);
    }

    @Override
    public boolean internal_mergeMatch(List<T> value) {
        final List<T> list = get();
        if (value==null ){
            return false;
        }
        if (list.size() != value.size()) {
            return false;
        }
        for (int i = 0; i < list.size(); i++) {
            if (!referenceEquals(list.get(i), value.get(i))) {
                return false;
            }
        }
        return true;
    }

    private boolean referenceEquals(FactoryBase<?,?> ref1, FactoryBase<?,?> ref2) {
        if (ref1 == null && ref2 == null) {
            return true;
        }
        if (ref1 == null || ref2 == null) {
            return false;
        }
        return ref1.idEquals(ref2);
    }

    @Override
    public void internal_fixDuplicateObjects(Map<String, FactoryBase<?,?>> idToDataMap) {
        //nothing
    }

    @Override
    public List<T> get() {
        List<T> result = view.apply(root);
        if (result==null){
            return new ArrayList<>();
        }
        return result;
    }

    @Override
    public void set(List<T> value) {
        //nothing
    }

//    @Override
//    public void internal_copyTo(FactoryViewListReferenceAttribute<R,L,T> copyAttribute, FactoryBase.DataCopyProvider dataCopyProvider) {
//        //nothing
//    }

    @Override
    public void internal_semanticCopyTo(FactoryViewListReferenceAttribute<R,L,T> copyAttribute) {
        //nothing
    }

    //** so we don't need to initialise javax toolkit for tests*/
    Consumer<Runnable> runlaterExecutor;
    @Override
    public void setRunlaterExecutor(Consumer<Runnable> runlaterExecutor){
        this.runlaterExecutor=runlaterExecutor;
    }

    private void runLater(Runnable runnable){
        if (runlaterExecutor!=null){
            runlaterExecutor.accept(runnable);
        }
    }

    class DirtyTrackingThread extends Thread{
        volatile boolean tracking=true;
        List<T> previousList;
        @Override
        public void run() {
            super.run();
            while(tracking){
                List<T>  currentList = get();
                if ((isEmpty(previousList) && !isEmpty(currentList)) ||
                        (!isEmpty(previousList) && isEmpty(currentList))){

                    for (AttributeChangeListener<List<T>,FactoryViewListReferenceAttribute<R,L,T>> listener: new ArrayList<>(listeners)){
                        runLater(()-> listener.changed(FactoryViewListReferenceAttribute.this,currentList));
                    }
                }
                if (!isEmpty(previousList) && !isEmpty(currentList)) {
                    HashSet<Object> idList=new HashSet<>();
                    previousList.stream().map(FactoryBase::getId).forEach(idList::add);
                    boolean changed=false;
                    for(T item: currentList){
                        if (!idList.contains(item.getId())){
                            changed=true;
                            break;
                        }
                    }
                    if (changed){
                        for (AttributeChangeListener<List<T>,FactoryViewListReferenceAttribute<R,L,T>> listener: new ArrayList<>(listeners)){
                            runLater(()->listener.changed(FactoryViewListReferenceAttribute.this,currentList));
                        }
                    }
                }
                previousList= currentList;
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        private boolean isEmpty(List<T> list){
            return list == null || list.isEmpty();
        }

        public void stopTracking() {
            tracking=false;
        }
    }
    DirtyTrackingThread dirtyTracking;

    List<AttributeChangeListener<List<T>,FactoryViewListReferenceAttribute<R,L,T>>> listeners;
    @Override
    public void internal_addListener(AttributeChangeListener<List<T>,FactoryViewListReferenceAttribute<R,L,T>> listener) {
        if (listeners==null){
            listeners=Collections.synchronizedList(new ArrayList<>());
        }
        listeners.add(listener);
        if (dirtyTracking==null){
            dirtyTracking = new DirtyTrackingThread();
            dirtyTracking.setDaemon(true);
            dirtyTracking.start();
        }
    }
    @Override
    public void internal_removeListener(AttributeChangeListener<List<T>,FactoryViewListReferenceAttribute<R,L,T>> listener) {
        if (listeners!=null) {
            for (AttributeChangeListener<List<T>,FactoryViewListReferenceAttribute<R,L,T>> listenerItem: new ArrayList<>(listeners)){
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

    @Override
    public String getDisplayText() {
        return new CollectionAttributeUtil<>(get(), t -> t.internal().getDisplayText()).getDisplayText();
    }

    @Override
    public void internal_addBackReferences(R root, FactoryBase<?,R> parent){
        this.root=root;
    }

    @Override
    public void internal_copyTo(FactoryViewListReferenceAttribute<R, L, T> copyAttribute, int level, int maxLevel, List<FactoryBase<?, R>> oldData, FactoryBase<?, R> parent, R root) {
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

    public Stream<T> stream() {
        return get().stream();
    }

    @Override
    public boolean internal_ignoreForMerging() {
        return true;
    }
}

