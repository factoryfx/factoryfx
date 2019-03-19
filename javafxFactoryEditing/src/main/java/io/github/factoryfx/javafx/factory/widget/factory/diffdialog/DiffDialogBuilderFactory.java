package io.github.factoryfx.javafx.factory.widget.factory.diffdialog;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import io.github.factoryfx.javafx.data.editor.attribute.AttributeVisualisationMappingBuilder;
import io.github.factoryfx.javafx.factory.RichClientRoot;
import io.github.factoryfx.javafx.factory.editor.attribute.AttributeEditorBuilderFactory;
import io.github.factoryfx.javafx.data.util.UniformDesign;
import io.github.factoryfx.javafx.factory.util.UniformDesignFactory;

public class DiffDialogBuilderFactory extends SimpleFactoryBase<DiffDialogBuilder,RichClientRoot> {
    public final FactoryReferenceAttribute<UniformDesign,UniformDesignFactory> uniformDesign = new FactoryReferenceAttribute<>(UniformDesignFactory.class).de("uniformDesign").en("uniformDesign");
    public final FactoryReferenceAttribute<AttributeVisualisationMappingBuilder,AttributeEditorBuilderFactory> attributeEditorBuilder = new FactoryReferenceAttribute<>(AttributeEditorBuilderFactory.class).de("attributeEditorBuilder").en("attributeEditorBuilder");


    @Override
    public DiffDialogBuilder createImpl() {
        return new DiffDialogBuilder(uniformDesign.instance(),attributeEditorBuilder.instance());
    }
}