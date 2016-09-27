package de.factoryfx.javafx.editor.attribute;

import java.math.BigDecimal;
import java.util.Optional;

import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.javafx.editor.attribute.visualisation.BigDecimalAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.BooleanAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.DoubleAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.EnumAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.IntegerAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.ListAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.LongAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.StringAttributeVisualisation;
import de.factoryfx.javafx.util.UniformDesign;
import javafx.collections.ObservableList;

public class AttributeEditorFactory {
    private final UniformDesign uniformDesign;

    public AttributeEditorFactory(UniformDesign uniformDesign) {
        this.uniformDesign = uniformDesign;
    }

    @SuppressWarnings("unchecked")
    public Optional<AttributeEditor<?>> getAttributeEditor(Attribute<?> attribute){

        if (String.class.isAssignableFrom(attribute.getAttributeType().dataType)){
            return Optional.of(new AttributeEditor<>((Attribute<String>)attribute,new StringAttributeVisualisation()));
        }

        if (Integer.class.isAssignableFrom(attribute.getAttributeType().dataType)){
            return Optional.of(new AttributeEditor<>((Attribute<Integer>)attribute,new IntegerAttributeVisualisation()));
        }

        if (Boolean.class.isAssignableFrom(attribute.getAttributeType().dataType)){
            return Optional.of(new AttributeEditor<>((Attribute<Boolean>)attribute,new BooleanAttributeVisualisation()));
        }

        if (Enum.class.isAssignableFrom(attribute.getAttributeType().dataType)){
            return Optional.of(new AttributeEditor<>((Attribute<Enum>)attribute,new EnumAttributeVisualisation()));
        }

        if (Long.class.isAssignableFrom(attribute.getAttributeType().dataType)){
            return Optional.of(new AttributeEditor<>((Attribute<Long>)attribute,new LongAttributeVisualisation()));
        }

        if (BigDecimal.class.isAssignableFrom(attribute.getAttributeType().dataType)){
            return Optional.of(new AttributeEditor<>((Attribute<BigDecimal>)attribute,new BigDecimalAttributeVisualisation()));
        }

        if (Double.class.isAssignableFrom(attribute.getAttributeType().dataType)){
            return Optional.of(new AttributeEditor<>((Attribute<Double>)attribute,new DoubleAttributeVisualisation()));
        }

        if (ObservableList.class.isAssignableFrom(attribute.getAttributeType().dataType) && String.class.isAssignableFrom(attribute.getAttributeType().listItemType)){
            StringAttribute detailAttribute = new StringAttribute(new AttributeMetadata());
            AttributeEditor<String> attributeEditor = (AttributeEditor<String>) getAttributeEditor(detailAttribute).get();
            return Optional.of(new AttributeEditor<>((Attribute<ObservableList<String>>)attribute,new ListAttributeVisualisation<>(uniformDesign, detailAttribute, attributeEditor)));
        }



        return Optional.empty();
    }

}
