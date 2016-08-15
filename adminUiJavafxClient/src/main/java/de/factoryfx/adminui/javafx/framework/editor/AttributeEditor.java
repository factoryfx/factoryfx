package de.factoryfx.adminui.javafx.framework.editor;

import de.factoryfx.factory.attribute.Attribute;
import de.factoryfx.factory.attribute.AttributeChangeListener;
import de.factoryfx.adminui.javafx.framework.widget.Widget;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;

public abstract class AttributeEditor<T,A extends Attribute<T,?>> implements Widget {
    protected SimpleObjectProperty<A> boundToAttribute= new SimpleObjectProperty<>();
    protected SimpleObjectProperty<T> boundTo= new SimpleObjectProperty<>();
    protected SimpleBooleanProperty disabledProperty= new SimpleBooleanProperty(true);
    private AttributeChangeListener<T> attributeChangeListener=(attribute, value) -> {
        boundTo.set(value);
    };

    Class<T> clazz;
    public AttributeEditor(Class<T> clazz){
        this.clazz = clazz;

        boundTo.addListener(observable -> {
            if (boundToAttribute.get()!=null){
                boundToAttribute.get().set(boundTo.get());
            }
        });
    }

    @SuppressWarnings("unchecked")
    public void bind(Attribute<?,?> attribute){
        boundToAttribute.set((A)attribute);
        disabledProperty.set(attribute==null);

        boundToAttribute.get().addListener(attributeChangeListener);
        attributeChangeListener.changed(boundToAttribute.get(),boundToAttribute.get().get());
    }

    public boolean canEdit(Object attribute){
        if (attribute==null){
            return false;
        }
        return clazz.isAssignableFrom(attribute.getClass());
    }

    public void unbind(){
        boundToAttribute.get().removeListener(attributeChangeListener);
        boundToAttribute.set(null);
        disabledProperty.set(true);

    }

    protected Node addValidationDecoration(Node node) {
        //TODO
        return node;//validationDecorator.addValidationDecoration(node,validProperty());
    }

    public SimpleBooleanProperty disabledProperty(){
        return disabledProperty;
    }

}
