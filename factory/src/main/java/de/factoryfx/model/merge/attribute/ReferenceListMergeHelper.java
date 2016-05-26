package de.factoryfx.model.merge.attribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import de.factoryfx.model.FactoryBase;
import de.factoryfx.model.attribute.Attribute;
import javafx.collections.ObservableList;

public class ReferenceListMergeHelper<T extends FactoryBase<?,? super T>> extends AttributeMergeHelper<List<T>> {

    final Attribute<ObservableList<T>> attribute;

    public ReferenceListMergeHelper(Attribute<ObservableList<T>> attribute) {
        this.attribute = attribute;
    }

    @Override
    public boolean equalValuesTyped(List<T> value) {
        List<T> currentList = attribute.get();
        List<T> valueList = value;

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
    public boolean isMergeableTyped(Optional<List<T>> originalValue, Optional<List<T>> newValue) {
        return true;
    }

    @Override
    public void mergeTyped(Optional<List<T>> originalValue, List<T> newValue) {
        ObservableList<T> currentToEditList = attribute.get();

        List<T> currentList = new ArrayList<>(attribute.get());
        List<T> originalList = new ArrayList<>();
        if (originalValue.isPresent()) {
            originalList = new ArrayList<>(originalValue.get());
        }
        List<T> newList = new ArrayList<>(newValue);

        HashMap<String, T> currentMap = new HashMap<>();
        HashMap<String, T> originalMap = new HashMap<>();
        HashMap<String, T> newMap = new HashMap<>();

        LinkedHashMap<String, T> allMap = new LinkedHashMap<>();

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

        for (Map.Entry<String, T> entry : allMap.entrySet()) {
            String id = entry.getKey();
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

    private boolean referenceEquals(FactoryBase<?,? super T> ref1, FactoryBase<?,? super T> ref2) {
        if (ref1 == null && ref2 == null) {
            return true;
        }
        if (ref1 == null || ref2 == null) {
            return false;
        }
        return ref1.getId().equals(ref2.getId());
    }

}
