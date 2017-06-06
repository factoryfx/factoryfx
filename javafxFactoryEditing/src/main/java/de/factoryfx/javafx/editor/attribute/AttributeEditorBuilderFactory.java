package de.factoryfx.javafx.editor.attribute;

import java.util.List;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ValueListAttribute;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.javafx.editor.attribute.builder.SingleAttributeEditorBuilder;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.util.UniformDesignFactory;

public class AttributeEditorBuilderFactory<V> extends SimpleFactoryBase<AttributeEditorBuilder,V> {
    public final FactoryReferenceAttribute<UniformDesign,UniformDesignFactory<V>> uniformDesign = new FactoryReferenceAttribute<>(new AttributeMetadata().de("uniformDesign").en("uniformDesign"),UniformDesignFactory.class);
    public final ValueListAttribute<SingleAttributeEditorBuilder<?>> additionalAttributeEditorBuilders = new ValueListAttribute<>(new AttributeMetadata().en("editorAssociations"),SingleAttributeEditorBuilder.class);

    @Override
    public AttributeEditorBuilder createImpl() {
        List<SingleAttributeEditorBuilder<?>> defaultSingleAttributeEditorBuilders = AttributeEditorBuilder.createDefaultSingleAttributeEditorBuilders(uniformDesign.instance());
        defaultSingleAttributeEditorBuilders.addAll(0,additionalAttributeEditorBuilders.get());
        return new AttributeEditorBuilder(uniformDesign.instance(), defaultSingleAttributeEditorBuilders);
    }
}
