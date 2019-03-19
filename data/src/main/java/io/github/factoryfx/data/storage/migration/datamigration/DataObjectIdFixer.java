package io.github.factoryfx.data.storage.migration.datamigration;

import java.util.HashMap;
import java.util.Map;

public class DataObjectIdFixer {
    protected Map<String,DataJsonNode> items = new HashMap<>();

    public DataObjectIdFixer() { }


    public void bindItem(String id, DataJsonNode ob) {
        items.put(id, ob);
    }

    public boolean isResolvable(String id) {
        return items.containsKey(id);
    }
}