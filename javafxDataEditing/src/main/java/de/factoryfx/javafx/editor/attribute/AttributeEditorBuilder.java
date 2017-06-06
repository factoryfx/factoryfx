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

        result.add(new DataSingleAttributeEditorBuilder<Data,ReferenceAttribute<Data,?>>(uniformDesign,(a)->a instanceof ReferenceAttribute,(attribute, dataEditor, previousData)->{
            return new ReferenceAttributeVisualisation(uniformDesign,dataEditor, attribute::internal_addNewFactory, attribute::internal_possibleValues, attribute::internal_deleteFactory, attribute.internal_isUserEditable(),attribute.internal_isUserSelectable(),attribute.internal_isUserCreatable());
        }));
        result.add(new DataSingleAttributeEditorBuilder<List<Data>,ReferenceListAttribute<Data,?>>(uniformDesign,(a)->a instanceof ReferenceListAttribute,(attribute, dataEditor,previousData)->{
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


}
