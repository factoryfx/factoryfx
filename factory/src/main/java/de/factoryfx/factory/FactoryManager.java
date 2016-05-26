package de.factoryfx.factory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

public class FactoryManager<T extends FactoryBase<? extends LiveObject, ? extends FactoryBase>> {

    T currentFactory;

    public void update(T newFactory){
        LinkedHashMap<String, LiveObject> previousLiveObjects = new LinkedHashMap<>();
        currentFactory.collectLiveObjects(previousLiveObjects);

        newFactory.create(new PreviousLiveObjectProvider(previousLiveObjects));
        newFactory.collectLiveObjects(new HashMap<>());
        currentFactory=newFactory;

        LinkedHashMap<String, LiveObject> newLiveObjects = new LinkedHashMap<>();
        currentFactory.collectLiveObjects(previousLiveObjects);

        updateLiveObjects(previousLiveObjects,newLiveObjects);
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
        currentFactory=newFactory;

        newFactory.create(new PreviousLiveObjectProvider(new HashMap<>()));

        LinkedHashMap<String, LiveObject> newLiveObjects = new LinkedHashMap<>();
        newFactory.collectLiveObjects(newLiveObjects);

        for (LiveObject newLiveObject: newLiveObjects.values()){
            newLiveObject.start();
        }
    }

}
