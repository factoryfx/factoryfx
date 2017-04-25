package de.factoryfx.javafx.editor.attribute;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
import de.factoryfx.javafx.widget.datalistedit.DataListEditWidget;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;
import org.controlsfx.glyphfont.FontAwesome;

public class AttributeEditorBuilder {

    private final UniformDesign uniformDesign;
    private final List<Function<Attribute<?>,Optional<AttributeEditor<?>>>> editorAssociations;

    public AttributeEditorBuilder(UniformDesign uniformDesign, List<Function<Attribute<?>,Optional<AttributeEditor<?>>>> editorAssociations) {
        this.uniformDesign = uniformDesign;
        this.editorAssociations = editorAssociations;
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
            return Optional.of(new AttributeEditor<>((Attribute<Data>)attribute,new ViewReferenceAttributeVisualisation(dataEditor, uniformDesign),uniformDesign));
        }

        if (attribute instanceof ViewListReferenceAttribute){
            ViewListReferenceAttributeVisualisation viewListReferenceAttributeVisualisationnew = new ViewListReferenceAttributeVisualisation(dataEditor, uniformDesign);
            ExpandableAttributeVisualisation<List<Data>> expandableAttributeVisualisation= new ExpandableAttributeVisualisation<>(viewListReferenceAttributeVisualisationnew,uniformDesign,(l)->"Items: "+l.size(),FontAwesome.Glyph.LIST);
            return Optional.of(new AttributeEditor<>((Attribute<List<Data>>)attribute,expandableAttributeVisualisation,uniformDesign));
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    private Optional<AttributeEditor<?>> getAttributeEditorReference(Attribute<?> attribute, DataEditor dataEditor, Supplier<List<ValidationError>> validation, Data oldValue) {
        if (attribute.internal_getAttributeType().dataType == null)
            return Optional.empty();

        if (Data.class==attribute.internal_getAttributeType().dataType){
            ReferenceAttribute<?> referenceAttribute = (ReferenceAttribute<?>) attribute;
            return Optional.of(new AttributeEditor<>((Attribute<Data>)attribute,new ReferenceAttributeVisualisation(uniformDesign,dataEditor,()->referenceAttribute.internal_addNewFactory(),()->referenceAttribute.internal_possibleValues(),()->referenceAttribute.internal_deleteFactory(), referenceAttribute.internal_isUserEditable(),referenceAttribute.internal_isUserSelectable(),referenceAttribute.internal_isUserCreatable()),uniformDesign));
        }

        if (ObservableList.class.isAssignableFrom(attribute.internal_getAttributeType().dataType) && Data.class.isAssignableFrom(attribute.internal_getAttributeType().listItemType)){
            ReferenceListAttribute<Data> referenceListAttribute = (ReferenceListAttribute<Data>) attribute;

            final TableView<Data> dataTableView = new TableView<>();
            final ReferenceListAttributeVisualisation referenceListAttributeVisualisation = new ReferenceListAttributeVisualisation(uniformDesign, dataEditor, dataTableView, new DataListEditWidget<>(referenceListAttribute.get(), dataTableView, dataEditor,uniformDesign,referenceListAttribute));
            ExpandableAttributeVisualisation<ObservableList<Data>> expandableAttributeVisualisation= new ExpandableAttributeVisualisation<>(referenceListAttributeVisualisation,uniformDesign,(l)->"Items: "+l.size(),FontAwesome.Glyph.LIST);
            if (referenceListAttribute.get().contains(oldValue)){
                expandableAttributeVisualisation.expand();
            }
            return Optional.of(new AttributeEditor<>((Attribute<ObservableList<Data>>)attribute, expandableAttributeVisualisation,uniformDesign));
        }
        return Optional.empty();
    }

    private <T> ExpandableAttributeVisualisation<ObservableList<T>> createExpandableValueListVis(Attribute<T> detailAttribute, AttributeEditor<T> attributeEditor ){
        return new ExpandableAttributeVisualisation<>(new ValueListAttributeVisualisation<T>(uniformDesign, detailAttribute, attributeEditor),uniformDesign,(l)->"Items: "+l.size(),FontAwesome.Glyph.LIST);
    }

    @SuppressWarnings("unchecked")
    private Optional<AttributeEditor<?>> getAttributeEditorList(Attribute<?> attribute, DataEditor dataEditor, Supplier<List<ValidationError>> validation) {
        if (attribute.internal_getAttributeType().dataType == null)
            return Optional.empty();

        if (ObservableList.class.isAssignableFrom(attribute.internal_getAttributeType().dataType) && String.class==attribute.internal_getAttributeType().listItemType){
            StringAttribute detailAttribute = new StringAttribute(new AttributeMetadata().de("Wert").en("Value"));
            AttributeEditor<String> attributeEditor = (AttributeEditor<String>) getAttributeEditor(detailAttribute,dataEditor,validation).get();
            return Optional.of(new AttributeEditor<>((Attribute<ObservableList<String>>)attribute, createExpandableValueListVis(detailAttribute, attributeEditor),uniformDesign));
        }




        if (ObservableList.class.isAssignableFrom(attribute.internal_getAttributeType().dataType) && Integer.class==attribute.internal_getAttributeType().listItemType){
            IntegerAttribute detailAttribute = new IntegerAttribute(new AttributeMetadata().de("Wert").en("Value"));
            AttributeEditor<Integer> attributeEditor = (AttributeEditor<Integer>) getAttributeEditor(detailAttribute,dataEditor,validation).get();
            return Optional.of(new AttributeEditor<>((Attribute<ObservableList<Integer>>)attribute, createExpandableValueListVis(detailAttribute, attributeEditor),uniformDesign));
        }

        if (ObservableList.class.isAssignableFrom(attribute.internal_getAttributeType().dataType) && Long.class==attribute.internal_getAttributeType().listItemType){
            LongAttribute detailAttribute = new LongAttribute(new AttributeMetadata().de("Wert").en("Value"));
            AttributeEditor<Long> attributeEditor = (AttributeEditor<Long>) getAttributeEditor(detailAttribute,dataEditor,validation).get();
            return Optional.of(new AttributeEditor<>((Attribute<ObservableList<Long>>)attribute, createExpandableValueListVis(detailAttribute, attributeEditor),uniformDesign));
        }

        if (ObservableList.class.isAssignableFrom(attribute.internal_getAttributeType().dataType) && BigDecimal.class==attribute.internal_getAttributeType().listItemType){
            BigDecimalAttribute detailAttribute = new BigDecimalAttribute(new AttributeMetadata().de("Wert").en("Value"));
            AttributeEditor<BigDecimal> attributeEditor = (AttributeEditor<BigDecimal>) getAttributeEditor(detailAttribute,dataEditor,validation).get();
            return Optional.of(new AttributeEditor<>((Attribute<ObservableList<BigDecimal>>)attribute, createExpandableValueListVis(detailAttribute, attributeEditor),uniformDesign));
        }

        if (ObservableList.class.isAssignableFrom(attribute.internal_getAttributeType().dataType) && Double.class==attribute.internal_getAttributeType().listItemType){
            DoubleAttribute detailAttribute = new DoubleAttribute(new AttributeMetadata().de("Wert").en("Value"));
            AttributeEditor<Double> attributeEditor = (AttributeEditor<Double>) getAttributeEditor(detailAttribute,dataEditor,validation).get();
            return Optional.of(new AttributeEditor<>((Attribute<ObservableList<Double>>)attribute, createExpandableValueListVis(detailAttribute, attributeEditor),uniformDesign));
        }

        if (ObservableList.class.isAssignableFrom(attribute.internal_getAttributeType().dataType) && URI.class==attribute.internal_getAttributeType().listItemType){
            URIAttribute detailAttribute = new URIAttribute(new AttributeMetadata().de("URI").en("URI"));
            AttributeEditor<URI> attributeEditor = (AttributeEditor<URI>) getAttributeEditor(detailAttribute,dataEditor,validation).get();
            return Optional.of(new AttributeEditor<>((Attribute<ObservableList<URI>>)attribute, createExpandableValueListVis(detailAttribute, attributeEditor),uniformDesign));
        }

        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    private Optional<AttributeEditor<?>> getAttributeEditorSimpleType(Attribute<?> attribute, Supplier<List<ValidationError>> validation) {
        if (attribute.internal_getAttributeType().dataType == null)
            return Optional.empty();

        if (attribute instanceof EncryptedStringAttribute){
            EncryptedStringAttribute encryptedStringAttribute = (EncryptedStringAttribute) attribute;
            if (!encryptedStringAttribute.isLongText()){
                return Optional.of(new AttributeEditor<>(encryptedStringAttribute,new EncryptedStringAttributeVisualisation(encryptedStringAttribute,uniformDesign),uniformDesign));
            }
        }

        if (String.class==attribute.internal_getAttributeType().dataType){
            StringAttribute stringAttribute = (StringAttribute) attribute;
            if (!stringAttribute.internal_isLongText()){
                return Optional.of(new AttributeEditor<>(stringAttribute,new StringAttributeVisualisation(),uniformDesign));
            }
        }

        if (String.class==attribute.internal_getAttributeType().dataType){
            StringAttribute stringAttribute = (StringAttribute) attribute;
            if (stringAttribute.internal_isLongText()){
                return Optional.of(new AttributeEditor<>(stringAttribute,
                        new ExpandableAttributeVisualisation<>(new StringLongAttributeVisualisation(),uniformDesign, (s)->Ascii.truncate(s,20,"..."),FontAwesome.Glyph.FONT ),uniformDesign));
            }
        }

        if (Integer.class==attribute.internal_getAttributeType().dataType){
            return Optional.of(new AttributeEditor<>((Attribute<Integer>)attribute,new IntegerAttributeVisualisation(),uniformDesign));
        }

        if (Boolean.class==attribute.internal_getAttributeType().dataType){
            return Optional.of(new AttributeEditor<>((Attribute<Boolean>)attribute,new BooleanAttributeVisualisation(),uniformDesign));
        }

        if (Enum.class.isAssignableFrom(attribute.internal_getAttributeType().dataType)){
            Attribute<Enum> enumAttribute = (Attribute<Enum>) attribute;
            List<Enum> enumConstants = Arrays.asList((Enum[]) enumAttribute.internal_getAttributeType().dataType.getEnumConstants());
            return Optional.of(new AttributeEditor<>(enumAttribute,new EnumAttributeVisualisation(enumConstants),uniformDesign));
        }

        if (Long.class==attribute.internal_getAttributeType().dataType){
            return Optional.of(new AttributeEditor<>((Attribute<Long>)attribute,new LongAttributeVisualisation(),uniformDesign));
        }

        if (BigDecimal.class==attribute.internal_getAttributeType().dataType && attribute instanceof BigDecimalAttribute){
            return Optional.of(new AttributeEditor<>((Attribute<BigDecimal>)attribute,new BigDecimalAttributeVisualisation(((BigDecimalAttribute)attribute).internal_getDecimalFormatPattern()),uniformDesign));
        }

        if (Double.class==attribute.internal_getAttributeType().dataType){
            return Optional.of(new AttributeEditor<>((Attribute<Double>)attribute,new DoubleAttributeVisualisation(),uniformDesign));
        }

        if (URI.class.isAssignableFrom(attribute.internal_getAttributeType().dataType)) {
            return Optional.of(new AttributeEditor<>((Attribute<URI>)attribute,new URIAttributeVisualisation(),uniformDesign));
        }

        if (LocalDate.class.isAssignableFrom(attribute.internal_getAttributeType().dataType)) {
            return Optional.of(new AttributeEditor<>((Attribute<LocalDate>)attribute,new LocalDateAttributeVisualisation(),uniformDesign));
        }

        if (LocalDateTime.class.isAssignableFrom(attribute.internal_getAttributeType().dataType)) {
            return Optional.of(new AttributeEditor<>((Attribute<LocalDateTime>)attribute,new LocalDateTimeAttributeVisualisation(),uniformDesign));
        }

        if (Color.class.isAssignableFrom(attribute.internal_getAttributeType().dataType)) {
            return Optional.of(new AttributeEditor<>((Attribute<Color>)attribute,new ColorAttributeVisualisation(),uniformDesign));
        }

        if (Locale.class.isAssignableFrom(attribute.internal_getAttributeType().dataType)) {
            return Optional.of(new AttributeEditor<>((Attribute<Locale>)attribute,new LocaleAttributeVisualisation(),uniformDesign));
        }

        return Optional.empty();
    }

}
