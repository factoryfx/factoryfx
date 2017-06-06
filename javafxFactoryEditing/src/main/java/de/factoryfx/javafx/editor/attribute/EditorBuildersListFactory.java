package de.factoryfx.javafx.editor.attribute;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.javafx.editor.attribute.builder.SingleAttributeEditorBuilder;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.util.UniformDesignFactory;

import java.util.List;

public class EditorBuildersListFactory<V> extends SimpleFactoryBase<List<SingleAttributeEditorBuilder<?>>,V> {
    public final FactoryReferenceAttribute<UniformDesign, UniformDesignFactory<V>> uniformDesign = new FactoryReferenceAttribute<>((new AttributeMetadata()).de("uniformDesign").en("uniformDesign"), UniformDesignFactory.class);

    @Override
    public List<SingleAttributeEditorBuilder<?>> createImpl() {
        return AttributeEditorBuilder.createDefaultSingleAttributeEditorBuilders(uniformDesign.instance());
    }
}
