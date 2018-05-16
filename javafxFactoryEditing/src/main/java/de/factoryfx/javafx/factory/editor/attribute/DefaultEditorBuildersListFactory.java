package de.factoryfx.javafx.factory.editor.attribute;

import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.javafx.data.editor.attribute.AttributeEditorBuilder;
import de.factoryfx.javafx.data.editor.attribute.builder.SingleAttributeEditorBuilder;
import de.factoryfx.javafx.data.util.UniformDesign;
import de.factoryfx.javafx.factory.RichClientRoot;
import de.factoryfx.javafx.factory.util.UniformDesignFactory;

import java.util.List;

public class DefaultEditorBuildersListFactory extends SimpleFactoryBase<List<SingleAttributeEditorBuilder<?>>,Void,RichClientRoot> {
    public final FactoryReferenceAttribute<UniformDesign, UniformDesignFactory> uniformDesign = new FactoryReferenceAttribute<>(UniformDesignFactory.class).de("uniformDesign").en("uniformDesign");

    @Override
    public List<SingleAttributeEditorBuilder<?>> createImpl() {
        return AttributeEditorBuilder.createDefaultSingleAttributeEditorBuilders(uniformDesign.instance());
    }
}
