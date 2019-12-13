package io.github.factoryfx.javafx.widget.factory.diffdialog;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.javafx.editor.attribute.AttributeVisualisationMappingBuilder;
import io.github.factoryfx.javafx.RichClientRoot;
import io.github.factoryfx.javafx.editor.attribute.AttributeEditorBuilderFactory;
import io.github.factoryfx.javafx.util.UniformDesign;
import io.github.factoryfx.javafx.util.UniformDesignFactory;

public class DiffDialogBuilderFactory<RS extends FactoryBase<?,RS>> extends SimpleFactoryBase<DiffDialogBuilder<RS>,RichClientRoot> {

    public final FactoryAttribute<UniformDesign,UniformDesignFactory> uniformDesign = new FactoryAttribute<>();
    public final FactoryAttribute<AttributeVisualisationMappingBuilder,AttributeEditorBuilderFactory> attributeEditorBuilder = new FactoryAttribute<>();

    @Override
    protected DiffDialogBuilder<RS> createImpl() {
        return new DiffDialogBuilder<>(uniformDesign.instance(),attributeEditorBuilder.instance());
    }
}