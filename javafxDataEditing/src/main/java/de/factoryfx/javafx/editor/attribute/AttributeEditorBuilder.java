package de.factoryfx.javafx.editor.attribute;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;

import com.google.common.base.Ascii;
import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.*;
import de.factoryfx.data.attribute.primitive.BooleanAttribute;
import de.factoryfx.data.attribute.primitive.DoubleAttribute;
import de.factoryfx.data.attribute.primitive.IntegerAttribute;
import de.factoryfx.data.attribute.primitive.LongAttribute;
import de.factoryfx.data.attribute.types.*;
import de.factoryfx.data.util.LanguageText;
import de.factoryfx.data.validation.ValidationError;
import de.factoryfx.javafx.editor.attribute.builder.DataSingleAttributeEditorBuilder;
import de.factoryfx.javafx.editor.attribute.builder.SimpleSingleAttributeEditorBuilder;
import de.factoryfx.javafx.editor.attribute.builder.SingleAttributeEditorBuilder;
import de.factoryfx.javafx.editor.attribute.visualisation.*;
import de.factoryfx.javafx.editor.data.DataEditor;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.widget.datalistedit.DataListEditWidget;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;
import org.controlsfx.glyphfont.FontAwesome;

public class AttributeEditorBuilder {

    private final UniformDesign uniformDesign;
    private final List<SingleAttributeEditorBuilder<?>> singleAttributeEditorBuilders;

    public AttributeEditorBuilder(UniformDesign uniformDesign, List<SingleAttributeEditorBuilder<?>> singleAttributeEditorBuilders) {
        this.uniformDesign = uniformDesign;
        this.singleAttributeEditorBuilders = singleAttributeEditorBuilders;
    }

    public static List<SingleAttributeEditorBuilder<?>> createDefaultSingleAttributeEditorBuilders(UniformDesign uniformDesign){
        ArrayList<SingleAttributeEditorBuilder<?>> result = new ArrayList<>();
        result.add(new SimpleSingleAttributeEditorBuilder<>(uniformDesign,BigDecimalAttribute.class,BigDecimal.class,(attribute)->{
            return new BigDecimalAttributeVisualisation(attribute.internal_getDecimalFormatPattern());
        },()->new BigDecimalAttribute(new AttributeMetadata().de("Wert").en("Value"))));
        result.add(new SimpleSingleAttributeEditorBuilder<>(uniformDesign,BooleanAttribute.class,Boolean.class,(attribute)->{
            return new BooleanAttributeVisualisation();
        },()->new BooleanAttribute(new AttributeMetadata().de("Wert").en("Value"))));
//        result.add(new SimpleSingleAttributeEditorBuilder<>(ByteArrayAttribute.class,byte[].class,(attribute)->{
//            return new AttributeEditor<BigDecimal>(attribute,new BigDecimalAttributeVisualisation(attribute.internal_getDecimalFormatPattern()),uniformDesign);
//        }));
        result.add(new SimpleSingleAttributeEditorBuilder<>(uniformDesign,ColorAttribute.class,Color.class,(attribute)->{
            return new ColorAttributeVisualisation();
        },()->new ColorAttribute(new AttributeMetadata().de("Wert").en("Value"))));
        result.add(new SimpleSingleAttributeEditorBuilder<>(uniformDesign,DoubleAttribute.class,Double.class,(attribute)->{
            return new DoubleAttributeVisualisation();
        },()->new DoubleAttribute(new AttributeMetadata().de("Wert").en("Value"))));

        result.add(new SimpleSingleAttributeEditorBuilder<>(uniformDesign,EncryptedStringAttribute.class,EncryptedString.class,(attribute)->{
            return new EncryptedStringAttributeVisualisation(()->attribute.createKey(),uniformDesign);
        },()->new EncryptedStringAttribute(new AttributeMetadata().de("Wert").en("Value"))));

        result.add(new SingleAttributeEditorBuilder<Enum<?>>(){
            @Override
            public boolean isListItemEditorFor(Attribute<?> attribute) {
                return false;
            }

            @Override
            public boolean isEditorFor(Attribute<?> attribute) {
                return attribute instanceof EnumAttribute;
            }

            @Override
            @SuppressWarnings("unchecked")
            public AttributeEditor<Enum<?>> createEditor(Attribute<?> attribute, DataEditor dataEditor, Data previousData) {
                EnumAttribute enumAttribute = (EnumAttribute)attribute;
                return new AttributeEditor(enumAttribute,new EnumAttributeVisualisation(enumAttribute.internal_possibleEnumValues()),uniformDesign);
            }
            @Override
            public AttributeEditor<List<Enum<?>>> createValueListEditor(Attribute<?> attribute) {
                return null;
            }
        });
        result.add(new SimpleSingleAttributeEditorBuilder<>(uniformDesign,I18nAttribute.class,LanguageText.class,(attribute)->{
            return new I18nAttributeVisualisation();
        },()->new I18nAttribute(new AttributeMetadata().de("Wert").en("Value"))));
        result.add(new SimpleSingleAttributeEditorBuilder<>(uniformDesign,IntegerAttribute.class,Integer.class,(attribute)->{
            return new IntegerAttributeVisualisation();
        },()->new IntegerAttribute(new AttributeMetadata().de("Wert").en("Value"))));
        result.add(new SimpleSingleAttributeEditorBuilder<>(uniformDesign,LocalDateAttribute.class,LocalDate.class,(attribute)->{
            return new LocalDateAttributeVisualisation();
        },()->new LocalDateAttribute(new AttributeMetadata().de("Wert").en("Value"))));
        result.add(new SimpleSingleAttributeEditorBuilder<>(uniformDesign,LocalDateTimeAttribute.class,LocalDateTime.class,(attribute)->{
            return new LocalDateTimeAttributeVisualisation();
        },()->new LocalDateTimeAttribute(new AttributeMetadata().de("Wert").en("Value"))));
        result.add(new SimpleSingleAttributeEditorBuilder<>(uniformDesign,LocaleAttribute.class,Locale.class,(attribute)->{
            return new LocaleAttributeVisualisation();
        },()->new LocaleAttribute(new AttributeMetadata().de("Wert").en("Value"))));
        result.add(new SimpleSingleAttributeEditorBuilder<>(uniformDesign,LongAttribute.class,Long.class,(attribute)->{
            return new LongAttributeVisualisation();
        },()->new LongAttribute(new AttributeMetadata().de("Wert").en("Value"))));
        result.add(new SimpleSingleAttributeEditorBuilder<>(uniformDesign,StringAttribute.class,String.class,(attribute)->{
            if (attribute.internal_isLongText()){
                return new ExpandableAttributeVisualisation<>(new StringLongAttributeVisualisation(),uniformDesign, (s)->Ascii.truncate(s,20,"..."),FontAwesome.Glyph.FONT,attribute.internal_isDefaultExpanded() );
            } else {
                return new StringAttributeVisualisation();
            }
        },()->new StringAttribute(new AttributeMetadata().de("Wert").en("Value"))));
        result.add(new SimpleSingleAttributeEditorBuilder<>(uniformDesign,URIAttribute.class,URI.class,(attribute)->{
            return new URIAttributeVisualisation();
        },()->new URIAttribute(new AttributeMetadata().de("Wert").en("Value"))));

        result.add(new DataSingleAttributeEditorBuilder<Data,ViewReferenceAttribute<?,Data>>(uniformDesign,(a)->a instanceof ViewReferenceAttribute,(attribute, dataEditor, previousData)->{
            return new ViewReferenceAttributeVisualisation(dataEditor, uniformDesign);
        }));
        result.add(new DataSingleAttributeEditorBuilder<List<Data>,ViewListReferenceAttribute<?,Data>>(uniformDesign,(a)->a instanceof ViewListReferenceAttribute,(attribute, dataEditor, previousData)->{
            ViewListReferenceAttributeVisualisation visualisation = new ViewListReferenceAttributeVisualisation(dataEditor, uniformDesign);
            ExpandableAttributeVisualisation<List<Data>> expandableAttributeVisualisation= new ExpandableAttributeVisualisation<>(visualisation,uniformDesign,(l)->"Items: "+l.size(),FontAwesome.Glyph.LIST);
            if (attribute.get().contains(previousData)){
                expandableAttributeVisualisation.expand();
            }
            return expandableAttributeVisualisation;
        }));

        result.add(new DataSingleAttributeEditorBuilder<Data,ReferenceAttribute<Data>>(uniformDesign,(a)->a instanceof ReferenceAttribute,(attribute, dataEditor, previousData)->{
            return new ReferenceAttributeVisualisation(uniformDesign,dataEditor, attribute::internal_addNewFactory, attribute::internal_possibleValues, attribute::internal_deleteFactory, attribute.internal_isUserEditable(),attribute.internal_isUserSelectable(),attribute.internal_isUserCreatable());
        }));
        result.add(new DataSingleAttributeEditorBuilder<List<Data>,ReferenceListAttribute<Data>>(uniformDesign,(a)->a instanceof ReferenceListAttribute,(attribute, dataEditor,previousData)->{
            final TableView<Data> dataTableView = new TableView<>();
            final ReferenceListAttributeVisualisation referenceListAttributeVisualisation = new ReferenceListAttributeVisualisation(uniformDesign, dataEditor, dataTableView, new DataListEditWidget<>(attribute.get(), dataTableView, dataEditor,uniformDesign,attribute));
            ExpandableAttributeVisualisation<List<Data>> expandableAttributeVisualisation= new ExpandableAttributeVisualisation<>(referenceListAttributeVisualisation,uniformDesign,(l)->"Items: "+l.size(),FontAwesome.Glyph.LIST);
            if (attribute.get().contains(previousData)){
                expandableAttributeVisualisation.expand();
            }
            return expandableAttributeVisualisation;
        }));

        return result;
    }

    public AttributeEditorBuilder(UniformDesign uniformDesign) {
        this(uniformDesign,createDefaultSingleAttributeEditorBuilders(uniformDesign));
    }


    private Optional<AttributeEditor<?>> getAttributeEditor(Attribute<?> attribute, DataEditor dataEditor, Supplier<List<ValidationError>> validation){
       return this.getAttributeEditor(attribute,dataEditor,validation,null);
    }


    public Optional<AttributeEditor<?>> getAttributeEditor(Attribute<?> attribute, DataEditor dataEditor, Supplier<List<ValidationError>> validation, Data oldValue){

        if (attribute instanceof ValueListAttribute<?>){
            Optional<SingleAttributeEditorBuilder<?>> builder = singleAttributeEditorBuilders.stream().filter(a -> a.isListItemEditorFor(attribute)).findAny();
            return builder.map(singleAttributeEditorBuilder -> singleAttributeEditorBuilder.createValueListEditor(attribute));
        }

//        if (attribute instanceof ValueAttribute<?>){
        Optional<SingleAttributeEditorBuilder<?>> builder = singleAttributeEditorBuilders.stream().filter(a -> a.isEditorFor(attribute)).findAny();
        return builder.map(singleAttributeEditorBuilder -> singleAttributeEditorBuilder.createEditor(attribute, dataEditor, oldValue));
//        }

//        Optional<AttributeEditor<?>> viewAttribute = getViewAttribute(attribute, dataEditor,validation);
//        if (viewAttribute.isPresent()) return viewAttribute;

//        Optional<AttributeEditor<?>> referenceAttribute = getAttributeEditorReference(attribute, dataEditor,validation,oldValue);
//        if (referenceAttribute.isPresent()) return referenceAttribute;

//        return Optional.empty();
    }

//    @SuppressWarnings("unchecked")
//    private Optional<AttributeEditor<?>> getViewAttribute(Attribute<?> attribute, DataEditor dataEditor, Supplier<List<ValidationError>> validation) {
//        if (attribute instanceof ViewReferenceAttribute){
//            return Optional.of(new AttributeEditor<>((Attribute<Data>)attribute,new ViewReferenceAttributeVisualisation(dataEditor, uniformDesign),uniformDesign));
//        }
//
//        if (attribute instanceof ViewListReferenceAttribute){
//            ViewListReferenceAttributeVisualisation viewListReferenceAttributeVisualisationnew = new ViewListReferenceAttributeVisualisation(dataEditor, uniformDesign);
//            ExpandableAttributeVisualisation<List<Data>> expandableAttributeVisualisation= new ExpandableAttributeVisualisation<>(viewListReferenceAttributeVisualisationnew,uniformDesign,(l)->"Items: "+l.size(),FontAwesome.Glyph.LIST);
//            return Optional.of(new AttributeEditor<>((Attribute<List<Data>>)attribute,expandableAttributeVisualisation,uniformDesign));
//        }
//        return Optional.empty();
//    }

    @SuppressWarnings("unchecked")
    private Optional<AttributeEditor<?>> getAttributeEditorReference(Attribute<?> attribute, DataEditor dataEditor, Supplier<List<ValidationError>> validation, Data oldValue) {
        if (attribute.internal_getAttributeType().dataType == null)
            return Optional.empty();

        if (Data.class==attribute.internal_getAttributeType().dataType){
            ReferenceAttribute<?> referenceAttribute = (ReferenceAttribute<?>) attribute;
            return Optional.of(new AttributeEditor<>((Attribute<Data>)attribute,new ReferenceAttributeVisualisation(uniformDesign,dataEditor, referenceAttribute::internal_addNewFactory, referenceAttribute::internal_possibleValues, referenceAttribute::internal_deleteFactory, referenceAttribute.internal_isUserEditable(),referenceAttribute.internal_isUserSelectable(),referenceAttribute.internal_isUserCreatable()),uniformDesign));
        }

        if (ObservableList.class.isAssignableFrom(attribute.internal_getAttributeType().dataType) && Data.class.isAssignableFrom(attribute.internal_getAttributeType().listItemType)){
            ReferenceListAttribute<Data> referenceListAttribute = (ReferenceListAttribute<Data>) attribute;

            final TableView<Data> dataTableView = new TableView<>();
            final ReferenceListAttributeVisualisation referenceListAttributeVisualisation = new ReferenceListAttributeVisualisation(uniformDesign, dataEditor, dataTableView, new DataListEditWidget<>(referenceListAttribute.get(), dataTableView, dataEditor,uniformDesign,referenceListAttribute));
            ExpandableAttributeVisualisation<List<Data>> expandableAttributeVisualisation= new ExpandableAttributeVisualisation<>(referenceListAttributeVisualisation,uniformDesign,(l)->"Items: "+l.size(),FontAwesome.Glyph.LIST);
            if (referenceListAttribute.get().contains(oldValue)){
                expandableAttributeVisualisation.expand();
            }
            return Optional.of(new AttributeEditor<>((Attribute<List<Data>>)attribute, expandableAttributeVisualisation,uniformDesign));
        }
        return Optional.empty();
    }

//    @SuppressWarnings("unchecked")
//    private Optional<AttributeEditor<?>> getAttributeEditorList(Attribute<?> attribute, DataEditor dataEditor, Supplier<List<ValidationError>> validation) {
//        if (attribute.internal_getAttributeType().dataType == null || !ObservableList.class.isAssignableFrom(attribute.internal_getAttributeType().dataType) || attribute.internal_getAttributeType().listItemType==null ) {
//            return Optional.empty();
//        }
//
//
//
//        StringAttribute detailAttribute = new StringAttribute(new AttributeMetadata().de("Wert").en("Value"));
//        Optional<AttributeEditor<?>> attributeEditor = getAttributeEditor(detailAttribute,dataEditor,validation);
//        if (!attributeEditor.isPresent()){
//            return Optional.empty();
//        }
//        return Optional.of(new AttributeEditor<>((Attribute<ObservableList<String>>)attribute, createExpandableValueListVis(detailAttribute, attributeEditor.get()),uniformDesign));
//
//
//
//
//        if (ObservableList.class.isAssignableFrom(attribute.internal_getAttributeType().dataType) && Integer.class==attribute.internal_getAttributeType().listItemType){
//            IntegerAttribute detailAttribute = new IntegerAttribute(new AttributeMetadata().de("Wert").en("Value"));
//            AttributeEditor<Integer> attributeEditor = (AttributeEditor<Integer>) getAttributeEditor(detailAttribute,dataEditor,validation).get();
//            return Optional.of(new AttributeEditor<>((Attribute<ObservableList<Integer>>)attribute, createExpandableValueListVis(detailAttribute, attributeEditor),uniformDesign));
//        }
//
//        if (ObservableList.class.isAssignableFrom(attribute.internal_getAttributeType().dataType) && Long.class==attribute.internal_getAttributeType().listItemType){
//            LongAttribute detailAttribute = new LongAttribute(new AttributeMetadata().de("Wert").en("Value"));
//            AttributeEditor<Long> attributeEditor = (AttributeEditor<Long>) getAttributeEditor(detailAttribute,dataEditor,validation).get();
//            return Optional.of(new AttributeEditor<>((Attribute<ObservableList<Long>>)attribute, createExpandableValueListVis(detailAttribute, attributeEditor),uniformDesign));
//        }
//
//        if (ObservableList.class.isAssignableFrom(attribute.internal_getAttributeType().dataType) && BigDecimal.class==attribute.internal_getAttributeType().listItemType){
//            BigDecimalAttribute detailAttribute = new BigDecimalAttribute(new AttributeMetadata().de("Wert").en("Value"));
//            AttributeEditor<BigDecimal> attributeEditor = (AttributeEditor<BigDecimal>) getAttributeEditor(detailAttribute,dataEditor,validation).get();
//            return Optional.of(new AttributeEditor<>((Attribute<ObservableList<BigDecimal>>)attribute, createExpandableValueListVis(detailAttribute, attributeEditor),uniformDesign));
//        }
//
//        if (ObservableList.class.isAssignableFrom(attribute.internal_getAttributeType().dataType) && Double.class==attribute.internal_getAttributeType().listItemType){
//            DoubleAttribute detailAttribute = new DoubleAttribute(new AttributeMetadata().de("Wert").en("Value"));
//            AttributeEditor<Double> attributeEditor = (AttributeEditor<Double>) getAttributeEditor(detailAttribute,dataEditor,validation).get();
//            return Optional.of(new AttributeEditor<>((Attribute<ObservableList<Double>>)attribute, createExpandableValueListVis(detailAttribute, attributeEditor),uniformDesign));
//        }
//
//        if (ObservableList.class.isAssignableFrom(attribute.internal_getAttributeType().dataType) && URI.class==attribute.internal_getAttributeType().listItemType){
//            URIAttribute detailAttribute = new URIAttribute(new AttributeMetadata().de("URI").en("URI"));
//            AttributeEditor<URI> attributeEditor = (AttributeEditor<URI>) getAttributeEditor(detailAttribute,dataEditor,validation).get();
//            return Optional.of(new AttributeEditor<>((Attribute<ObservableList<URI>>)attribute, createExpandableValueListVis(detailAttribute, attributeEditor),uniformDesign));
//        }
//
//        return Optional.empty();
//    }

//    @SuppressWarnings("unchecked")
//    private Optional<AttributeEditor<?>> getAttributeEditorSimpleType(Attribute<?> attribute, Supplier<List<ValidationError>> validation) {
//        if (attribute.internal_getAttributeType().dataType == null)
//            return Optional.empty();
//
//        if (attribute instanceof EncryptedStringAttribute){
//            EncryptedStringAttribute encryptedStringAttribute = (EncryptedStringAttribute) attribute;
//            if (!encryptedStringAttribute.isLongText()){
//                return Optional.of(new AttributeEditor<>(encryptedStringAttribute,new EncryptedStringAttributeVisualisation(encryptedStringAttribute,uniformDesign),uniformDesign));
//            }
//        }
//
//        if (String.class==attribute.internal_getAttributeType().dataType){
//            StringAttribute stringAttribute = (StringAttribute) attribute;
//            if (!stringAttribute.internal_isLongText()){
//                return Optional.of(new AttributeEditor<>(stringAttribute,new StringAttributeVisualisation(),uniformDesign));
//            }
//        }
//
//        if (String.class==attribute.internal_getAttributeType().dataType){
//            StringAttribute stringAttribute = (StringAttribute) attribute;
//            if (stringAttribute.internal_isLongText()){
//                return Optional.of(new AttributeEditor<>(stringAttribute,
//                        new ExpandableAttributeVisualisation<>(new StringLongAttributeVisualisation(),uniformDesign, (s)->Ascii.truncate(s,20,"..."),FontAwesome.Glyph.FONT,stringAttribute.internal_isDefaultExpanded() ),uniformDesign));
//            }
//        }
//
//        if (Integer.class==attribute.internal_getAttributeType().dataType){
//            return Optional.of(new AttributeEditor<>((Attribute<Integer>)attribute,new IntegerAttributeVisualisation(),uniformDesign));
//        }
//
//        if (Boolean.class==attribute.internal_getAttributeType().dataType){
//            return Optional.of(new AttributeEditor<>((Attribute<Boolean>)attribute,new BooleanAttributeVisualisation(),uniformDesign));
//        }
//
//        if (Enum.class.isAssignableFrom(attribute.internal_getAttributeType().dataType)){
//            Attribute<Enum> enumAttribute = (Attribute<Enum>) attribute;
//            List<Enum> enumConstants = Arrays.asList((Enum[]) enumAttribute.internal_getAttributeType().dataType.getEnumConstants());
//            return Optional.of(new AttributeEditor<>(enumAttribute,new EnumAttributeVisualisation(enumConstants),uniformDesign));
//        }
//
//        if (Long.class==attribute.internal_getAttributeType().dataType){
//            return Optional.of(new AttributeEditor<>((Attribute<Long>)attribute,new LongAttributeVisualisation(),uniformDesign));
//        }
//
//        if (BigDecimal.class==attribute.internal_getAttributeType().dataType && attribute instanceof BigDecimalAttribute){
//            return Optional.of(new AttributeEditor<>((Attribute<BigDecimal>)attribute,new BigDecimalAttributeVisualisation(((BigDecimalAttribute)attribute).internal_getDecimalFormatPattern()),uniformDesign));
//        }
//
//        if (Double.class==attribute.internal_getAttributeType().dataType){
//            return Optional.of(new AttributeEditor<>((Attribute<Double>)attribute,new DoubleAttributeVisualisation(),uniformDesign));
//        }
//
//        if (URI.class.isAssignableFrom(attribute.internal_getAttributeType().dataType)) {
//            return Optional.of(new AttributeEditor<>((Attribute<URI>)attribute,new URIAttributeVisualisation(),uniformDesign));
//        }
//
//        if (LocalDate.class.isAssignableFrom(attribute.internal_getAttributeType().dataType)) {
//            return Optional.of(new AttributeEditor<>((Attribute<LocalDate>)attribute,new LocalDateAttributeVisualisation(),uniformDesign));
//        }
//
//        if (LocalDateTime.class.isAssignableFrom(attribute.internal_getAttributeType().dataType)) {
//            return Optional.of(new AttributeEditor<>((Attribute<LocalDateTime>)attribute,new LocalDateTimeAttributeVisualisation(),uniformDesign));
//        }
//
//        if (Color.class.isAssignableFrom(attribute.internal_getAttributeType().dataType)) {
//            return Optional.of(new AttributeEditor<>((Attribute<Color>)attribute,new ColorAttributeVisualisation(),uniformDesign));
//        }
//
//        if (Locale.class.isAssignableFrom(attribute.internal_getAttributeType().dataType)) {
//            return Optional.of(new AttributeEditor<>((Attribute<Locale>)attribute,new LocaleAttributeVisualisation(),uniformDesign));
//        }
//
//        return Optional.empty();
//    }

}
