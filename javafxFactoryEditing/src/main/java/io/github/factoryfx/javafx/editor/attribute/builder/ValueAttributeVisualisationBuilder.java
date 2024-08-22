package io.github.factoryfx.javafx.editor.attribute.builder;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.controlsfx.glyphfont.FontAwesome;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.attribute.ValueListAttribute;
import io.github.factoryfx.factory.metadata.AttributeMetadata;
import io.github.factoryfx.javafx.editor.attribute.AttributeVisualisation;
import io.github.factoryfx.javafx.editor.attribute.ValidationDecoration;
import io.github.factoryfx.javafx.editor.attribute.visualisation.ExpandableAttributeVisualisation;
import io.github.factoryfx.javafx.editor.attribute.visualisation.ValueListAttributeVisualisation;
import io.github.factoryfx.javafx.util.UniformDesign;

public class ValueAttributeVisualisationBuilder<T, A extends Attribute<T, A>, AL extends Attribute<List<T>, AL>> implements AttributeVisualisationBuilder {

    private final Predicate<Attribute<?, ?>> isEditorFor;
    private final Predicate<Attribute<?, ?>> isListItemEditorFor;
    private final Function<A, AttributeVisualisation> attributeEditorVisualisationCreator;
    private final UniformDesign uniformDesign;
    private final Supplier<A> attributeCreator;

    public ValueAttributeVisualisationBuilder(UniformDesign uniformDesign, Class<A> attributeClazz, Class<T> typeClazz, Function<A, AttributeVisualisation> attributeEditorVisualisationCreator, Supplier<A> attributeCreator) {
        this(uniformDesign,
             (a) -> attributeClazz == a.getClass(),
             (a) -> {
                 if (a instanceof ValueListAttribute<?, ?>) {
                     return ((ValueListAttribute<?, ?>) a).internal_getItemType() == typeClazz;
                 }
                 return false;
             },
             attributeEditorVisualisationCreator,
             attributeCreator);
    }

    public ValueAttributeVisualisationBuilder(UniformDesign uniformDesign, Predicate<Attribute<?, ?>> isEditorFor, Predicate<Attribute<?, ?>> isListItemEditorFor, Function<A, AttributeVisualisation> attributeEditorVisualisationCreator, Supplier<A> attributeCreator) {
        this.isEditorFor = isEditorFor;
        this.attributeEditorVisualisationCreator = attributeEditorVisualisationCreator;
        this.isListItemEditorFor = isListItemEditorFor;
        this.uniformDesign = uniformDesign;
        this.attributeCreator = attributeCreator;
    }

    @Override
    public boolean isListItemEditorFor(Attribute<?, ?> attribute) {
        return isListItemEditorFor.test(attribute);
    }

    @Override
    public boolean isEditorFor(Attribute<?, ?> attribute) {
        return isEditorFor.test(attribute);
    }

    @Override
    @SuppressWarnings("unchecked")
    public AttributeVisualisation createVisualisation(Attribute<?, ?> attribute, AttributeMetadata attributeMetadata, Consumer<FactoryBase<?, ?>> navigateToData, FactoryBase<?, ?> previousData) {
        A attributeTyped = (A) attribute;
        return attributeEditorVisualisationCreator.apply(attributeTyped);
    }

    @Override
    @SuppressWarnings("unchecked")
    public AttributeVisualisation createValueListVisualisation(Attribute<?, ?> attribute, AttributeMetadata attributeMetadata) {
        A detailAttribute = attributeCreator.get();
        return new ExpandableAttributeVisualisation<>(
            new ValueListAttributeVisualisation<>((AL) attribute,
                                                  new ValidationDecoration(uniformDesign),
                                                  uniformDesign,
                                                  detailAttribute,
                                                  createVisualisation(detailAttribute, attributeMetadata, null, null)
            ),
            uniformDesign,
            (l) -> "Items: " + l.size(),
            FontAwesome.Glyph.LIST);
    }

}
