package de.factoryfx.javafx.data.editor.attribute;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;

import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.ReferenceListAttribute;
import de.factoryfx.data.attribute.ValueListAttribute;

public abstract class ListAttributeEditorVisualisation<T> implements AttributeEditorVisualisation<List<T>> {
    private ObservableList<T> attributeValue= FXCollections.observableArrayList();

    public ListAttributeEditorVisualisation() { }

    @Override
    @SuppressWarnings("unchecked")
    public void init(Attribute<List<T>,?> boundAttribute) {
        if (boundAttribute instanceof ReferenceListAttribute || boundAttribute instanceof ValueListAttribute){
            boundAttribute.internal_addListener((att, val)-> {
                attributeValue.setAll(val);
            });
            this.attributeValue.setAll(boundAttribute.get());
        }
    }

    @Override
    public void attributeValueChanged(List<T> newValue) {
        //nothing
    }

    @Override
    public Node createVisualisation() {
        return createContent(attributeValue,false);
    }

    @Override
    public Node createReadOnlyVisualisation() {
        return createContent(attributeValue, true);
    }

    /**
     * @param readOnlyList changes to list do not update the attribute
     * @param readonly flag for readonly mode
     * @return javafx node
     */
    public abstract Node createContent(ObservableList<T> readOnlyList, boolean readonly);

}
