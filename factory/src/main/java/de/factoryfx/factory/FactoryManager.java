package de.factoryfx.factory;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.function.Function;

import com.google.common.collect.TreeTraverser;
import de.factoryfx.factory.merge.FactoryMerger;
import de.factoryfx.factory.merge.MergeDiff;
import de.factoryfx.factory.merge.MergeResultEntry;

public class FactoryManager<V,T extends FactoryBase<? extends LiveObject<V>, T>> {

    private T currentFactory;

    @SuppressWarnings("unchecked")
    public MergeDiff update(T commonVersion , T newVersion, Locale locale){
        newVersion.loopDetector();
        HashSet<LiveObject> previousLiveObjects = stopOrderProvider.apply(currentFactory);

        FactoryMerger factoryMerger = new FactoryMerger(currentFactory, commonVersion, newVersion);
        factoryMerger.setLocale(locale);
        MergeDiff mergeDiff= factoryMerger.mergeIntoCurrent();
        if (mergeDiff.hasNoConflicts()){
            for (FactoryBase<?,?> current : currentFactory.collectChildFactories()){
                current.unMarkChanged();
            }
            for (MergeResultEntry<?> mergeResultEntry: mergeDiff.getMergeInfos()){
                mergeResultEntry.parent.markChanged();
            }


            currentFactory.instance();


            HashSet<LiveObject> newLiveObjects = startOrderProvider.apply(currentFactory);
            updateLiveObjects(previousLiveObjects,newLiveObjects);
        }
        return mergeDiff;
    }


    /** get the merge result  but don't execute the merge and liveobjects Update*/
    @SuppressWarnings("unchecked")
    public MergeDiff simulateUpdate(T commonVersion , T newVersion, Locale locale){
        newVersion.loopDetector();

        FactoryMerger factoryMerger = new FactoryMerger(currentFactory, commonVersion, newVersion);
        factoryMerger.setLocale(locale);
        return factoryMerger.createMergeResult();
    }

    public MergeDiff update(T commonVersion , T newVersion){
        return update(commonVersion , newVersion,Locale.ENGLISH);
    }


    private void updateLiveObjects(HashSet<LiveObject> previousLiveObjects, HashSet<LiveObject> newLiveObjects){
        for (LiveObject previousLiveObject: previousLiveObjects){
            if (!newLiveObjects.contains(previousLiveObject)){
                previousLiveObject.stop();
            }
        }

        for (LiveObject newLiveObject: newLiveObjects){
            if (previousLiveObjects.contains(newLiveObject)){
                //nothing reused live object
            }
            if (!previousLiveObjects.contains(newLiveObject)){
                newLiveObject.start();
            }
        }
    }

    TreeTraverser<FactoryBase<?,?>> factoryTraverser = new TreeTraverser<FactoryBase<?,?>>() {
        @Override
        public Iterable<FactoryBase<?,?>> children(FactoryBase<?,?> factory) {
            return factory.getChildFactories();
        }
    };
    private Function<T,HashSet<LiveObject>> startOrderProvider= root -> {
        HashSet<LiveObject> result = new HashSet<>();
        for (FactoryBase<?,?> factory : factoryTraverser.postOrderTraversal(root)) {
            factory.getCreatedLiveObject().ifPresent(result::add);
        }
        return result;
    };
    public void customizeStartOrder(Function<T,HashSet<LiveObject>> orderProvider){
        startOrderProvider=orderProvider;
    }

    private Function<T,HashSet<LiveObject>> stopOrderProvider= root -> {
        HashSet<LiveObject> result = new HashSet<>();
        for (FactoryBase<?,?> factory : factoryTraverser.breadthFirstTraversal(root)) {
            factory.getCreatedLiveObject().ifPresent(result::add);
        }
        return result;
    };
    public void customizeStopOrder(Function<T,HashSet<LiveObject>> orderProvider){
        stopOrderProvider=orderProvider;
    }

    public T getCurrentFactory(){
        return currentFactory;
    }

    @SuppressWarnings("unchecked")
    public void start(T newFactory){
        newFactory.loopDetector();
        currentFactory=newFactory;

        newFactory.instance();

        LinkedHashMap<String, LiveObject> newLiveObjects = new LinkedHashMap<>();
        newFactory.collectLiveObjects(newLiveObjects);

        for (LiveObject newLiveObject: newLiveObjects.values()){
            newLiveObject.start();
        }
    }

    @SuppressWarnings("unchecked")
    public void stop(){
        LinkedHashMap<String, LiveObject> newLiveObjects = new LinkedHashMap<>();
        currentFactory.collectLiveObjects(newLiveObjects);

        for (LiveObject newLiveObject: newLiveObjects.values()){
            newLiveObject.stop();
        }
    }

    @SuppressWarnings("unchecked")
    public V query(V visitor){
        LinkedHashMap<String, LiveObject> previousLiveObjects = new LinkedHashMap<>();
        currentFactory.collectLiveObjects(previousLiveObjects);
        for(LiveObject<V> liveObject: previousLiveObjects.values()){
            liveObject.accept(visitor);
        }
        return visitor;
    }

}
