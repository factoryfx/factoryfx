package io.github.factoryfx.javafx.editor.attribute.visualisation;

import java.util.Collection;
import java.util.function.Supplier;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.dependency.ReferenceBaseAttribute;
import io.github.factoryfx.javafx.editor.attribute.ValidationDecoration;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;

import io.github.factoryfx.javafx.editor.attribute.ValueAttributeVisualisation;

public class CatalogAttributeVisualisation<T extends FactoryBase<?,?>, A extends ReferenceBaseAttribute<T,T,A>> extends ValueAttributeVisualisation<T, A> {
    private final Supplier<Collection<T>> possibleValuesProvider;
    private ComboBox<T> comboBox;

    public CatalogAttributeVisualisation(Supplier<Collection<T>> possibleValuesProvider, A attribute, ValidationDecoration validationDecoration) {
        super(attribute, validationDecoration);
        this.possibleValuesProvider = possibleValuesProvider;
    }

    @Override
    public Node createValueVisualisation() {

        comboBox = new ComboBox<>();
        comboBox.getItems().add(0, null);
        comboBox.getItems().addAll(possibleValuesProvider.get());
        comboBox.valueProperty().bindBidirectional(observableAttributeValue);

        comboBox.setEditable(false);
        comboBox.disableProperty().bind(readOnly);

        comboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(T object) {
                return object == null ? "" : object.internal().getDisplayText();
            }

            @Override
            public T fromString(String string) {
                throw new UnsupportedOperationException();
            }
        });
        comboBox.setMinWidth(300);
        return comboBox;
    }
}
