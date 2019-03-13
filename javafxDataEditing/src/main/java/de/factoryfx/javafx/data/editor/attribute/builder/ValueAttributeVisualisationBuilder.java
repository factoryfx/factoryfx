package de.factoryfx.javafx.data.editor.attribute.builder;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.ValueListAttribute;
import de.factoryfx.javafx.data.editor.attribute.ValidationDecoration;
import de.factoryfx.javafx.data.editor.attribute.visualisation.ExpandableAttributeVisualisation;
import de.factoryfx.javafx.data.editor.attribute.visualisation.ValueListAttributeVisualisation;
import de.factoryfx.javafx.data.editor.attribute.AttributeVisualisation;
import de.factoryfx.javafx.data.util.UniformDesign;
import org.controlsfx.glyphfont.FontAwesome;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ValueAttributeVisualisationBuilder<T,A extends Attribute<T,A>, AL extends Attribute<List<T>,AL>> implements AttributeVisualisationBuilder {

    private final Predicate<Attribute<?,?>> isEditorFor;
    private final Predicate<Attribute<?,?>> isListItemEditorFor;
    private final Function<A, AttributeVisualisation> attributeEditorVisualisationCreator;
    private final UniformDesign uniformDesign;
    private final Supplier<A> attributeCreator;

    public ValueAttributeVisualisationBuilder(UniformDesign uniformDesign, Class<A> attributeClazz, Class<T> typeClazz, Function<A, AttributeVisualisation> attributeEditorVisualisationCreator, Supplier<A> attributeCreator) {
        this(uniformDesign,(a)->attributeClazz==a.getClass(),(a)->{
            if (a instanceof ValueListAttribute<?,?>){
                return ((ValueListAttribute<?,?>)a).internal_getItemType()==typeClazz;
            }
            return false;
        },attributeEditorVisualisationCreator,attributeCreator);
    }

    public ValueAttributeVisualisationBuilder(UniformDesign uniformDesign, Predicate<Attribute<?,?>> isEditorFor, Predicate<Attribute<?,?>> isListItemEditorFor, Function<A, AttributeVisualisation> attributeEditorVisualisationCreator, Supplier<A> attributeCreator) {
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
    public AttributeVisualisation createVisualisation(Attribute<?,?> attribute, Consumer<Data> navigateToData, Data previousData) {
        A attributeTyped = (A) attribute;
        return attributeEditorVisualisationCreator.apply(attributeTyped);
    }

    @Override
    @SuppressWarnings("unchecked")
    public AttributeVisualisation createValueListVisualisation(Attribute<?,?> attribute) {
        A detailAttribute = attributeCreator.get();
        ExpandableAttributeVisualisation<List<T>,AL> listExpandableAttributeVisualisation = new ExpandableAttributeVisualisation<List<T>,AL>(
                new ValueListAttributeVisualisation(
                        attribute,
                        new ValidationDecoration(uniformDesign),
                        uniformDesign,
                        detailAttribute,
                        createVisualisation(detailAttribute,null,null)
                        ),
                uniformDesign,
                (l) -> "Items: " + l.size(),
                FontAwesome.Glyph.LIST);
        return listExpandableAttributeVisualisation;
    }



}
