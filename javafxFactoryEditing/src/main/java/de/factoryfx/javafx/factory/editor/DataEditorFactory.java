package de.factoryfx.javafx.factory.editor;

import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.javafx.data.editor.attribute.AttributeVisualisationMappingBuilder;
import de.factoryfx.javafx.factory.RichClientRoot;
import de.factoryfx.javafx.factory.editor.attribute.AttributeEditorBuilderFactory;
import de.factoryfx.javafx.data.editor.data.DataEditor;
import de.factoryfx.javafx.data.util.UniformDesign;
import de.factoryfx.javafx.factory.util.UniformDesignFactory;

public class DataEditorFactory extends SimpleFactoryBase<DataEditor,RichClientRoot> {

    public final FactoryReferenceAttribute<UniformDesign,UniformDesignFactory> uniformDesign = new FactoryReferenceAttribute<>(UniformDesignFactory.class).de("uniformDesign").en("uniformDesign");
    public final FactoryReferenceAttribute<AttributeVisualisationMappingBuilder,AttributeEditorBuilderFactory> attributeEditorBuilder = new FactoryReferenceAttribute<>(AttributeEditorBuilderFactory.class).de("uniformDesign").en("uniformDesign");

    @Override
    public DataEditor createImpl() {
        return new DataEditor(attributeEditorBuilder.instance(),uniformDesign.instance());
    }
}