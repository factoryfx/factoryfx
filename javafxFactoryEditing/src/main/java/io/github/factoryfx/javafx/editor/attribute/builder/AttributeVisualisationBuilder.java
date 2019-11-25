package io.github.factoryfx.javafx.editor.attribute.builder;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.metadata.AttributeMetadata;
import io.github.factoryfx.javafx.editor.attribute.AttributeVisualisation;

import java.util.function.Consumer;

public interface AttributeVisualisationBuilder {
    default boolean isListItemEditorFor(Attribute<?,?> attribute){
        return false;
    }
    boolean isEditorFor(Attribute<?,?> attribute);
    AttributeVisualisation createVisualisation(Attribute<?,?> attribute, AttributeMetadata attributeMetadata, Consumer<FactoryBase<?,?>> navigateToData, FactoryBase<?,?> previousData);
    default AttributeVisualisation createValueListVisualisation(Attribute<?,?> attribute, AttributeMetadata attributeMetadata) { return null;}
}
