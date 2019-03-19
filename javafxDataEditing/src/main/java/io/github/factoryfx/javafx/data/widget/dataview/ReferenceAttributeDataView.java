package io.github.factoryfx.javafx.data.widget.dataview;

import io.github.factoryfx.data.Data;
import io.github.factoryfx.data.attribute.AttributeChangeListener;
import io.github.factoryfx.data.attribute.ReferenceBaseAttribute;
import io.github.factoryfx.data.attribute.ReferenceListAttribute;
import io.github.factoryfx.data.attribute.WeakAttributeChangeListener;
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
