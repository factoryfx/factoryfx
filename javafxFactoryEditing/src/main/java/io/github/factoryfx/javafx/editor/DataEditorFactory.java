package io.github.factoryfx.javafx.editor;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.javafx.RichClientRoot;
import io.github.factoryfx.javafx.editor.attribute.AttributeEditorBuilderFactory;
import io.github.factoryfx.javafx.editor.attribute.AttributeVisualisationMappingBuilder;
import io.github.factoryfx.javafx.util.UniformDesign;
import io.github.factoryfx.javafx.util.UniformDesignFactory;

public class DataEditorFactory extends SimpleFactoryBase<DataEditor, RichClientRoot> {

    public final FactoryAttribute<UniformDesign, UniformDesignFactory> uniformDesign = new FactoryAttribute<>();
    public final FactoryAttribute<AttributeVisualisationMappingBuilder, AttributeEditorBuilderFactory> attributeEditorBuilder = new FactoryAttribute<>();

    @Override
    protected DataEditor createImpl() {
        return new DataEditor(attributeEditorBuilder.instance(), uniformDesign.instance());
    }
}