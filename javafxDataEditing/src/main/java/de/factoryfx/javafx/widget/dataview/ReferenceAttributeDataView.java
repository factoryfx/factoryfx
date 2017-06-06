package de.factoryfx.javafx.widget.dataview;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.ReferenceListAttribute;
import javafx.collections.ObservableList;

public class ReferenceAttributeDataView<T extends Data> implements DataView<T>{

    private final ReferenceListAttribute<T,?> refList;

    public ReferenceAttributeDataView(ReferenceListAttribute<T,?> refList) {
        this.refList=refList;
    }

    @Override
    public ObservableList<T> dataList(){
        return (ObservableList<T>) refList.get();
    }

}
