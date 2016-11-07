package de.factoryfx.data.attribute;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.factoryfx.data.Data;
import de.factoryfx.data.merge.attribute.AttributeMergeHelper;
import de.factoryfx.data.merge.attribute.NopMergeHelper;
import javafx.collections.ObservableList;

public class ViewListReferenceAttribute <R extends Data, P extends Data, T extends Data> extends Attribute<List<T>> {
    private P parent;
    private R root;
    private final BiFunction<R,P,List<T>> view;

    public ViewListReferenceAttribute(AttributeMetadata attributeMetadata, BiFunction<R,P,List<T>> view) {
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
    public List<T> get() {
        return root==null? Collections.emptyList():view.apply(root,parent);
    }

    @Override
    public void set(List<T> value) {
        //nothing
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

                    for (AttributeChangeListener<List<T>> listener: listeners){
                        listener.changed(ViewListReferenceAttribute.this,currentList);
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
                        for (AttributeChangeListener<List<T>> listener: listeners){
                            listener.changed(ViewListReferenceAttribute.this,currentList);
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

    List<AttributeChangeListener<List<T>>> listeners= new ArrayList<>();
    @Override
    public void addListener(AttributeChangeListener<List<T>> listener) {
        listeners.add(listener);
        if (dirtyTracking==null){
            dirtyTracking = new DirtyTrackingThread();
            dirtyTracking.setDaemon(true);
            dirtyTracking.start();
        }
    }
    @Override
    public void removeListener(AttributeChangeListener<List<T>> listener) {
        listeners.remove(listener);
        if (listeners.isEmpty()){
            dirtyTracking.stopTracking();
            dirtyTracking=null;
        }
    }


    @Override
    public String getDisplayText() {
        List<T> list = get();
        StringBuilder stringBuilder = new StringBuilder("List (number of entries: "+ list.size()+")\n");
        for (T item: list){
            stringBuilder.append(item.internal().getDisplayText());
            stringBuilder.append(",\n");
        }
        return stringBuilder.toString();
    }

    @Override
    public void visit(AttributeVisitor attributeVisitor) {
        //nothing
    }

    @JsonIgnore
    @Override
    public AttributeTypeInfo getAttributeType() {
        return new AttributeTypeInfo(ObservableList.class,null,null,Data.class, AttributeTypeInfo.AttributeTypeCategory.REFERENCE_LIST, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void prepareEditing(Data root, Data parent){
        this.parent=(P)parent;
        this.root=(R)root;;
    }

    @Override
    public void endEditing() {
        if (dirtyTracking!=null) {
            dirtyTracking.stopTracking();
        }
        listeners.clear();
    }
}
