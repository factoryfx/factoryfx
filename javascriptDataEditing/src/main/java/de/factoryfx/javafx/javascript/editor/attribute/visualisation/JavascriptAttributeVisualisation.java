package de.factoryfx.javafx.javascript.editor.attribute.visualisation;


import de.factoryfx.javafx.data.editor.attribute.ValidationDecoration;
import de.factoryfx.javafx.data.editor.attribute.ValueAttributeVisualisation;
import de.factoryfx.javascript.data.attributes.types.JavascriptAttribute;
import de.factoryfx.javascript.data.attributes.types.Javascript;
import javafx.scene.Node;

/**
 *
 * @param <A> api class
 */
public class JavascriptAttributeVisualisation<A> extends ValueAttributeVisualisation<Javascript<A>,JavascriptAttribute<A>> {

    private final JavascriptAttribute<A> attribute;

    public JavascriptAttributeVisualisation(JavascriptAttribute<A> attribute, ValidationDecoration validationDecoration) {
        super(attribute, validationDecoration);
        this.attribute = attribute;
    }

    @Override
    public Node createValueVisualisation() {
        JavascriptVisual<A> visual = new JavascriptVisual<A>(attribute.internal_getExterns());
        return visual.createContent(observableAttributeValue);
    }


}
