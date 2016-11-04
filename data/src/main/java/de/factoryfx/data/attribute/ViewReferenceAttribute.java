package de.factoryfx.data.attribute;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.factoryfx.data.Data;
import de.factoryfx.data.merge.attribute.AttributeMergeHelper;
import de.factoryfx.data.merge.attribute.NopMergeHelper;

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
        return root == null? null:view.apply(root,parent);
    }

    @Override
    public void set(T value) {
        //nothing
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
                    for (AttributeChangeListener<T> listener: listeners){
                        listener.changed(ViewReferenceAttribute.this,currentValue);
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

    List<AttributeChangeListener<T>> listeners= new ArrayList<>();
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
        listeners.remove(listener);
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
            referenceDisplayText=value.getDisplayText();
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
