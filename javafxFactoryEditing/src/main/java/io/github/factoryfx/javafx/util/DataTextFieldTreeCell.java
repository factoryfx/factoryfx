package io.github.factoryfx.javafx.util;

import java.util.function.Function;

import io.github.factoryfx.factory.FactoryBase;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.cell.TextFieldTreeCell;

/** treeview cell for data with auto updatable displaytext*/
public class DataTextFieldTreeCell<T> extends TextFieldTreeCell<T> {

    private ObservableValue<String> displayText;
    private final Function<T, FactoryBase<?,?>> dataSupplier;
    private final Function<T,String> alternativeDisplayText;

    public DataTextFieldTreeCell(Function<T,FactoryBase<?,?>> dataSupplier, Function<T,String> alternativeDisplayText){
        this.dataSupplier = dataSupplier;
        this.alternativeDisplayText = alternativeDisplayText;
    }

    @Override
    public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
            if (dataSupplier.apply(item)!=null){
                displayText= new ObservableFactoryDisplayText(dataSupplier.apply(item));
                InvalidationListener invalidationListener = observable -> setText(displayText.getValue());
                displayText.addListener(invalidationListener);
                invalidationListener.invalidated(null);
            } else {
                setText(alternativeDisplayText.apply(item));
            }

        }
    }


}
