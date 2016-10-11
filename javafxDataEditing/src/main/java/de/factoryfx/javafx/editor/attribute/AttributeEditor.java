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

    private SimpleObjectProperty<T> bound = new SimpleObjectProperty<>();
    private Attribute<T> boundAttribute;
    private SimpleObjectProperty<List<ValidationError>> validationResult=new SimpleObjectProperty<>();

    public AttributeEditor(Attribute<T> boundAttribute, AttributeEditorVisualisation<T> attributeEditorVisualisation) {
        this.boundAttribute=boundAttribute;
        this.attributeEditorVisualisation=attributeEditorVisualisation;

        bound.addListener((observable, oldValue, newValue1) -> {
            if (!setLoop){

                boundAttribute.set(newValue1);
            }
        });

        bound.set(boundAttribute.get());
        boundAttribute.addListener(attributeChangeListener);
    }

    boolean setLoop=false;
    private AttributeChangeListener<T> attributeChangeListener = (attribute, value) -> {
        setLoop=true;
        if (value==bound.get()){
            //workaround to force changelistener to trigger
            //same ref doesn't mean the content didn't chnage e.g List
            bound.set(null);
            bound.set(value);
        }
        bound.set(value);
        setLoop=false;

        validationResult.set(attribute.validate());
    };

    Node content;
    @Override
    public Node createContent() {
        if (content==null){
            content = addValidationDecoration(attributeEditorVisualisation.createContent(bound));
        }
        return content;
    }

    public void unbind() {
        content=null;
        boundAttribute.removeListener(attributeChangeListener);
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
