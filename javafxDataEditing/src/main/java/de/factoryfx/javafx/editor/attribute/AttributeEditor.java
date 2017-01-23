package de.factoryfx.javafx.editor.attribute;

import java.util.List;
import java.util.Locale;

import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.AttributeChangeListener;
import de.factoryfx.data.validation.ValidationError;
import de.factoryfx.javafx.widget.Widget;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;

public class AttributeEditor<T> implements Widget {

    public final AttributeEditorVisualisation<T> attributeEditorVisualisation;

    private Attribute<T> boundAttribute;
    private SimpleObjectProperty<List<ValidationError>> validationResult=new SimpleObjectProperty<>();


    public AttributeEditor(Attribute<T> boundAttribute, AttributeEditorVisualisation<T> attributeEditorVisualisation) {
        this.boundAttribute=boundAttribute;
        this.attributeEditorVisualisation=attributeEditorVisualisation;
        attributeEditorVisualisation.init(boundAttribute);
    }


    private AttributeChangeListener<T> attributeChangeListener = (attribute, value) -> {
//        Platform.runLater(()-> {
            AttributeEditor.this.attributeEditorVisualisation.attributeValueChanged(value);
//        });
    };

    public void expand() {
        attributeEditorVisualisation.expand();
    }

    public void reportValidation(List<ValidationError> attributeValidationErrors){
        validationResult.set(attributeValidationErrors);
    }

    Node content;
    @Override
    public Node createContent() {
        boundAttribute.internal_addListener(attributeChangeListener);
        attributeChangeListener.changed(boundAttribute,boundAttribute.get());
        if (content==null){
            content = addValidationDecoration(attributeEditorVisualisation.createContent());
            attributeEditorVisualisation.attributeValueChanged(boundAttribute.get());
        }
        return content;
    }

    public void unbind() {
        content=null;
        boundAttribute.internal_removeListener(attributeChangeListener);
    }

    public Node addValidationDecoration(Node node) {

        if (node == null)
            return null;

        ContextMenu validationPopupWorkaround = new ContextMenu();//workaround for missing javax feature https://bugs.openjdk.java.net/browse/JDK-8090477
        validationPopupWorkaround.getStyleClass().add("errorContextMenu");
        MenuItem menuItem = new MenuItem();
        Label validationTest = new Label();
        menuItem.setGraphic(validationTest);
        validationPopupWorkaround.getItems().add(menuItem);

        ChangeListener<List<ValidationError>> changeListener = (observable, oldValue, newValue) -> {
            if (newValue!=null){
                boolean isValid = true;
                StringBuilder validationErrorText = new StringBuilder();
                int counter = 1;
                for (ValidationError validationError : newValue) {
                    validationErrorText.append(counter);
                    validationErrorText.append(": ");
                    validationErrorText.append(validationError.validationDescription.getPreferred(Locale.getDefault()));
                    validationErrorText.append("\n");
                    counter++;
                    isValid=false;
                }

                if (!isValid && !node.isDisabled()) {
                    validationTest.setText(validationErrorText.toString());
                    menuItem.setGraphic(validationTest);
                    if (node.getStyleClass().stream().noneMatch(c -> c.equals("error"))) node.getStyleClass().add("error");
                    node.setOnMouseEntered(event -> {
                        if (!validationPopupWorkaround.isShowing()) {
                            validationPopupWorkaround.show(node, Side.BOTTOM, 0, 0);
                        }
                    });
                    node.setOnMouseExited(event -> validationPopupWorkaround.hide());
                    if (node.isFocused() && !validationPopupWorkaround.isShowing()) {
                        validationPopupWorkaround.show(node, Side.BOTTOM, 0, 0);
                    }
                } else {
                    node.getStyleClass().removeIf(c -> c.equals("error"));
                    node.setOnMouseEntered(null);
                    node.setOnMouseExited(null);
                    validationPopupWorkaround.hide();
                }
            }
        };
        validationResult.addListener(changeListener);
        changeListener.changed(validationResult,null,validationResult.getValue());

        return node;
    }
}
