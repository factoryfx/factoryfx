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
    };

    Node content;
    @Override
    public Node createContent() {
        if (content==null){
            content = attributeEditorVisualisation.createContent(bound);
        }
        return content;
    }

    public void unbind() {
        content=null;
        boundAttribute.removeListener(attributeChangeListener);
    }
}
