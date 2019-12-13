package io.github.factoryfx.javafx.editor.attribute;

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

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryBaseAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryViewListAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryViewAttribute;
import io.github.factoryfx.factory.attribute.types.PasswordAttribute;
import io.github.factoryfx.factory.metadata.AttributeMetadata;
import io.github.factoryfx.javafx.editor.attribute.builder.AttributeVisualisationBuilder;
import io.github.factoryfx.javafx.editor.attribute.builder.SimpleAttributeVisualisationBuilder;
import io.github.factoryfx.javafx.editor.attribute.builder.ValueAttributeVisualisationBuilder;
import io.github.factoryfx.javafx.editor.attribute.visualisation.*;
import io.github.factoryfx.javafx.widget.factory.listedit.FactoryListAttributeEditWidget;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;

import org.controlsfx.glyphfont.FontAwesome;

import com.google.common.base.Ascii;

import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryListBaseAttribute;
import io.github.factoryfx.factory.attribute.ValueListAttribute;
import io.github.factoryfx.factory.attribute.primitive.BooleanAttribute;
import io.github.factoryfx.factory.attribute.primitive.DoubleAttribute;
import io.github.factoryfx.factory.attribute.primitive.IntegerAttribute;
import io.github.factoryfx.factory.attribute.primitive.LongAttribute;
import io.github.factoryfx.factory.attribute.primitive.ShortAttribute;
import io.github.factoryfx.factory.attribute.time.DurationAttribute;
import io.github.factoryfx.factory.attribute.time.LocalDateAttribute;
import io.github.factoryfx.factory.attribute.time.LocalDateTimeAttribute;
import io.github.factoryfx.factory.attribute.time.LocalTimeAttribute;
import io.github.factoryfx.factory.attribute.types.FileContentAttribute;
import io.github.factoryfx.factory.attribute.types.BigDecimalAttribute;
import io.github.factoryfx.factory.attribute.types.EncryptedString;
import io.github.factoryfx.factory.attribute.types.EncryptedStringAttribute;
import io.github.factoryfx.factory.attribute.types.EnumAttribute;
import io.github.factoryfx.factory.attribute.types.EnumListAttribute;
import io.github.factoryfx.factory.attribute.types.I18nAttribute;
import io.github.factoryfx.factory.attribute.types.LocaleAttribute;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.attribute.types.URIAttribute;
import io.github.factoryfx.factory.util.LanguageText;
import io.github.factoryfx.javafx.util.UniformDesign;

public class AttributeVisualisationMappingBuilder {

    private final List<AttributeVisualisationBuilder> singleAttributeEditorBuilders;

    public AttributeVisualisationMappingBuilder(List<AttributeVisualisationBuilder> singleAttributeEditorBuilders) {
        this.singleAttributeEditorBuilders = singleAttributeEditorBuilders;
    }

    public static List<AttributeVisualisationBuilder> createDefaultSingleAttributeEditorBuilders(UniformDesign uniformDesign){
        return createDefaultSingleAttributeEditorBuildersFunctions().stream().map((f)->f.apply(uniformDesign)).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public static List<Function<UniformDesign, AttributeVisualisationBuilder>> createDefaultSingleAttributeEditorBuildersFunctions(){
        ArrayList<Function<UniformDesign, AttributeVisualisationBuilder>> result = new ArrayList<>();


        result.add(uniformDesign->new SimpleAttributeVisualisationBuilder<PasswordAttribute>((attribute)->attribute instanceof PasswordAttribute,(attribute,attributeMetadata, navigateToData, previousData)->{
            return new PasswordAttributeVisualisation(attribute,new ValidationDecoration(uniformDesign),uniformDesign);
        }));

        result.add(uniformDesign->new ValueAttributeVisualisationBuilder<>(uniformDesign,BigDecimalAttribute.class,BigDecimal.class,(attribute)-> new BigDecimalAttributeVisualisation(attribute,new ValidationDecoration(uniformDesign)), BigDecimalAttribute::new));
        result.add(uniformDesign->new SimpleAttributeVisualisationBuilder<FileContentAttribute>((attribute)->attribute instanceof FileContentAttribute,(attribute,attributeMetadata, navigateToData, previousData)->{
            return new FileContentAttributeVisualisation(attribute,new ValidationDecoration(uniformDesign), uniformDesign);
        }));
        result.add(uniformDesign->new ValueAttributeVisualisationBuilder<>(uniformDesign,BooleanAttribute.class,Boolean.class,(attribute)-> new BooleanAttributeVisualisation(attribute,new ValidationDecoration(uniformDesign)), BooleanAttribute::new));
        result.add(uniformDesign->new ValueAttributeVisualisationBuilder<>(uniformDesign, ColorAttribute.class,Color.class,(attribute)-> new ColorAttributeVisualisation(attribute,new ValidationDecoration(uniformDesign)), ColorAttribute::new));
        result.add(uniformDesign->new ValueAttributeVisualisationBuilder<>(uniformDesign,DoubleAttribute.class,Double.class,(attribute)-> new DoubleAttributeVisualisation(attribute,new ValidationDecoration(uniformDesign)), DoubleAttribute::new));
        result.add(uniformDesign->new ValueAttributeVisualisationBuilder<>(uniformDesign,EncryptedStringAttribute.class,EncryptedString.class,(attribute)-> new EncryptedStringAttributeVisualisation(attribute, new ValidationDecoration(uniformDesign),uniformDesign), EncryptedStringAttribute::new));

        result.add(uniformDesign->new SimpleAttributeVisualisationBuilder<>((attribute)->attribute instanceof EnumAttribute,(attribute,attributeMetadata, navigateToData, previousData)->{
            return new EnumAttributeVisualisation<>((EnumAttribute<?>)attribute,attributeMetadata, new ValidationDecoration(uniformDesign),uniformDesign);
        }));
        result.add(uniformDesign->new SimpleAttributeVisualisationBuilder<>((attribute)->attribute instanceof EnumListAttribute,(attribute,attributeMetadata, navigateToData, previousData)->{
            return new EnumListAttributeVisualisation<>((EnumListAttribute<?>)attribute,attributeMetadata,new ValidationDecoration(uniformDesign),uniformDesign);
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
        result.add(uniformDesign->new SimpleAttributeVisualisationBuilder<>((a)->a instanceof FactoryViewAttribute,(attribute, attributeMetadata, navigateToData, previousData)-> new ViewReferenceAttributeVisualisation<>((FactoryViewAttribute<?,?,?>)attribute, new ValidationDecoration(uniformDesign), navigateToData, uniformDesign)));
        result.add(uniformDesign->new SimpleAttributeVisualisationBuilder<>((a)->a instanceof FactoryViewListAttribute,(attribute, attributeMetadata, navigateToData, previousData)->{
            ViewListReferenceAttributeVisualisation visualisation = new ViewListReferenceAttributeVisualisation<>((FactoryViewListAttribute<?,?,?>)attribute,new ValidationDecoration(uniformDesign), navigateToData, uniformDesign);
            ExpandableAttributeVisualisation expandableAttributeVisualisation= new ExpandableAttributeVisualisation(visualisation,uniformDesign,(l)->"Items: "+((List<FactoryBase<?,?>>)l).size(),FontAwesome.Glyph.LIST);
            if (((FactoryViewListAttribute)attribute).get().contains(previousData)){
                expandableAttributeVisualisation.expand();
            }
            return expandableAttributeVisualisation;
        }));

        result.add(uniformDesign->new SimpleAttributeVisualisationBuilder((a)->a instanceof FactoryBaseAttribute,(attribute, attributeMetadata, navigateToData, previousData)->{
            FactoryBaseAttribute referenceAttribute =(FactoryBaseAttribute)attribute;
            if(referenceAttribute.internal_isCatalogueBased()){
                return new CatalogAttributeVisualisation(()->referenceAttribute.internal_possibleValues(attributeMetadata), referenceAttribute,new ValidationDecoration(uniformDesign));
            } else {
                return new FactoryAttributeVisualisation(referenceAttribute,attributeMetadata,new ValidationDecoration(uniformDesign), uniformDesign, navigateToData);
            }
        }));

        result.add(uniformDesign->new SimpleAttributeVisualisationBuilder((a)->a instanceof FactoryListBaseAttribute,(attribute,attributeMetadata, navigateToData, previousData)->{
            FactoryListBaseAttribute factoryListBaseAttribute =(FactoryListBaseAttribute)attribute;
            if(factoryListBaseAttribute.internal_isCatalogueBased()){
                return new CatalogListAttributeVisualisation(factoryListBaseAttribute,attributeMetadata,new ValidationDecoration(uniformDesign), uniformDesign);
            } else {
                final TableView dataTableView = new TableView();
                final FactoryListAttributeVisualisation referenceListAttributeVisualisation = new FactoryListAttributeVisualisation(factoryListBaseAttribute,new ValidationDecoration(uniformDesign),uniformDesign, navigateToData, dataTableView, new FactoryListAttributeEditWidget(dataTableView, navigateToData, uniformDesign, factoryListBaseAttribute,attributeMetadata));
                ExpandableAttributeVisualisation expandableAttributeVisualisation = new ExpandableAttributeVisualisation(referenceListAttributeVisualisation, uniformDesign, (l) -> "Items: " + ((List<FactoryBase<?,?>>)l).size(), FontAwesome.Glyph.LIST, factoryListBaseAttribute.internal_isDefaultExpanded());
                if (factoryListBaseAttribute.contains(previousData)) {
                    expandableAttributeVisualisation.expand();
                }
                return expandableAttributeVisualisation;
            }
        }));
        return result;
    }

    private AttributeVisualisation getAttributeEditorInternal(Attribute<?, ?> attribute, AttributeMetadata attributeMetadata, Consumer<FactoryBase<?,?>> navigateToData, FactoryBase<?,?> oldValue) {

        Optional<AttributeVisualisationBuilder> editorBuilderOptional = singleAttributeEditorBuilders.stream().filter(a -> a.isEditorFor(attribute)).findAny();
        if (editorBuilderOptional.isPresent()){
            return editorBuilderOptional.get().createVisualisation(attribute,attributeMetadata, navigateToData, oldValue);
        }
        if (attribute instanceof ValueListAttribute<?, ?>) {
            Optional<AttributeVisualisationBuilder> detailEditorBuilderOptional = singleAttributeEditorBuilders.stream().filter(a -> a.isListItemEditorFor(attribute)).findAny();
            if (detailEditorBuilderOptional.isPresent()){
                return detailEditorBuilderOptional.get().createValueListVisualisation(attribute,attributeMetadata);
            }
        }

        return new FallbackValueAttributeVisualisation();
    }

    public AttributeVisualisation getAttributeVisualisation(Attribute<?, ?> attribute, AttributeMetadata attributeMetadata, Consumer<FactoryBase<?,?>> navigateToData, FactoryBase<?,?> oldValue) {
        AttributeVisualisation attributeVisualisation = getAttributeEditorInternal(attribute,attributeMetadata,navigateToData,oldValue);
        if (attribute.internal_isUserReadOnly()){
            attributeVisualisation.setReadOnly();
        }
        return attributeVisualisation;
    }
}
