package de.factoryfx.javafx.editor.attribute.builder;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.javafx.editor.attribute.AttributeEditor;
import de.factoryfx.javafx.editor.attribute.AttributeEditorVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.ExpandableAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.ValueListAttributeVisualisation;
import de.factoryfx.javafx.editor.data.DataEditor;
import de.factoryfx.javafx.util.UniformDesign;
import org.controlsfx.glyphfont.FontAwesome;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class SimpleSingleAttributeEditorBuilder<T,A extends Attribute<T>> implements SingleAttributeEditorBuilder<T> {

    private final Predicate<Attribute<?>> isEditorFor;
    private final Predicate<Attribute<?>> isListItemEditorFor;
    private final Function<A,AttributeEditorVisualisation<T>> attributeEditorVisualisationCreator;
    private final UniformDesign uniformDesign;
    private final Supplier<A> attributeCreator;

    public SimpleSingleAttributeEditorBuilder(UniformDesign uniformDesign, Class<A> attributeClazz, Class<T> typeClazz, Function<A,AttributeEditorVisualisation<T>> attributeEditorVisualisationCreator, Supplier<A> attributeCreator) {
        this(uniformDesign,(a)->attributeClazz==a.getClass(),(a)->a.internal_getAttributeType().listItemType==typeClazz,attributeEditorVisualisationCreator,attributeCreator);
    }

    public SimpleSingleAttributeEditorBuilder(UniformDesign uniformDesign, Predicate<Attribute<?>> isEditorFor, Predicate<Attribute<?>> isListItemEditorFor, Function<A,AttributeEditorVisualisation<T>> attributeEditorVisualisationCreator, Supplier<A> attributeCreator) {
        this.isEditorFor=isEditorFor;
        this.attributeEditorVisualisationCreator= attributeEditorVisualisationCreator;
        this.isListItemEditorFor=isListItemEditorFor;
        this.uniformDesign = uniformDesign;
        this.attributeCreator=attributeCreator;
    }

    @Override
    public boolean isListItemEditorFor(Attribute<?> attribute) {
        return isListItemEditorFor.test(attribute);
    }

    @Override
    public boolean isEditorFor(Attribute<?> attribute) {
        return isEditorFor.test(attribute);
    }

    @Override
    @SuppressWarnings("unchecked")
    public AttributeEditor<T> createEditor(Attribute<?> attribute, DataEditor dataEditor, Data previousData) {
        return new AttributeEditor<>((Attribute<T>) attribute, attributeEditorVisualisationCreator.apply((A) attribute), uniformDesign);
    }

    @Override
    @SuppressWarnings("unchecked")
    public AttributeEditor<List<T>> createValueListEditor(Attribute<?> attribute) {
        A detailAttribute = attributeCreator.get();
        ExpandableAttributeVisualisation<List<T>> listExpandableAttributeVisualisation = new ExpandableAttributeVisualisation<>(new ValueListAttributeVisualisation<>(uniformDesign, detailAttribute, createEditor(detailAttribute,null,null)), uniformDesign, (l) -> "Items: " + l.size(), FontAwesome.Glyph.LIST);
        return new AttributeEditor<>((Attribute<List<T>>)attribute, listExpandableAttributeVisualisation,uniformDesign);
    }

}
