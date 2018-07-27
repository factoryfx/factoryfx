package de.factoryfx.javafx.data.util;

import impl.org.controlsfx.skin.CheckComboBoxSkin;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Skin;
import org.controlsfx.control.CheckComboBox;

public class CheckComboBoxHelper {

    //workaround: http://stackoverflow.com/questions/25177523/how-to-listen-to-open-close-events-of-a-checkcombobox
    public static void addOpenCloseListener(CheckComboBox comboBox, Runnable listener){
        comboBox.skinProperty().addListener((ChangeListener<Skin>) (skinObs, oldVal, newVal) -> {
            if (oldVal == null && newVal != null) {
                CheckComboBoxSkin skin = (CheckComboBoxSkin) newVal;
                ComboBox combo = (ComboBox) skin.getChildren().get(0);
                combo.showingProperty().addListener((obs, hidden, showing) -> listener.run());
            }
        });
    }

}
