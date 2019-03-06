package de.factoryfx.javafx.data.util;

import java.util.function.Consumer;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Skin;

import javafx.scene.control.SkinBase;
import org.controlsfx.control.CheckComboBox;


public class CheckComboBoxHelper {

    //workaround: http://stackoverflow.com/questions/25177523/how-to-listen-to-open-close-events-of-a-checkcombobox
    public static <T> void addOpenCloseListener(CheckComboBox<T> comboBox, Consumer<CheckComboBox<T>> listener){
        comboBox.skinProperty().addListener((ChangeListener<Skin>) (skinObs, oldVal, newVal) -> {
            if (oldVal == null && newVal != null) {
                SkinBase skin = (SkinBase) newVal;
                ComboBox combo = (ComboBox) skin.getChildren().get(0);
                combo.showingProperty().addListener((obs, hidden, showing) -> listener.accept(comboBox));
            }
        });
    }

}
