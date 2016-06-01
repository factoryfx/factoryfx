package de.factoryfx.richclient.framework.editor.editors;

import com.google.common.base.Strings;
import de.factoryfx.factory.attribute.IntegerAttribute;
import de.factoryfx.richclient.framework.editor.AttributeEditor;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.IntegerStringConverter;

public class IntegerEditor extends AttributeEditor<Integer,IntegerAttribute> {
    public IntegerEditor() {
        super(Integer.class);
    }

    @Override
    public Node createContent() {
        TextField textField = new TextField();
        setupIntegerTextField(textField);
        textField.textProperty().bindBidirectional(boundTo, new IntegerStringConverter());
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
