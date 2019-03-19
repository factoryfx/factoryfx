package io.github.factoryfx.javafx.factory.editor;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import io.github.factoryfx.javafx.data.editor.attribute.AttributeVisualisationMappingBuilder;
import io.github.factoryfx.javafx.factory.RichClientRoot;
import io.github.factoryfx.javafx.factory.editor.attribute.AttributeEditorBuilderFactory;
import io.github.factoryfx.javafx.data.editor.data.DataEditor;
import io.github.factoryfx.javafx.data.util.UniformDesign;
import io.github.factoryfx.javafx.factory.util.UniformDesignFactory;

public class DataEditorFactory extends SimpleFactoryBase<DataEditor,RichClientRoot> {

    public final FactoryReferenceAttribute<UniformDesign,UniformDesignFactory> uniformDesign = new FactoryReferenceAttribute<>(UniformDesignFactory.class).de("uniformDesign").en("uniformDesign");
    public final FactoryReferenceAttribute<AttributeVisualisationMappingBuilder, AttributeEditorBuilderFactory> attributeEditorBuilder = new FactoryReferenceAttribute<>(AttributeEditorBuilderFactory.class).de("uniformDesign").en("uniformDesign");

    @Override
    public DataEditor createImpl() {
        return new DataEditor(attributeEditorBuilder.instance(),uniformDesign.instance());
    }
}