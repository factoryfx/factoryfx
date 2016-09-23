package de.factoryfx.javafx.editor.attribute;

import java.util.Optional;

import de.factoryfx.data.attribute.Attribute;

public class AttributeEditorFactory {


    public Optional<AttributeEditor<?>> getAttributeEditor(Attribute<?> attribute){

        System.out.println(attribute.getClass());
        if (String.class.isAssignableFrom(attribute.getAttributeType().dataType)){
            return Optional.of(new AttributeEditor<>(new StringAttributeVisualisation()));
        }

        return Optional.empty();
    }

}
