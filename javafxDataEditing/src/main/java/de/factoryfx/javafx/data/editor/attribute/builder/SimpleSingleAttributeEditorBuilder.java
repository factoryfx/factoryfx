package de.factoryfx.javafx.data.editor.attribute.builder;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.ValueListAttribute;
import de.factoryfx.javafx.data.editor.attribute.visualisation.ExpandableAttributeVisualisation;
import de.factoryfx.javafx.data.editor.attribute.visualisation.ValueListAttributeVisualisation;
import de.factoryfx.javafx.data.editor.attribute.AttributeEditor;
import de.factoryfx.javafx.data.editor.attribute.AttributeEditorVisualisation;
import de.factoryfx.javafx.data.util.UniformDesign;
import org.controlsfx.glyphfont.FontAwesome;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class SimpleSingleAttributeEditorBuilder<T,A extends Attribute<T,A>> implements SingleAttributeEditorBuilder<T> {

    private final Predicate<Attribute<?,?>> isEditorFor;
    private final Predicate<Attribute<?,?>> isListItemEditorFor;
    private final Function<A,AttributeEditorVisualisation<T>> attributeEditorVisualisationCreator;
    private final UniformDesign uniformDesign;
    private final Supplier<A> attributeCreator;

    public SimpleSingleAttributeEditorBuilder(UniformDesign uniformDesign, Class<A> attributeClazz, Class<T> typeClazz, Function<A,AttributeEditorVisualisation<T>> attributeEditorVisualisationCreator, Supplier<A> attributeCreator) {
        this(uniformDesign,(a)->attributeClazz==a.getClass(),(a)->a.internal_getAttributeType().listItemType==typeClazz,attributeEditorVisualisationCreator,attributeCreator);
    }

    public SimpleSingleAttributeEditorBuilder(UniformDesign uniformDesign, Predicate<Attribute<?,?>> isEditorFor, Predicate<Attribute<?,?>> isListItemEditorFor, Function<A,AttributeEditorVisualisation<T>> attributeEditorVisualisationCreator, Supplier<A> attributeCreator) {
        this.isEditorFor=isEditorFor;
        this.attributeEditorVisualisationCreator= attributeEditorVisualisationCreator;
        this.isListItemEditorFor=isListItemEditorFor;
        this.uniformDesign = uniformDesign;
        this.attributeCreator=attributeCreator;
    }

    @Override
    public boolean isListItemEditorFor(Attribute<?,?> attribute) {
        return isListItemEditorFor.test(attribute);
    }

    @Override
    public boolean isEditorFor(Attribute<?,?> attribute) {
        return isEditorFor.test(attribute);
    }

    @Override
    @SuppressWarnings("unchecked")
    public AttributeEditor<T,A> createEditor(Attribute<?,?> attribute, Consumer<Data> navigateToData, Data previousData) {
        A attributeTyped = (A) attribute;
        return new AttributeEditor<>(attributeTyped, attributeEditorVisualisationCreator.apply(attributeTyped), uniformDesign);
    }

    @Override
    @SuppressWarnings("unchecked")
    public AttributeEditor<List<T>,?> createValueListEditor(Attribute<?,?> attribute) {
        A detailAttribute = attributeCreator.get();
        ExpandableAttributeVisualisation listExpandableAttributeVisualisation = new ExpandableAttributeVisualisation(
                new ValueListAttributeVisualisation(
                    uniformDesign,
                    detailAttribute,
                    createEditor(detailAttribute,null,null),
                    (ValueListAttribute)attribute),
                uniformDesign,
                (l) -> "Items: " + ((Collection)l).size(),
                FontAwesome.Glyph.LIST);
        return new AttributeEditor(attribute, listExpandableAttributeVisualisation,uniformDesign);
    }

}
