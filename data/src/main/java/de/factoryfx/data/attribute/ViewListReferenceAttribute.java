package de.factoryfx.data.attribute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import de.factoryfx.data.Data;

@JsonIgnoreType
public abstract class ViewListReferenceAttribute <R extends Data, T extends Data, A extends Attribute<List<T>,A>> extends Attribute<List<T>,A> implements RunLaterAble {
    private R root;
    protected final Function<R,List<T>> view;

    public ViewListReferenceAttribute(Function<R,List<T>> view) {
        super();
        this.view=view;
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

    private boolean referenceEquals(Data ref1, Data ref2) {
        if (ref1 == null && ref2 == null) {
            return true;
        }
        if (ref1 == null || ref2 == null) {
            return false;
        }
        return ref1.getId().equals(ref2.getId());
    }

    @Override
    public void internal_fixDuplicateObjects(Map<String, Data> idToDataMap) {
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

    @Override
    public void internal_copyTo(A copyAttribute, Function<Data,Data> dataCopyProvider) {
        //nothing
    }

    @Override
    public void internal_semanticCopyTo(A copyAttribute) {
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

                    for (AttributeChangeListener<List<T>,A> listener: new ArrayList<>(listeners)){
                        runLater(()-> listener.changed(ViewListReferenceAttribute.this,currentList));
                    }
                }
                if (!isEmpty(previousList) && !isEmpty(currentList)) {
                    HashSet<Object> idList=new HashSet<>();
                    previousList.stream().map(Data::getId).forEach(idList::add);
                    boolean changed=false;
                    for(T item: currentList){
                        if (!idList.contains(item.getId())){
                            changed=true;
                            break;
                        }
                    }
                    if (changed){
                        for (AttributeChangeListener<List<T>,A> listener: new ArrayList<>(listeners)){
                            runLater(()->listener.changed(ViewListReferenceAttribute.this,currentList));
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

    List<AttributeChangeListener<List<T>,A>> listeners;
    @Override
    @SuppressWarnings("unchecked")
    public void internal_addListener(AttributeChangeListener<List<T>,A> listener) {
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
    public void internal_removeListener(AttributeChangeListener<List<T>,A> listener) {
        if (listeners!=null) {
            for (AttributeChangeListener<List<T>,A> listenerItem: new ArrayList<>(listeners)){
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


    @JsonIgnore
    @Override
    public AttributeTypeInfo internal_getAttributeType() {
        return new AttributeTypeInfo(List.class,null,null,Data.class, AttributeTypeInfo.AttributeTypeCategory.REFERENCE_LIST);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void internal_prepareUsage(Data root, Data parent){
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

    public Stream<T> stream() {
        return get().stream();
    }

    @Override
    public boolean internal_ignoreForMerging() {
        return true;
    }

}
