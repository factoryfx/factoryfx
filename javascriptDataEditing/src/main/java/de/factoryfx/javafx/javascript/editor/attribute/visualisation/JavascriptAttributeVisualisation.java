package de.factoryfx.javafx.javascript.editor.attribute.visualisation;


import de.factoryfx.javafx.data.editor.attribute.ValueAttributeEditorVisualisation;
import de.factoryfx.javascript.data.attributes.types.JavascriptAttribute;
import de.factoryfx.javascript.data.attributes.types.Javascript;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;

public class JavascriptAttributeVisualisation extends ValueAttributeEditorVisualisation<Javascript<?>> {

    private final JavascriptAttribute<?> attribute;

    public JavascriptAttributeVisualisation(JavascriptAttribute attribute) {
        this.attribute = attribute;
    }

    @Override
    public Node createVisualisation(SimpleObjectProperty<Javascript<?>> boundTo, boolean readonly) {
        JavascriptVisual visual = new JavascriptVisual(attribute.internal_getExterns());
        return visual.createContent(boundTo);
    }


}
