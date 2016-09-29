package de.factoryfx.javafx.editor.attribute;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ReferenceAttribute;
import de.factoryfx.data.attribute.ReferenceListAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.javafx.editor.attribute.visualisation.BigDecimalAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.BooleanAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.DoubleAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.EnumAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.IntegerAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.ListAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.LongAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.ReferenceAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.ReferenceListAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.StringAttributeVisualisation;
import de.factoryfx.javafx.editor.data.DataEditor;
import de.factoryfx.javafx.util.UniformDesign;
import javafx.collections.ObservableList;

public class AttributeEditorFactory {
    private final UniformDesign uniformDesign;
    private final Data root;

    public AttributeEditorFactory(UniformDesign uniformDesign, Data root) {
        this.uniformDesign = uniformDesign;
        this.root = root;
    }

    List<Function<Attribute<?>,Optional<AttributeEditor<?>>>> editorAssociations=new ArrayList<>();
    public void addEditorAssociation(Function<Attribute<?>,Optional<AttributeEditor<?>>> editorAssociation){
        editorAssociations.add(editorAssociation);
    }

    @SuppressWarnings("unchecked")
    public Optional<AttributeEditor<?>> getAttributeEditor(Attribute<?> attribute, DataEditor dataEditor){

        if (String.class==attribute.getAttributeType().dataType){
            return Optional.of(new AttributeEditor<>((Attribute<String>)attribute,new StringAttributeVisualisation()));
        }

        if (Integer.class==attribute.getAttributeType().dataType){
            return Optional.of(new AttributeEditor<>((Attribute<Integer>)attribute,new IntegerAttributeVisualisation()));
        }

        if (Boolean.class==attribute.getAttributeType().dataType){
            return Optional.of(new AttributeEditor<>((Attribute<Boolean>)attribute,new BooleanAttributeVisualisation()));
        }

        if (Enum.class.isAssignableFrom(attribute.getAttributeType().dataType)){
            Attribute<Enum> enumAttribute = (Attribute<Enum>) attribute;
            List<Enum> enumConstants = Arrays.asList((Enum[]) enumAttribute.getAttributeType().dataType.getEnumConstants());
            return Optional.of(new AttributeEditor<>(enumAttribute,new EnumAttributeVisualisation(enumConstants)));
        }

        if (Long.class==attribute.getAttributeType().dataType){
            return Optional.of(new AttributeEditor<>((Attribute<Long>)attribute,new LongAttributeVisualisation()));
        }

        if (BigDecimal.class==attribute.getAttributeType().dataType){
            return Optional.of(new AttributeEditor<>((Attribute<BigDecimal>)attribute,new BigDecimalAttributeVisualisation()));
        }

        if (Double.class==attribute.getAttributeType().dataType){
            return Optional.of(new AttributeEditor<>((Attribute<Double>)attribute,new DoubleAttributeVisualisation()));
        }

        if (Data.class==attribute.getAttributeType().dataType){
            return Optional.of(new AttributeEditor<>((Attribute<Data>)attribute,new ReferenceAttributeVisualisation(uniformDesign,dataEditor,()->((ReferenceAttribute<?>)attribute).addNewFactory(root))));
        }

        if (ObservableList.class.isAssignableFrom(attribute.getAttributeType().dataType) && String.class.isAssignableFrom(attribute.getAttributeType().listItemType)){
            StringAttribute detailAttribute = new StringAttribute(new AttributeMetadata());
            AttributeEditor<String> attributeEditor = (AttributeEditor<String>) getAttributeEditor(detailAttribute,dataEditor).get();
            return Optional.of(new AttributeEditor<>((Attribute<ObservableList<String>>)attribute,new ListAttributeVisualisation<>(uniformDesign, detailAttribute, attributeEditor)));
        }

        if (ObservableList.class.isAssignableFrom(attribute.getAttributeType().dataType) && Data.class.isAssignableFrom(attribute.getAttributeType().listItemType)){
            ReferenceListAttribute<?> referenceListAttribute = (ReferenceListAttribute<?>) attribute;
            return Optional.of(new AttributeEditor<>((Attribute<ObservableList<Data>>)attribute,new ReferenceListAttributeVisualisation(uniformDesign, dataEditor, () -> referenceListAttribute.addNewFactory(root), null)));
        }

        for (Function<Attribute<?>,Optional<AttributeEditor<?>>> editorAssociation: editorAssociations) {
            Optional<AttributeEditor<?>> attributeEditor = editorAssociation.apply(attribute);
            if (attributeEditor.isPresent()) {
                return attributeEditor;
            }
        }

        return Optional.empty();
    }

}