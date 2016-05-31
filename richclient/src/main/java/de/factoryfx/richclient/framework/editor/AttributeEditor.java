package de.factoryfx.richclient.framework.editor;

import de.factoryfx.factory.attribute.Attribute;
import de.factoryfx.factory.attribute.AttributeChangeListener;
import de.factoryfx.richclient.framework.Widget;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;

public abstract class AttributeEditor<T,A extends Attribute<T>> implements Widget {
    protected SimpleObjectProperty<A> boundToAttribute= new SimpleObjectProperty<>();
    protected SimpleObjectProperty<T> boundTo= new SimpleObjectProperty<>();
    protected SimpleBooleanProperty disabledProperty= new SimpleBooleanProperty(true);
    private AttributeChangeListener<T> attributeChangeListener=value -> boundTo.set(value);

    Class<T> clazz;
    public AttributeEditor(Class<T> clazz){
        this.clazz = clazz;
    }

    public void bind(Attribute<?> attribute){
        boundToAttribute.set((A)attribute);
        disabledProperty.set(attribute==null);

        boundToAttribute.get().addListener(attributeChangeListener);
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
