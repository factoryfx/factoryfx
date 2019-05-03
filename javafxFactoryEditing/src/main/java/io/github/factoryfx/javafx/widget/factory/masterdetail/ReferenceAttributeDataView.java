package io.github.factoryfx.javafx.widget.factory.masterdetail;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.AttributeChangeListener;
import io.github.factoryfx.factory.attribute.dependency.FactoryListAttribute;
import io.github.factoryfx.factory.attribute.WeakAttributeChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class ReferenceAttributeDataView<RS extends FactoryBase<?,RS>,L, T extends FactoryBase<L,RS>> implements DataView<T>{

    private final FactoryListAttribute<RS,L,T> refList;
    private final AttributeChangeListener<List<T>, FactoryListAttribute<RS,L,T>> listAttributeChangeListener;
    private final ObservableList<T> list = FXCollections.observableArrayList();

    public ReferenceAttributeDataView(FactoryListAttribute<RS,L,T> refList) {
        this.refList=refList;
        this.listAttributeChangeListener = (attribute, value) -> list.setAll(value);
        this.refList.internal_addListener(new WeakAttributeChangeListener<>(listAttributeChangeListener));
    }

    @Override
    public ObservableList<T> dataList(){
        list.setAll(refList.get());
        return list;
    }

}
