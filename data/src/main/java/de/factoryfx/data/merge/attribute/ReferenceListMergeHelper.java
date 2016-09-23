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

public class ReferenceListMergeHelper<T extends Data> extends AttributeMergeHelper<ObservableList<T>> {

    public ReferenceListMergeHelper(Attribute<ObservableList<T>> attribute) {
        super(attribute);
    }

    @Override
    public boolean equalValuesTyped(ObservableList<T> valueList) {
        List<T> currentList = attribute.get();

        if (valueList==null ){
            return false;
        }
        if (currentList.size() != valueList.size()) {
            return false;
        }
        for (int i = 0; i < currentList.size(); i++) {
            if (!referenceEquals(currentList.get(i), valueList.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean hasNoConflictTyped(Optional<ObservableList<T>> originalValue, Optional<ObservableList<T>> newValue) {
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean isMergeable(Optional<Attribute<?>> originalValue, Optional<Attribute<?>> newValue) {
        ObservableList<T> newValueTyped = null;
        if (newValue.isPresent()) {
            newValueTyped = (ObservableList<T>)newValue.get().get();
        }
//        ObservableList<T> originalValueTyped = null;
//        if (originalValue.isPresent()) {
//            originalValueTyped = (ObservableList<T>) originalValue.get().get();
//        }
        if (/*!equalValuesTyped(originalValueTyped) ||*/ equalValuesTyped(newValueTyped)) {
            return false ;
        }
        return true;
    }

    @Override
    public void mergeTyped(Optional<ObservableList<T>> originalValue, ObservableList<T> newValue) {
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

    private boolean referenceEquals(Data ref1, Data ref2) {
        if (ref1 == null && ref2 == null) {
            return true;
        }
        if (ref1 == null || ref2 == null) {
            return false;
        }
        return ref1.getId().equals(ref2.getId());
    }

}
