package de.factoryfx.javafx.factory.widget.factory.diffdialog;

import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.javafx.data.editor.attribute.AttributeVisualisationMappingBuilder;
import de.factoryfx.javafx.factory.RichClientRoot;
import de.factoryfx.javafx.factory.editor.attribute.AttributeEditorBuilderFactory;
import de.factoryfx.javafx.data.util.UniformDesign;
import de.factoryfx.javafx.factory.util.UniformDesignFactory;

public class DiffDialogBuilderFactory extends SimpleFactoryBase<DiffDialogBuilder,RichClientRoot> {
    public final FactoryReferenceAttribute<UniformDesign,UniformDesignFactory> uniformDesign = new FactoryReferenceAttribute<>(UniformDesignFactory.class).de("uniformDesign").en("uniformDesign");
    public final FactoryReferenceAttribute<AttributeVisualisationMappingBuilder,AttributeEditorBuilderFactory> attributeEditorBuilder = new FactoryReferenceAttribute<>(AttributeEditorBuilderFactory.class).de("attributeEditorBuilder").en("attributeEditorBuilder");


    @Override
    public DiffDialogBuilder createImpl() {
        return new DiffDialogBuilder(uniformDesign.instance(),attributeEditorBuilder.instance());
    }
}