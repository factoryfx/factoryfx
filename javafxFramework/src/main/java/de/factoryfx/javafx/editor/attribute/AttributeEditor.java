package de.factoryfx.javafx.editor.attribute;

import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.AttributeChangeListener;
import de.factoryfx.javafx.widget.Widget;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;

public class AttributeEditor<T> implements Widget {

    public final AttributeEditorVisualisation<T> attributeEditorVisualisation;

    private SimpleObjectProperty<T> bound = new SimpleObjectProperty<>();
    private Attribute<T> boundAttribute;

    public AttributeEditor(AttributeEditorVisualisation<T> attributeEditorVisualisation) {
        this.attributeEditorVisualisation=attributeEditorVisualisation;

        bound.addListener((observable, oldValue, newValue1) -> {
            boundAttribute.set(newValue1);
        });
    }

    private AttributeChangeListener<T> attributeChangeListener = (attribute, value) -> {
        bound.set(boundAttribute.get());
    };

    @SuppressWarnings("unchecked")
    public void bindAnyway(Attribute<?> newAttribute) {
        bind((Attribute<T>)newAttribute);
    }

    public void bind(Attribute<T> newAttribute) {
        if (boundAttribute!=null){
            boundAttribute.removeListener(attributeChangeListener);
        }
        boundAttribute=newAttribute;

        bound.set(newAttribute.get());
        newAttribute.addListener(attributeChangeListener);
    }

    @Override
    public Node createContent() {
        return attributeEditorVisualisation.createContent(bound);
    }

}
