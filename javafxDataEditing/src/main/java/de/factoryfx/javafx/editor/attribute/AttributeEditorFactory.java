package de.factoryfx.javafx.editor.attribute;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ReferenceAttribute;
import de.factoryfx.data.attribute.ReferenceListAttribute;
import de.factoryfx.data.attribute.types.*;
import de.factoryfx.javafx.editor.attribute.visualisation.*;
import de.factoryfx.javafx.editor.data.DataEditor;
import de.factoryfx.javafx.util.UniformDesign;
import javafx.collections.ObservableList;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

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

        for (Function<Attribute<?>,Optional<AttributeEditor<?>>> editorAssociation: editorAssociations) {
            Optional<AttributeEditor<?>> attributeEditor = editorAssociation.apply(attribute);
            if (attributeEditor.isPresent()) {
                return attributeEditor;
            }
        }

        Optional<AttributeEditor<?>> enumAttribute = getAttributeEditorSimpleType(attribute);
        if (enumAttribute != null) return enumAttribute;

        Optional<AttributeEditor<?>> detailAttribute = getAttributeEditorList(attribute, dataEditor);
        if (detailAttribute != null) return detailAttribute;

        Optional<AttributeEditor<?>> referenceAttribute = getAttributeEditorReference(attribute, dataEditor);
        if (referenceAttribute != null) return referenceAttribute;

        return Optional.empty();
    }

    private Optional<AttributeEditor<?>> getAttributeEditorReference(Attribute<?> attribute, DataEditor dataEditor) {
        if (Data.class==attribute.getAttributeType().dataType){
            ReferenceAttribute<?> referenceAttribute = (ReferenceAttribute<?>) attribute;
            return Optional.of(new AttributeEditor<>((Attribute<Data>)attribute,new ReferenceAttributeVisualisation(uniformDesign,dataEditor,()->referenceAttribute.addNewFactory(root),()->(List<Data>)referenceAttribute.possibleValues(root))));
        }

        if (ObservableList.class.isAssignableFrom(attribute.getAttributeType().dataType) && Data.class.isAssignableFrom(attribute.getAttributeType().listItemType)){
            ReferenceListAttribute<?> referenceListAttribute = (ReferenceListAttribute<?>) attribute;
            return Optional.of(new AttributeEditor<>((Attribute<ObservableList<Data>>)attribute,new ReferenceListAttributeVisualisation(uniformDesign, dataEditor, () -> referenceListAttribute.addNewFactory(root), ()->(List<Data>)referenceListAttribute.possibleValues(root))));
        }
        return null;
    }


    private Optional<AttributeEditor<?>> getAttributeEditorList(Attribute<?> attribute, DataEditor dataEditor) {
        if (ObservableList.class.isAssignableFrom(attribute.getAttributeType().dataType) && String.class==attribute.getAttributeType().listItemType){
            StringAttribute detailAttribute = new StringAttribute(new AttributeMetadata().de("Wert").en("Value"));
            AttributeEditor<String> attributeEditor = (AttributeEditor<String>) getAttributeEditor(detailAttribute,dataEditor).get();
            return Optional.of(new AttributeEditor<>((Attribute<ObservableList<String>>)attribute,new ListAttributeVisualisation<>(uniformDesign, detailAttribute, attributeEditor)));
        }

        if (ObservableList.class.isAssignableFrom(attribute.getAttributeType().dataType) && Integer.class==attribute.getAttributeType().listItemType){
            IntegerAttribute detailAttribute = new IntegerAttribute(new AttributeMetadata().de("Wert").en("Value"));
            AttributeEditor<Integer> attributeEditor = (AttributeEditor<Integer>) getAttributeEditor(detailAttribute,dataEditor).get();
            return Optional.of(new AttributeEditor<>((Attribute<ObservableList<Integer>>)attribute,new ListAttributeVisualisation<>(uniformDesign, detailAttribute, attributeEditor)));
        }

        if (ObservableList.class.isAssignableFrom(attribute.getAttributeType().dataType) && Long.class==attribute.getAttributeType().listItemType){
            LongAttribute detailAttribute = new LongAttribute(new AttributeMetadata().de("Wert").en("Value"));
            AttributeEditor<Long> attributeEditor = (AttributeEditor<Long>) getAttributeEditor(detailAttribute,dataEditor).get();
            return Optional.of(new AttributeEditor<>((Attribute<ObservableList<Long>>)attribute,new ListAttributeVisualisation<>(uniformDesign, detailAttribute, attributeEditor)));
        }

        if (ObservableList.class.isAssignableFrom(attribute.getAttributeType().dataType) && BigDecimal.class==attribute.getAttributeType().listItemType){
            BigDecimalAttribute detailAttribute = new BigDecimalAttribute(new AttributeMetadata().de("Wert").en("Value"));
            AttributeEditor<BigDecimal> attributeEditor = (AttributeEditor<BigDecimal>) getAttributeEditor(detailAttribute,dataEditor).get();
            return Optional.of(new AttributeEditor<>((Attribute<ObservableList<BigDecimal>>)attribute,new ListAttributeVisualisation<>(uniformDesign, detailAttribute, attributeEditor)));
        }

        if (ObservableList.class.isAssignableFrom(attribute.getAttributeType().dataType) && Double.class==attribute.getAttributeType().listItemType){
            DoubleAttribute detailAttribute = new DoubleAttribute(new AttributeMetadata().de("Wert").en("Value"));
            AttributeEditor<Double> attributeEditor = (AttributeEditor<Double>) getAttributeEditor(detailAttribute,dataEditor).get();
            return Optional.of(new AttributeEditor<>((Attribute<ObservableList<Double>>)attribute,new ListAttributeVisualisation<>(uniformDesign, detailAttribute, attributeEditor)));
        }

        if (ObservableList.class.isAssignableFrom(attribute.getAttributeType().dataType) && URI.class==attribute.getAttributeType().listItemType){
            URIAttribute detailAttribute = new URIAttribute(new AttributeMetadata().de("URI").en("URI"));
            AttributeEditor<URI> attributeEditor = (AttributeEditor<URI>) getAttributeEditor(detailAttribute,dataEditor).get();
            return Optional.of(new AttributeEditor<>((Attribute<ObservableList<URI>>)attribute,new ListAttributeVisualisation<>(uniformDesign, detailAttribute, attributeEditor)));
        }

        return null;
    }


    private Optional<AttributeEditor<?>> getAttributeEditorSimpleType(Attribute<?> attribute) {
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

        if (URI.class.isAssignableFrom(attribute.getAttributeType().dataType)) {
            return Optional.of(new AttributeEditor<>((Attribute<URI>)attribute,new URIAttributeVisualisation()));
        }

        if (LocalDate.class.isAssignableFrom(attribute.getAttributeType().dataType)) {
            return Optional.of(new AttributeEditor<>((Attribute<LocalDate>)attribute,new LocalDateAttributeVisualisation()));
        }

        return null;
    }

}
