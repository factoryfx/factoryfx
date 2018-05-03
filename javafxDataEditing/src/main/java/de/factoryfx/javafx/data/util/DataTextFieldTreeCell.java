package de.factoryfx.javafx.data.util;

import java.util.function.Function;

import de.factoryfx.data.Data;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.scene.control.cell.TextFieldTreeCell;

/** treeview cell for data with auto updatable displaytext*/
public class DataTextFieldTreeCell<T> extends TextFieldTreeCell<T> {

    private WeakChangeListener<String> changeListener;
    private ReadOnlyStringProperty displayText;
    private ChangeListener<String> changeListenerGarbageCollectionSave;
    private final Function<T,Data> dataSupplier;
    private final Function<T,String> alternativeDisplayText;

    public DataTextFieldTreeCell(Function<T,Data> dataSupplier, Function<T,String> alternativeDisplayText){
        this.dataSupplier = dataSupplier;
        this.alternativeDisplayText = alternativeDisplayText;
    }

    @Override
    public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        if (item != null) {
            if (dataSupplier.apply(item)!=null){
                if (displayText!=null){
                    displayText.removeListener(changeListener);
                }

                displayText= new DataObservableDisplayText(dataSupplier.apply(item)).get();
                changeListenerGarbageCollectionSave = (observable, oldValue, newValue) -> {
                    setText(dataSupplier.apply(item).internal().getDisplayText());
                };
                changeListener = new WeakChangeListener<>(changeListenerGarbageCollectionSave);
                displayText.addListener(changeListener);
                changeListener.changed(displayText,null,displayText.get());
            } else {
                setText(alternativeDisplayText.apply(item));
            }

        }
        //CellUtils.updateItem(this, getConverter(), hbox, getTreeItemGraphic(), textField);
    }

}
