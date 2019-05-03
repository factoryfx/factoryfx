package io.github.factoryfx.javafx.javascript.editor.attribute.visualisation;


import io.github.factoryfx.javafx.editor.attribute.ValidationDecoration;
import io.github.factoryfx.javafx.editor.attribute.ValueAttributeVisualisation;
import io.github.factoryfx.javascript.data.attributes.types.JavascriptAttribute;
import io.github.factoryfx.javascript.data.attributes.types.Javascript;
import javafx.application.Platform;
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
        this.attribute.internal_setRunlaterExecutor(Platform::runLater);
    }

    @Override
    public Node createValueVisualisation() {
        JavascriptVisual<A> visual = new JavascriptVisual<>(attribute.internal_getExterns());
        return visual.createContent(observableAttributeValue);
    }


}
