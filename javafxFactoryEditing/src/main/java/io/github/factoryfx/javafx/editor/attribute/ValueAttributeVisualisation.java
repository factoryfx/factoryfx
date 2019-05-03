package io.github.factoryfx.javafx.editor.attribute;

import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.attribute.AttributeChangeListener;
import io.github.factoryfx.factory.attribute.WeakAttributeChangeListener;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;


public abstract class ValueAttributeVisualisation<T, A extends Attribute<T,A>> implements AttributeVisualisation {

    public final SimpleObjectProperty<T> observableAttributeValue = new SimpleObjectProperty<>();
    boolean setLoop=false;

    private final ValidationDecoration validationDecoration;
    private final A boundAttribute;
    private AttributeChangeListener<T,A> attributeChangeListener;

    protected final SimpleBooleanProperty readOnly=new SimpleBooleanProperty(false);


    protected ValueAttributeVisualisation(A boundAttribute, ValidationDecoration validationDecoration) {
        this.validationDecoration = validationDecoration;
        this.boundAttribute = boundAttribute;
        initAttributeValue();
    }
    private void initAttributeValue() {
        attributeChangeListener = (attribute, newAttributeValue) -> {
            setLoop = true;
            observableAttributeValue.set(newAttributeValue);
            setLoop = false;
            validationDecoration.update(boundAttribute.internal_validate(null, ""));
        };
        boundAttribute.internal_addListener(new WeakAttributeChangeListener<>(attributeChangeListener));
        attributeChangeListener.changed(boundAttribute,boundAttribute.get());


        observableAttributeValue.addListener(observable -> {
            if (!setLoop) {
                boundAttribute.set(observableAttributeValue.get());
            }
        });
    }

    public abstract Node createValueVisualisation();

    @Override
    public Node createVisualisation(){
        return validationDecoration.wrap(createValueVisualisation());
    }

    @Override
    public void setReadOnly() {
        readOnly.set(true);
    }

    @Override
    public void destroy(){
        boundAttribute.internal_removeListener(attributeChangeListener);
    }
}
