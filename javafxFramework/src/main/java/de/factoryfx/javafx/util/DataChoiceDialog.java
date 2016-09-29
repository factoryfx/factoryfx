package de.factoryfx.javafx.util;

import java.util.List;

import de.factoryfx.data.Data;
import javafx.scene.control.ChoiceDialog;

public class DataChoiceDialog {
    public Data show(List<Data> possibleValues){
        ChoiceDialog<Data> choiceDialog = new ChoiceDialog<>();
        choiceDialog.getItems().addAll(possibleValues);
        choiceDialog.showAndWait();
        return choiceDialog.getSelectedItem();
    }
}
