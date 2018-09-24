package de.factoryfx.javafx.data.widget.dataview;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class ReferenceAttributeDataView<T extends Data, A extends ReferenceBaseAttribute<T,List<T>,A>> implements DataView<T>{

    private final ReferenceListAttribute<T,A> refList;
    private final AttributeChangeListener<List<T>, A> listAttributeChangeListener;
    private final ObservableList<T> list = FXCollections.observableArrayList();

    public ReferenceAttributeDataView(ReferenceListAttribute<T,A> refList) {
        this.refList=refList;
        this.listAttributeChangeListener = (attribute, value) -> list.setAll(value);
        this.refList.internal_addListener(new WeakAttributeChangeListener<>(listAttributeChangeListener));
    }

    @Override
    public ObservableList<T> dataList(){
        list.setAll(refList);
        return list;
    }

}
