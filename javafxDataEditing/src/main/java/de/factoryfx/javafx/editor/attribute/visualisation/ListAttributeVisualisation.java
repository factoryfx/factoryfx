package de.factoryfx.javafx.editor.attribute.visualisation;

import java.util.Optional;
import java.util.function.Consumer;

import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.javafx.editor.attribute.AttributeEditorVisualisation;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;

public abstract class ListAttributeVisualisation<T> implements AttributeEditorVisualisation<ObservableList<T>> {

    @Override
    public void init(Attribute<ObservableList<T>> boundAttribute) {
        boundToList=boundAttribute.get();
    }

    protected ObservableList<T> boundToList=null;
    protected Optional<Consumer<ObservableList<T>>> updater= Optional.empty();
    ListChangeListener<T> listChangeListener;
    @Override
    public void attributeValueChanged(ObservableList<T> newValue) {
        if (listChangeListener==null){
            listChangeListener = change -> updater.ifPresent(c -> c.accept(newValue));
            newValue.addListener(listChangeListener);
        }

        if (newValue!=boundToList){
            if (boundToList!=null){
                boundToList.remove(listChangeListener);
            }
            newValue.addListener(listChangeListener);
        }

        updater.ifPresent(c -> c.accept(newValue));
    }

    @Override
    public abstract Node createContent();
}
