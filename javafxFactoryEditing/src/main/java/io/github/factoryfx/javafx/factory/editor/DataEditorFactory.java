package io.github.factoryfx.javafx.factory.editor;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceAttribute;
import io.github.factoryfx.javafx.factory.RichClientRoot;
import io.github.factoryfx.javafx.factory.editor.attribute.AttributeVisualisationMappingBuilder;
import io.github.factoryfx.javafx.factory.editor.attribute.AttributeEditorBuilderFactory;
import io.github.factoryfx.javafx.factory.editor.data.DataEditor;
import io.github.factoryfx.javafx.factory.util.UniformDesign;
import io.github.factoryfx.javafx.factory.util.UniformDesignFactory;

public class DataEditorFactory extends SimpleFactoryBase<DataEditor, RichClientRoot> {

    public final FactoryReferenceAttribute<RichClientRoot,UniformDesign,UniformDesignFactory> uniformDesign = new FactoryReferenceAttribute<>();
    public final FactoryReferenceAttribute<RichClientRoot,AttributeVisualisationMappingBuilder, AttributeEditorBuilderFactory> attributeEditorBuilder = new FactoryReferenceAttribute<>();

    @Override
    public DataEditor createImpl() {
        return new DataEditor(attributeEditorBuilder.instance(),uniformDesign.instance());
    }
}