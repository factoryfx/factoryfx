package de.factoryfx.factory;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import de.factoryfx.factory.merge.FactoryMerger;
import de.factoryfx.factory.merge.MergeDiff;
import de.factoryfx.factory.merge.MergeResultEntry;

public class FactoryManager<V,T extends FactoryBase<? extends LiveObject<V>, T>> {

    private T currentFactory;

    @SuppressWarnings("unchecked")
    public MergeDiff update(T commonVersion , T newVersion, Locale locale){
        newVersion.loopDetector();
        LinkedHashMap<String, LiveObject> previousLiveObjects = new LinkedHashMap<>();
        currentFactory.collectLiveObjects(previousLiveObjects);

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

            LinkedHashMap<String, LiveObject> newLiveObjects = new LinkedHashMap<>();
            currentFactory.collectLiveObjects(newLiveObjects);
            updateLiveObjects(previousLiveObjects,newLiveObjects);
        }
        return mergeDiff;
    }


    /** get the merge result  but don't execute the merge and liveobjects Update*/
    @SuppressWarnings("unchecked")
    public MergeDiff simulateUpdate(T commonVersion , T newVersion, Locale locale){
        newVersion.loopDetector();
        LinkedHashMap<String, LiveObject> previousLiveObjects = new LinkedHashMap<>();
        currentFactory.collectLiveObjects(previousLiveObjects);

        FactoryMerger factoryMerger = new FactoryMerger(currentFactory, commonVersion, newVersion);
        factoryMerger.setLocale(locale);
        return factoryMerger.createMergeResult();
    }

    public MergeDiff update(T commonVersion , T newVersion){
        return update(commonVersion , newVersion,Locale.ENGLISH);
    }


    private void updateLiveObjects(Map<String, LiveObject> previousLiveObjects, Map<String, LiveObject> newLiveObjects){
        HashSet<LiveObject> previousLiveObjectsSet = new HashSet<>();
        previousLiveObjects.values().forEach(liveObject -> previousLiveObjectsSet.add(liveObject));

        HashSet<LiveObject> newLiveObjectsSet = new HashSet<>();
        newLiveObjects.values().forEach(liveObject -> newLiveObjectsSet.add(liveObject));


        for (LiveObject previousLiveObject: previousLiveObjectsSet){
            if (!newLiveObjectsSet.contains(previousLiveObject)){
                previousLiveObject.stop();
            }
        }

        for (LiveObject newLiveObject: newLiveObjectsSet){
            if (previousLiveObjectsSet.contains(newLiveObject)){
                //nothing reused live object
            }
            if (!previousLiveObjectsSet.contains(newLiveObject)){
                newLiveObject.start();
            }
        }
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
