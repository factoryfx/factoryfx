package de.factoryfx.javafx.data.editor.attribute;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.factoryfx.javafx.data.editor.attribute.visualisation.*;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;

import org.controlsfx.glyphfont.FontAwesome;

import com.google.common.base.Ascii;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.ReferenceAttribute;
import de.factoryfx.data.attribute.ReferenceListAttribute;
import de.factoryfx.data.attribute.ValueListAttribute;
import de.factoryfx.data.attribute.ViewListReferenceAttribute;
import de.factoryfx.data.attribute.ViewReferenceAttribute;
import de.factoryfx.data.attribute.primitive.BooleanAttribute;
import de.factoryfx.data.attribute.primitive.DoubleAttribute;
import de.factoryfx.data.attribute.primitive.IntegerAttribute;
import de.factoryfx.data.attribute.primitive.LongAttribute;
import de.factoryfx.data.attribute.primitive.ShortAttribute;
import de.factoryfx.data.attribute.time.DurationAttribute;
import de.factoryfx.data.attribute.time.LocalDateAttribute;
import de.factoryfx.data.attribute.time.LocalDateTimeAttribute;
import de.factoryfx.data.attribute.time.LocalTimeAttribute;
import de.factoryfx.data.attribute.types.FileContentAttribute;
import de.factoryfx.data.attribute.types.BigDecimalAttribute;
import de.factoryfx.data.attribute.types.EncryptedString;
import de.factoryfx.data.attribute.types.EncryptedStringAttribute;
import de.factoryfx.data.attribute.types.EnumAttribute;
import de.factoryfx.data.attribute.types.EnumListAttribute;
import de.factoryfx.data.attribute.types.I18nAttribute;
import de.factoryfx.data.attribute.types.LocaleAttribute;
import de.factoryfx.data.attribute.types.PasswordAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.attribute.types.URIAttribute;
import de.factoryfx.data.util.LanguageText;
import de.factoryfx.javafx.data.attribute.ColorAttribute;
import de.factoryfx.javafx.data.editor.attribute.builder.SimpleAttributeVisualisationBuilder;
import de.factoryfx.javafx.data.editor.attribute.builder.ValueAttributeVisualisationBuilder;
import de.factoryfx.javafx.data.util.UniformDesign;
import de.factoryfx.javafx.data.widget.datalistedit.ReferenceListAttributeEditWidget;

public class AttributeVisualisationMappingBuilder {

    private final List<de.factoryfx.javafx.data.editor.attribute.builder.AttributeVisualisationBuilder> singleAttributeEditorBuilders;

    public AttributeVisualisationMappingBuilder(List<de.factoryfx.javafx.data.editor.attribute.builder.AttributeVisualisationBuilder> singleAttributeEditorBuilders) {
        this.singleAttributeEditorBuilders = singleAttributeEditorBuilders;
    }

    public static List<de.factoryfx.javafx.data.editor.attribute.builder.AttributeVisualisationBuilder> createDefaultSingleAttributeEditorBuilders(UniformDesign uniformDesign){
        return createDefaultSingleAttributeEditorBuildersFunctions().stream().map((f)->f.apply(uniformDesign)).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public static List<Function<UniformDesign, de.factoryfx.javafx.data.editor.attribute.builder.AttributeVisualisationBuilder>> createDefaultSingleAttributeEditorBuildersFunctions(){
        ArrayList<Function<UniformDesign, de.factoryfx.javafx.data.editor.attribute.builder.AttributeVisualisationBuilder>> result = new ArrayList<>();


        result.add(uniformDesign->new SimpleAttributeVisualisationBuilder<PasswordAttribute>((attribute)->attribute instanceof PasswordAttribute,(attribute, navigateToData, previousData)->{
            return new PasswordAttributeVisualisation(attribute,new ValidationDecoration(uniformDesign),uniformDesign);
        }));

        result.add(uniformDesign->new ValueAttributeVisualisationBuilder<>(uniformDesign,BigDecimalAttribute.class,BigDecimal.class,(attribute)-> new BigDecimalAttributeVisualisation(attribute,new ValidationDecoration(uniformDesign)), BigDecimalAttribute::new));
        result.add(uniformDesign->new SimpleAttributeVisualisationBuilder<FileContentAttribute>((attribute)->attribute instanceof FileContentAttribute,(attribute, navigateToData, previousData)->{
            return new FileContentAttributeVisualisation(attribute,new ValidationDecoration(uniformDesign), uniformDesign);
        }));
        result.add(uniformDesign->new ValueAttributeVisualisationBuilder<>(uniformDesign,BooleanAttribute.class,Boolean.class,(attribute)-> new BooleanAttributeVisualisation(attribute,new ValidationDecoration(uniformDesign)), BooleanAttribute::new));
        result.add(uniformDesign->new ValueAttributeVisualisationBuilder<>(uniformDesign,ColorAttribute.class,Color.class,(attribute)-> new ColorAttributeVisualisation(attribute,new ValidationDecoration(uniformDesign)), ColorAttribute::new));
        result.add(uniformDesign->new ValueAttributeVisualisationBuilder<>(uniformDesign,DoubleAttribute.class,Double.class,(attribute)-> new DoubleAttributeVisualisation(attribute,new ValidationDecoration(uniformDesign)), DoubleAttribute::new));
        result.add(uniformDesign->new ValueAttributeVisualisationBuilder<>(uniformDesign,EncryptedStringAttribute.class,EncryptedString.class,(attribute)-> new EncryptedStringAttributeVisualisation(attribute, new ValidationDecoration(uniformDesign),uniformDesign), EncryptedStringAttribute::new));

        result.add(uniformDesign->new SimpleAttributeVisualisationBuilder((attribute)->attribute instanceof EnumAttribute,(attribute, navigateToData, previousData)->{
            return new EnumAttributeVisualisation((EnumAttribute)attribute, new ValidationDecoration(uniformDesign),uniformDesign);
        }));
        result.add(uniformDesign->new SimpleAttributeVisualisationBuilder((attribute)->attribute instanceof EnumListAttribute,(attribute, navigateToData, previousData)->{
            return new EnumListAttributeVisualisation((EnumListAttribute)attribute,new ValidationDecoration(uniformDesign),uniformDesign);
        }));


        result.add(uniformDesign->new ValueAttributeVisualisationBuilder<>(uniformDesign,I18nAttribute.class,LanguageText.class,(attribute)-> new I18nAttributeVisualisation(attribute,new ValidationDecoration(uniformDesign)), I18nAttribute::new));
        result.add(uniformDesign->new ValueAttributeVisualisationBuilder<>(uniformDesign,ShortAttribute.class,Short.class,(attribute)-> new ShortAttributeVisualisation(attribute,new ValidationDecoration(uniformDesign)), ShortAttribute::new));
        result.add(uniformDesign->new ValueAttributeVisualisationBuilder<>(uniformDesign,IntegerAttribute.class,Integer.class,(attribute)-> new IntegerAttributeVisualisation(attribute,new ValidationDecoration(uniformDesign)), IntegerAttribute::new));
        result.add(uniformDesign->new ValueAttributeVisualisationBuilder<>(uniformDesign,LocalDateAttribute.class,LocalDate.class,(attribute)-> new LocalDateAttributeVisualisation(attribute,new ValidationDecoration(uniformDesign)), LocalDateAttribute::new));
        result.add(uniformDesign->new ValueAttributeVisualisationBuilder<>(uniformDesign,LocalDateTimeAttribute.class,LocalDateTime.class,(attribute)-> new LocalDateTimeAttributeVisualisation(attribute,new ValidationDecoration(uniformDesign)), LocalDateTimeAttribute::new));
        result.add(uniformDesign->new ValueAttributeVisualisationBuilder<>(uniformDesign,LocalTimeAttribute.class,LocalTime.class,(attribute)-> new LocalTimeVisualisation(attribute,new ValidationDecoration(uniformDesign)), LocalTimeAttribute::new));

        result.add(uniformDesign->new ValueAttributeVisualisationBuilder<>(uniformDesign,LocaleAttribute.class,Locale.class,(attribute)-> new LocaleAttributeVisualisation(attribute,new ValidationDecoration(uniformDesign)), LocaleAttribute::new));
        result.add(uniformDesign->new ValueAttributeVisualisationBuilder<>(uniformDesign,LongAttribute.class,Long.class,(attribute)-> new LongAttributeVisualisation(attribute,new ValidationDecoration(uniformDesign)), LongAttribute::new));
        result.add(uniformDesign->new ValueAttributeVisualisationBuilder<>(uniformDesign, DurationAttribute.class, Duration.class, (attribute)-> new DurationAttributeVisualisation(attribute,new ValidationDecoration(uniformDesign)), DurationAttribute::new));
        result.add(uniformDesign->new ValueAttributeVisualisationBuilder<>(uniformDesign,StringAttribute.class,String.class,(attribute)->{
            if (attribute.internal_isLongText()){
                return new ExpandableAttributeVisualisation<>(new StringLongAttributeVisualisation(attribute,new ValidationDecoration(uniformDesign)),uniformDesign, (s)->Ascii.truncate(s,20,"..."),FontAwesome.Glyph.FONT,attribute.internal_isDefaultExpanded() );
            }
            if (attribute.internal_isHtmlText()){
                return new ExpandableAttributeVisualisation<>(new StringHtmlAttributeVisualisation(attribute,new ValidationDecoration(uniformDesign)),uniformDesign, (s)->Ascii.truncate(s,20,"..."),FontAwesome.Glyph.FONT,attribute.internal_isDefaultExpanded() );
            }
            return new StringAttributeVisualisation(attribute,new ValidationDecoration(uniformDesign));
        }, StringAttribute::new));
        result.add(uniformDesign->new ValueAttributeVisualisationBuilder<>(uniformDesign,URIAttribute.class,URI.class,(attribute)-> new URIAttributeVisualisation(attribute,new ValidationDecoration(uniformDesign)), URIAttribute::new));
        result.add(uniformDesign->new SimpleAttributeVisualisationBuilder((a)->a instanceof ViewReferenceAttribute,(attribute, navigateToData, previousData)-> new ViewReferenceAttributeVisualisation((ViewReferenceAttribute)attribute, new ValidationDecoration(uniformDesign), navigateToData, uniformDesign)));
        result.add(uniformDesign->new SimpleAttributeVisualisationBuilder((a)->a instanceof ViewListReferenceAttribute,(attribute, navigateToData, previousData)->{
            ViewListReferenceAttributeVisualisation visualisation = new ViewListReferenceAttributeVisualisation((ViewListReferenceAttribute)attribute,new ValidationDecoration(uniformDesign), navigateToData, uniformDesign);
            ExpandableAttributeVisualisation expandableAttributeVisualisation= new ExpandableAttributeVisualisation(visualisation,uniformDesign,(l)->"Items: "+((List<Data>)l).size(),FontAwesome.Glyph.LIST);
            if (((ViewListReferenceAttribute)attribute).get().contains(previousData)){
                expandableAttributeVisualisation.expand();
            }
            return expandableAttributeVisualisation;
        }));

        result.add(uniformDesign->new SimpleAttributeVisualisationBuilder((a)->a instanceof ReferenceAttribute,(attribute, navigateToData, previousData)->{
            ReferenceAttribute referenceAttribute =(ReferenceAttribute)attribute;
            if(referenceAttribute.internal_isCatalogueBased()){
                return new CatalogAttributeVisualisation(referenceAttribute::internal_possibleValues, referenceAttribute,new ValidationDecoration(uniformDesign));
            } else {
                return new ReferenceAttributeVisualisation(referenceAttribute,new ValidationDecoration(uniformDesign), uniformDesign, navigateToData);
            }
        }));

        result.add(uniformDesign->new SimpleAttributeVisualisationBuilder((a)->a instanceof ReferenceListAttribute,(attribute, navigateToData, previousData)->{
            ReferenceListAttribute referenceListAttribute =(ReferenceListAttribute)attribute;
            if(referenceListAttribute.internal_isCatalogueBased()){
                return new CatalogListAttributeVisualisation(referenceListAttribute,new ValidationDecoration(uniformDesign), uniformDesign);
            } else {
                final TableView dataTableView = new TableView();
                final ReferenceListAttributeVisualisation referenceListAttributeVisualisation = new ReferenceListAttributeVisualisation(referenceListAttribute,new ValidationDecoration(uniformDesign),uniformDesign, navigateToData, dataTableView, new ReferenceListAttributeEditWidget(dataTableView, navigateToData, uniformDesign,referenceListAttribute));
                ExpandableAttributeVisualisation expandableAttributeVisualisation = new ExpandableAttributeVisualisation(referenceListAttributeVisualisation, uniformDesign, (l) -> "Items: " + ((List<Data>)l).size(), FontAwesome.Glyph.LIST,referenceListAttribute.internal_isDefaultExpanded());
                if (referenceListAttribute.contains(previousData)) {
                    expandableAttributeVisualisation.expand();
                }
                return expandableAttributeVisualisation;
            }
        }));
        return result;
    }

    private AttributeVisualisation getAttributeEditorInternal(Attribute<?, ?> attribute, Consumer<Data> navigateToData, Data oldValue) {

        Optional<de.factoryfx.javafx.data.editor.attribute.builder.AttributeVisualisationBuilder> editorBuilderOptional = singleAttributeEditorBuilders.stream().filter(a -> a.isEditorFor(attribute)).findAny();
        if (editorBuilderOptional.isPresent()){
            return editorBuilderOptional.get().createVisualisation(attribute, navigateToData, oldValue);
        }
        if (attribute instanceof ValueListAttribute<?, ?>) {
            Optional<de.factoryfx.javafx.data.editor.attribute.builder.AttributeVisualisationBuilder> detailEditorBuilderOptional = singleAttributeEditorBuilders.stream().filter(a -> a.isListItemEditorFor(attribute)).findAny();
            if (detailEditorBuilderOptional.isPresent()){
                return detailEditorBuilderOptional.get().createValueListVisualisation(attribute);
            }
        }

        return new FallbackValueAttributeVisualisation();
    }

    public AttributeVisualisation getAttributeVisualisation(Attribute<?, ?> attribute, Consumer<Data> navigateToData, Data oldValue) {
        AttributeVisualisation attributeVisualisation = getAttributeEditorInternal(attribute,navigateToData,oldValue);
        if (attribute.internal_isUserReadOnly()){
            attributeVisualisation.setReadOnly();
        }
        return attributeVisualisation;
    }
}
