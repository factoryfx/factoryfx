package de.factoryfx.data.merge.attribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.Attribute;
import javafx.collections.ObservableList;

public class ReferenceListMergeHelper<T extends Data> extends AttributeMergeHelper<List<T>> {

    public ReferenceListMergeHelper(Attribute<List<T>> attribute) {
        super(attribute);
    }


    @Override
    public boolean hasNoConflict(Optional<Attribute<?>> originalValue, Optional<Attribute<?>> newValue)  {
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean isMergeable(Optional<Attribute<?>> originalValue, Optional<Attribute<?>> newValue) {
        ObservableList<T> newValueTyped = null;
        if (newValue.isPresent()) {
            newValueTyped = (ObservableList<T>)newValue.get().get();
        }

        if (attribute.internal_match(newValueTyped)) {
            return false ;
        }
        return true;
    }

    @Override
    public void mergeTyped(Optional<List<T>> originalValue, List<T> newValue) {
        List<T> currentToEditList = attribute.get();

        List<T> currentList = new ArrayList<>(attribute.get());
        List<T> originalList = new ArrayList<>();
        if (originalValue.isPresent()) {
            originalList = new ArrayList<>(originalValue.get());
        }
        List<T> newList = new ArrayList<>(newValue);

        HashMap<Object, T> currentMap = new HashMap<>();
        HashMap<Object, T> originalMap = new HashMap<>();
        HashMap<Object, T> newMap = new HashMap<>();

        LinkedHashMap<Object, T> allMap = new LinkedHashMap<>();

        for (T base : originalList) {
            originalMap.put(base.getId(), base);
            allMap.put(base.getId(), base);
        }
        for (T base : newList) {
            newMap.put(base.getId(), base);
            allMap.put(base.getId(), base);
        }
        for (T base : currentList) {
            currentMap.put(base.getId(), base);
            allMap.put(base.getId(), base);
        }

        currentToEditList.clear();

        for (Map.Entry<Object, T> entry : allMap.entrySet()) {
            Object id = entry.getKey();
            if (currentMap.containsKey(id) && originalMap.containsKey(id) && newMap.containsKey(id)) {
                currentToEditList.add(entry.getValue());
            }
            if (currentMap.containsKey(id) && !originalMap.containsKey(id) && newMap.containsKey(id)) {
                currentToEditList.add(entry.getValue());
            }
            if (currentMap.containsKey(id) && !originalMap.containsKey(id) && !newMap.containsKey(id)) {
                currentToEditList.add(entry.getValue());
            }
            if (!currentMap.containsKey(id) && !originalMap.containsKey(id) && newMap.containsKey(id)) {
                currentToEditList.add(entry.getValue());
            }
        }

    }


}
