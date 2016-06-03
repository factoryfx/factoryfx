package de.factoryfx.factory;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import de.factoryfx.factory.merge.FactoryMerger;
import de.factoryfx.factory.merge.MergeDiff;

public class FactoryManager<T extends FactoryBase<? extends LiveObject, T>> {

    T currentFactory;

    public MergeDiff update(T commonVersion ,T newVersion){
        newVersion.loopDetector();
        LinkedHashMap<String, LiveObject> previousLiveObjects = new LinkedHashMap<>();
        currentFactory.collectLiveObjects(previousLiveObjects);

        FactoryMerger factoryMerger = new FactoryMerger(currentFactory, commonVersion, newVersion);
        MergeDiff mergeDiff= factoryMerger.mergeIntoCurrent();
        if (mergeDiff.hasNoConflicts()){
            currentFactory.create();

            LinkedHashMap<String, LiveObject> newLiveObjects = new LinkedHashMap<>();
            currentFactory.collectLiveObjects(newLiveObjects);
            updateLiveObjects(previousLiveObjects,newLiveObjects);
        }
        return mergeDiff;
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

    public void start(T newFactory){
        newFactory.loopDetector();
        currentFactory=newFactory;

        newFactory.create();

        LinkedHashMap<String, LiveObject> newLiveObjects = new LinkedHashMap<>();
        newFactory.collectLiveObjects(newLiveObjects);

        for (LiveObject newLiveObject: newLiveObjects.values()){
            newLiveObject.start();
        }
    }

}
