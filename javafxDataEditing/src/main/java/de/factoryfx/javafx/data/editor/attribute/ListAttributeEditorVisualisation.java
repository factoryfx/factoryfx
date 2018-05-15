package de.factoryfx.javafx.data.editor.attribute;

import de.factoryfx.data.attribute.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public abstract class ListAttributeEditorVisualisation<T> implements AttributeEditorVisualisation<List<T>> {
    private ObservableList<T> attributeValue= FXCollections.observableArrayList();
    private AttributeChangeListener attributeChangeListener;
    private Consumer<Consumer<List<T>>> listModifyingAction;

    public ListAttributeEditorVisualisation() {

    }

    @Override
    @SuppressWarnings("unchecked")
    public void init(Attribute<List<T>,?> boundAttribute) {
        if (boundAttribute instanceof ReferenceListAttribute || boundAttribute instanceof ValueListAttribute){

            attributeChangeListener = (attribute, value) -> {
                attributeValue.setAll((Collection<T>) value);
            };
            boundAttribute.internal_addListener(new WeakAttributeChangeListener(attributeChangeListener));
            this.attributeValue.setAll(boundAttribute.get());

            this.listModifyingAction= listConsumer -> listConsumer.accept(boundAttribute.get());
        }
    }

    @Override
    public void attributeValueChanged(List<T> newValue) {
        //nothing
    }

    @Override
    public Node createVisualisation() {
        return createContent(attributeValue,listModifyingAction,false);
    }

    @Override
    public Node createReadOnlyVisualisation() {
        return createContent(attributeValue,listModifyingAction,true);
    }

    /**
     * @param readOnlyList changes to list do not update the attribute
     * @param listModifyingAction use to modify list, don't change the readOnlyList directly
     * @param readonly flag for readonly mode
     * @return javafx node
     */
    public abstract Node createContent(ObservableList<T> readOnlyList, Consumer<Consumer<List<T>>> listModifyingAction, boolean readonly);

}
