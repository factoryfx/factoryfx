package de.factoryfx.javafx.editor.attribute;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.ObjectValueAttribute;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.util.UniformDesignFactory;

public class AttributeEditorBuilderFactory<V> extends SimpleFactoryBase<AttributeEditorBuilder,V> {
    public final FactoryReferenceAttribute<UniformDesign,UniformDesignFactory<V>> uniformDesign = new FactoryReferenceAttribute<>(new AttributeMetadata().de("uniformDesign").en("uniformDesign"),UniformDesignFactory.class);
    public final ObjectValueAttribute<List<BiFunction<UniformDesign,Attribute<?>,Optional<AttributeEditor<?>>>>> editorAssociations = new ObjectValueAttribute<>(new AttributeMetadata().en("editorAssociations"));

    @Override
    public AttributeEditorBuilder createImpl() {
        return new AttributeEditorBuilder(uniformDesign.instance(),editorAssociations.get());
    }
}
