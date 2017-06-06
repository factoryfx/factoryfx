package de.factoryfx.javafx.editor.attribute;

import java.util.List;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ValueListAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;
import de.factoryfx.javafx.editor.attribute.builder.SingleAttributeEditorBuilder;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.util.UniformDesignFactory;

public class AttributeEditorBuilderFactory<V> extends SimpleFactoryBase<AttributeEditorBuilder,V> {
    public final FactoryReferenceAttribute<UniformDesign,UniformDesignFactory<V>> uniformDesign = new FactoryReferenceAttribute<>(new AttributeMetadata().de("uniformDesign").en("uniformDesign"),UniformDesignFactory.class);
    public final FactoryReferenceAttribute<List<SingleAttributeEditorBuilder<?>>,FactoryBase<? extends List<SingleAttributeEditorBuilder<?>>,V>> editorBuildersList = new FactoryReferenceAttribute<>(new AttributeMetadata().en("editorAssociations"),SingleAttributeEditorBuilder.class);

    @Override
    public AttributeEditorBuilder createImpl() {
        return new AttributeEditorBuilder(uniformDesign.instance(), editorBuildersList.instance());
    }

}
