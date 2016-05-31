package de.factoryfx.richclient.framework.editor.editors;

import java.text.DecimalFormat;

import com.google.common.base.Strings;
import de.factoryfx.factory.attribute.IntegerAttribute;
import de.factoryfx.richclient.framework.editor.AttributeEditor;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.NumberStringConverter;

public class IntegerEditor extends AttributeEditor<Number,IntegerAttribute> {
    public IntegerEditor() {
        super(Number.class);
    }

    @Override
    public Node createContent() {
        TextField textField = new TextField();
        setupIntegerTextField(textField);
        textField.textProperty().bindBidirectional(boundTo, new NumberStringConverter(new DecimalFormat("0")));
        textField.disableProperty().bind(disabledProperty());
        return textField;
    }

    private void setupIntegerTextField(TextField textField){
        textField.setTextFormatter(new TextFormatter<Integer>(change -> {
            try{
                if (!Strings.isNullOrEmpty(change.getControlNewText())){
                    Integer.valueOf(change.getControlNewText());
                }
                return change;
            } catch (/*NumberFormatException | */IllegalArgumentException e){ //javafxbug https://bugs.openjdk.java.net/browse/JDK-8081700
                return null;
            }
        }));
    }
}
