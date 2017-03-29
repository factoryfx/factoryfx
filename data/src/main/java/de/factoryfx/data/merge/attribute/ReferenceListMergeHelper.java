package de.factoryfx.data.merge.attribute;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.Attribute;

public class ReferenceListMergeHelper<T extends Data> extends AttributeMergeHelper<List<T>> {

    public ReferenceListMergeHelper(Attribute<List<T>> attribute) {
        super(attribute);
    }

    @Override
    public boolean hasConflict(Attribute<?> originalValue, Attribute<?> newValue)  {
        return false;
    }

    @Override
    public boolean isMergeable(Attribute<?> originalValue, Attribute<?> newValue) {
        if (attribute.internal_match(newValue)) {
            return false ;
        }
        return true;
    }

    @Override
    public void mergeTyped(List<T> originalList, List<T> newList) {
        List<T> currentToEditList = attribute.get();
        if (originalList==null) {
            originalList =new ArrayList<>();
        }

        Set<String> currentMap = new HashSet<>();
        Set<String> originalMap = new HashSet<>();
        Set<String> newMap = new HashSet<>();

        LinkedHashMap<String, T> allMap = new LinkedHashMap<>();

        for (T base : originalList) {
            originalMap.add(base.getId());
            allMap.put(base.getId(), base);
        }
        for (T base : newList) {
            newMap.add(base.getId());
            allMap.put(base.getId(), base);
        }
        for (T base : currentToEditList) {
            currentMap.add(base.getId());
            allMap.put(base.getId(), base);
        }

        currentToEditList.clear();

        for (Map.Entry<String, T> entry : allMap.entrySet()) {
            String id = entry.getKey();
            if (currentMap.contains(id) && originalMap.contains(id) && newMap.contains(id)) {
                currentToEditList.add(entry.getValue());
            }
            if (currentMap.contains(id) && !originalMap.contains(id) && newMap.contains(id)) {
                currentToEditList.add(entry.getValue());
            }
            if (currentMap.contains(id) && !originalMap.contains(id) && !newMap.contains(id)) {
                currentToEditList.add(entry.getValue());
            }
            if (!currentMap.contains(id) && !originalMap.contains(id) && newMap.contains(id)) {
                currentToEditList.add(entry.getValue());
            }
        }

    }


}
