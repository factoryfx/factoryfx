package de.factoryfx.javafx.editor.attribute;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.base.Ascii;
import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ReferenceAttribute;
import de.factoryfx.data.attribute.ReferenceListAttribute;
import de.factoryfx.data.attribute.ViewListReferenceAttribute;
import de.factoryfx.data.attribute.ViewReferenceAttribute;
import de.factoryfx.data.attribute.types.BigDecimalAttribute;
import de.factoryfx.data.attribute.types.DoubleAttribute;
import de.factoryfx.data.attribute.types.EncryptedStringAttribute;
import de.factoryfx.data.attribute.types.IntegerAttribute;
import de.factoryfx.data.attribute.types.LongAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.attribute.types.URIAttribute;
import de.factoryfx.data.validation.ValidationError;
import de.factoryfx.javafx.editor.attribute.visualisation.BigDecimalAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.BooleanAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.ColorAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.DoubleAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.EncryptedStringAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.EnumAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.ExpandableAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.IntegerAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.LocalDateAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.LocalDateTimeAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.LocaleAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.LongAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.ReferenceAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.ReferenceListAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.StringAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.StringLongAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.URIAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.ValueListAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.ViewListReferenceAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.ViewReferenceAttributeVisualisation;
import de.factoryfx.javafx.editor.data.DataEditor;
import de.factoryfx.javafx.util.UniformDesign;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import org.controlsfx.glyphfont.FontAwesome;

public class AttributeEditorFactory {

    private final UniformDesign uniformDesign;

    public AttributeEditorFactory(UniformDesign uniformDesign) {
        this.uniformDesign = uniformDesign;
    }

    List<Function<Attribute<?>,Optional<AttributeEditor<?>>>> editorAssociations=new ArrayList<>();
    public void addEditorAssociation(Function<Attribute<?>,Optional<AttributeEditor<?>>> editorAssociation){
        editorAssociations.add(editorAssociation);
    }

    private Optional<AttributeEditor<?>> getAttributeEditor(Attribute<?> attribute, DataEditor dataEditor, Supplier<List<ValidationError>> validation){
       return this.getAttributeEditor(attribute,dataEditor,validation,null);
    }


    public Optional<AttributeEditor<?>> getAttributeEditor(Attribute<?> attribute, DataEditor dataEditor, Supplier<List<ValidationError>> validation, Data oldValue){

        for (Function<Attribute<?>,Optional<AttributeEditor<?>>> editorAssociation: editorAssociations) {
            Optional<AttributeEditor<?>> attributeEditor = editorAssociation.apply(attribute);
            if (attributeEditor.isPresent()) {
                return attributeEditor;
            }
        }

        Optional<AttributeEditor<?>> enumAttribute = getAttributeEditorSimpleType(attribute,validation);
        if (enumAttribute.isPresent()) return enumAttribute;

        Optional<AttributeEditor<?>> detailAttribute = getAttributeEditorList(attribute, dataEditor,validation);
        if (detailAttribute.isPresent()) return detailAttribute;

        Optional<AttributeEditor<?>> viewAttribute = getViewAttribute(attribute, dataEditor,validation);
        if (viewAttribute.isPresent()) return viewAttribute;

        Optional<AttributeEditor<?>> referenceAttribute = getAttributeEditorReference(attribute, dataEditor,validation,oldValue);
        if (referenceAttribute.isPresent()) return referenceAttribute;

        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    private Optional<AttributeEditor<?>> getViewAttribute(Attribute<?> attribute, DataEditor dataEditor, Supplier<List<ValidationError>> validation) {
        if (attribute instanceof ViewReferenceAttribute){
            return Optional.of(new AttributeEditor<>((Attribute<Data>)attribute,new ViewReferenceAttributeVisualisation(dataEditor, uniformDesign)));
        }

        if (attribute instanceof ViewListReferenceAttribute){
            ViewListReferenceAttributeVisualisation viewListReferenceAttributeVisualisationnew = new ViewListReferenceAttributeVisualisation(dataEditor, uniformDesign);
            ExpandableAttributeVisualisation<List<Data>> expandableAttributeVisualisation= new ExpandableAttributeVisualisation<>(viewListReferenceAttributeVisualisationnew,uniformDesign,(l)->"Items: "+l.size(),FontAwesome.Glyph.LIST);
            return Optional.of(new AttributeEditor<>((Attribute<List<Data>>)attribute,expandableAttributeVisualisation));
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    private Optional<AttributeEditor<?>> getAttributeEditorReference(Attribute<?> attribute, DataEditor dataEditor, Supplier<List<ValidationError>> validation, Data oldValue) {
        if (attribute.getAttributeType().dataType == null)
            return Optional.empty();

        if (Data.class==attribute.getAttributeType().dataType){
            ReferenceAttribute<?> referenceAttribute = (ReferenceAttribute<?>) attribute;
            return Optional.of(new AttributeEditor<>((Attribute<Data>)attribute,new ReferenceAttributeVisualisation(uniformDesign,dataEditor,()->referenceAttribute.addNewFactory(),()->(List<Data>)referenceAttribute.possibleValues(),referenceAttribute.isUserEditable(),referenceAttribute.isUserSelectable())));
        }

        if (ObservableList.class.isAssignableFrom(attribute.getAttributeType().dataType) && Data.class.isAssignableFrom(attribute.getAttributeType().listItemType)){
            ReferenceListAttribute<?> referenceListAttribute = (ReferenceListAttribute<?>) attribute;

            final ReferenceListAttributeVisualisation referenceListAttributeVisualisation = new ReferenceListAttributeVisualisation(uniformDesign, dataEditor, () -> referenceListAttribute.addNewFactory(), () -> (List<Data>) referenceListAttribute.possibleValues(), referenceListAttribute.isUserEditable(), referenceListAttribute.isUserSelectable());
            ExpandableAttributeVisualisation<ObservableList<Data>> expandableAttributeVisualisation= new ExpandableAttributeVisualisation<>(referenceListAttributeVisualisation,uniformDesign,(l)->"Items: "+l.size(),FontAwesome.Glyph.LIST);
            if (referenceListAttribute.get().contains(oldValue)){
                expandableAttributeVisualisation.expand();
            }
            return Optional.of(new AttributeEditor<>((Attribute<ObservableList<Data>>)attribute, expandableAttributeVisualisation));
        }
        return Optional.empty();
    }

    private <T> ExpandableAttributeVisualisation<ObservableList<T>> createExpandableValueListVis(Attribute<T> detailAttribute, AttributeEditor<T> attributeEditor ){
        return new ExpandableAttributeVisualisation<>(new ValueListAttributeVisualisation<T>(uniformDesign, detailAttribute, attributeEditor),uniformDesign,(l)->"Items: "+l.size(),FontAwesome.Glyph.LIST);
    }

    @SuppressWarnings("unchecked")
    private Optional<AttributeEditor<?>> getAttributeEditorList(Attribute<?> attribute, DataEditor dataEditor, Supplier<List<ValidationError>> validation) {
        if (attribute.getAttributeType().dataType == null)
            return Optional.empty();

        if (ObservableList.class.isAssignableFrom(attribute.getAttributeType().dataType) && String.class==attribute.getAttributeType().listItemType){
            StringAttribute detailAttribute = new StringAttribute(new AttributeMetadata().de("Wert").en("Value"));
            AttributeEditor<String> attributeEditor = (AttributeEditor<String>) getAttributeEditor(detailAttribute,dataEditor,validation).get();
            return Optional.of(new AttributeEditor<>((Attribute<ObservableList<String>>)attribute, createExpandableValueListVis(detailAttribute, attributeEditor)));
        }




        if (ObservableList.class.isAssignableFrom(attribute.getAttributeType().dataType) && Integer.class==attribute.getAttributeType().listItemType){
            IntegerAttribute detailAttribute = new IntegerAttribute(new AttributeMetadata().de("Wert").en("Value"));
            AttributeEditor<Integer> attributeEditor = (AttributeEditor<Integer>) getAttributeEditor(detailAttribute,dataEditor,validation).get();
            return Optional.of(new AttributeEditor<>((Attribute<ObservableList<Integer>>)attribute, createExpandableValueListVis(detailAttribute, attributeEditor)));
        }

        if (ObservableList.class.isAssignableFrom(attribute.getAttributeType().dataType) && Long.class==attribute.getAttributeType().listItemType){
            LongAttribute detailAttribute = new LongAttribute(new AttributeMetadata().de("Wert").en("Value"));
            AttributeEditor<Long> attributeEditor = (AttributeEditor<Long>) getAttributeEditor(detailAttribute,dataEditor,validation).get();
            return Optional.of(new AttributeEditor<>((Attribute<ObservableList<Long>>)attribute, createExpandableValueListVis(detailAttribute, attributeEditor)));
        }

        if (ObservableList.class.isAssignableFrom(attribute.getAttributeType().dataType) && BigDecimal.class==attribute.getAttributeType().listItemType){
            BigDecimalAttribute detailAttribute = new BigDecimalAttribute(new AttributeMetadata().de("Wert").en("Value"));
            AttributeEditor<BigDecimal> attributeEditor = (AttributeEditor<BigDecimal>) getAttributeEditor(detailAttribute,dataEditor,validation).get();
            return Optional.of(new AttributeEditor<>((Attribute<ObservableList<BigDecimal>>)attribute, createExpandableValueListVis(detailAttribute, attributeEditor)));
        }

        if (ObservableList.class.isAssignableFrom(attribute.getAttributeType().dataType) && Double.class==attribute.getAttributeType().listItemType){
            DoubleAttribute detailAttribute = new DoubleAttribute(new AttributeMetadata().de("Wert").en("Value"));
            AttributeEditor<Double> attributeEditor = (AttributeEditor<Double>) getAttributeEditor(detailAttribute,dataEditor,validation).get();
            return Optional.of(new AttributeEditor<>((Attribute<ObservableList<Double>>)attribute, createExpandableValueListVis(detailAttribute, attributeEditor)));
        }

        if (ObservableList.class.isAssignableFrom(attribute.getAttributeType().dataType) && URI.class==attribute.getAttributeType().listItemType){
            URIAttribute detailAttribute = new URIAttribute(new AttributeMetadata().de("URI").en("URI"));
            AttributeEditor<URI> attributeEditor = (AttributeEditor<URI>) getAttributeEditor(detailAttribute,dataEditor,validation).get();
            return Optional.of(new AttributeEditor<>((Attribute<ObservableList<URI>>)attribute, createExpandableValueListVis(detailAttribute, attributeEditor)));
        }

        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    private Optional<AttributeEditor<?>> getAttributeEditorSimpleType(Attribute<?> attribute, Supplier<List<ValidationError>> validation) {
        if (attribute.getAttributeType().dataType == null)
            return Optional.empty();

        if (attribute instanceof EncryptedStringAttribute){
            EncryptedStringAttribute stringAttribute = (EncryptedStringAttribute) attribute;
            if (!stringAttribute.isLongText()){
                return Optional.of(new AttributeEditor<>(stringAttribute,new EncryptedStringAttributeVisualisation()));
            }
        }

        if (String.class==attribute.getAttributeType().dataType){
            StringAttribute stringAttribute = (StringAttribute) attribute;
            if (!stringAttribute.isLongText()){
                return Optional.of(new AttributeEditor<>(stringAttribute,new StringAttributeVisualisation()));
            }
        }

        if (String.class==attribute.getAttributeType().dataType){
            StringAttribute stringAttribute = (StringAttribute) attribute;
            if (stringAttribute.isLongText()){
                return Optional.of(new AttributeEditor<>(stringAttribute,
                        new ExpandableAttributeVisualisation<>(new StringLongAttributeVisualisation(),uniformDesign, (s)->Ascii.truncate(s,20,"..."),FontAwesome.Glyph.FONT )));
            }
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

        if (LocalDateTime.class.isAssignableFrom(attribute.getAttributeType().dataType)) {
            return Optional.of(new AttributeEditor<>((Attribute<LocalDateTime>)attribute,new LocalDateTimeAttributeVisualisation()));
        }

        if (Color.class.isAssignableFrom(attribute.getAttributeType().dataType)) {
            return Optional.of(new AttributeEditor<>((Attribute<Color>)attribute,new ColorAttributeVisualisation()));
        }

        if (Locale.class.isAssignableFrom(attribute.getAttributeType().dataType)) {
            return Optional.of(new AttributeEditor<>((Attribute<Locale>)attribute,new LocaleAttributeVisualisation()));
        }

        return Optional.empty();
    }

}
