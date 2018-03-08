package de.factoryfx.javafx.editor.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.AttributeGroup;
import de.factoryfx.data.attribute.DataReferenceAttribute;
import de.factoryfx.data.attribute.DataReferenceListAttribute;
import de.factoryfx.data.attribute.primitive.BooleanAttribute;
import de.factoryfx.data.attribute.primitive.DoubleAttribute;
import de.factoryfx.data.attribute.primitive.IntegerAttribute;
import de.factoryfx.data.attribute.primitive.LongAttribute;
import de.factoryfx.data.attribute.primitive.list.IntegerListAttribute;
import de.factoryfx.data.attribute.time.DurationAttribute;
import de.factoryfx.data.attribute.time.LocalDateAttribute;
import de.factoryfx.data.attribute.time.LocalDateTimeAttribute;
import de.factoryfx.data.attribute.types.BigDecimalAttribute;
import de.factoryfx.data.attribute.types.ByteArrayAttribute;
import de.factoryfx.data.attribute.types.ColorAttribute;
import de.factoryfx.data.attribute.types.EncryptedStringAttribute;
import de.factoryfx.data.attribute.types.EnumAttribute;
import de.factoryfx.data.attribute.types.I18nAttribute;
import de.factoryfx.data.attribute.types.LocaleAttribute;
import de.factoryfx.data.attribute.types.PasswordAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.attribute.types.StringListAttribute;
import de.factoryfx.data.attribute.types.StringMapAttribute;
import de.factoryfx.data.attribute.types.URIAttribute;
import de.factoryfx.data.attribute.types.URIListAttribute;
import de.factoryfx.data.util.LanguageText;
import de.factoryfx.data.validation.RegexValidation;
import de.factoryfx.data.validation.StringRequired;
import de.factoryfx.data.validation.Validation;
import de.factoryfx.data.validation.ValidationResult;
import de.factoryfx.factory.atrribute.FactoryPolymorphicReferenceAttribute;
import de.factoryfx.factory.testfactories.poly.ErrorPrinterFactory;
import de.factoryfx.factory.testfactories.poly.OutPrinterFactory;
import de.factoryfx.factory.testfactories.poly.Printer;

public class ExampleData1 extends Data {
    public final FactoryPolymorphicReferenceAttribute<Printer> reference = new FactoryPolymorphicReferenceAttribute<Printer>().setup(Printer.class,ErrorPrinterFactory.class,OutPrinterFactory.class).labelText("poly");
    public final PasswordAttribute passwordAttribute = new PasswordAttribute().en("PasswordAttribute").de("PasswordAttribute de");
    public final DurationAttribute durationAttribute = new DurationAttribute().en("durationAttribute").de("durationAttribute de");

    public final EncryptedStringAttribute encryptedStringAttribute=new EncryptedStringAttribute().en("encryptedStringAttribute").de("StringAttribute de").tooltipDe("tooltip xyz");

    public final StringAttribute stringAttribute=new StringAttribute().en("StringAttribute gajsd jgsdajh gjasdja jhsadgjg ghf hgf hgfhff hgfhgf hf").de("StringAttribute de").validation(StringRequired.VALIDATION).defaultValue("blub");
    public final StringAttribute stringLongAttribute=new StringAttribute().longText().defaultExpanded(true).validation(new StringRequired()).en("Long StringAttribute").de("Long StringAttribute de");
    public final StringAttribute stringHtmlAttribute=new StringAttribute().htmlText().en("stringHtmlAttribute").de("stringHtmlAttribute de");

    public final StringAttribute regexValidationNumber=new StringAttribute().en("regexValidationNumber").de("regexValidationNumber de").validation(new RegexValidation(Pattern.compile("[0-9]*")));
    public final BigDecimalAttribute bigDecimalAttribute=new BigDecimalAttribute().en("BigDecimalAttribute").de("BigDecimalAttribute de").addonText("EUR");
    public final BooleanAttribute booleanAttribute=new BooleanAttribute().en("BooleanAttribute").de("BooleanAttribute de");
    public final DoubleAttribute doubleAttribute=new DoubleAttribute().en("DoubleAttribute").de("DoubleAttribute de");
    public final EnumAttribute<ExampleEnum> enumAttribute=new EnumAttribute<>(ExampleEnum.class).en("EnumAttribute").de("EnumAttribute de");
    public final IntegerAttribute integerAttribute=new IntegerAttribute().en("IntegerAttribute").de("IntegerAttribute de");
    public final LongAttribute longAttribute=new LongAttribute().en("LongAttribute").de("LongAttribute de");
    public final StringListAttribute valueListAttribute=new StringListAttribute().en("ValueListAttribute").de("ValueListAttribute de");
    public final IntegerListAttribute integerListAttribute=new IntegerListAttribute().en("ValueListAttribute").de("ValueListAttribute de");
    public final StringMapAttribute mapAttribute=new StringMapAttribute().en("MapAttribute").de("MapAttribute de");
    public final I18nAttribute i18nAttribute=new I18nAttribute().en("i18nAttribute").de("i18nAttribute de").en("envalue").de("devalue");
    public final ByteArrayAttribute byteArrayAttribute=new ByteArrayAttribute().en("byteArrayAttribute").de("byteArrayAttribute de");

    public final DataReferenceAttribute<ExampleData2> referenceAttribute = new DataReferenceAttribute<ExampleData2>().setup(ExampleData2.class).en("ReferenceAttribute").de("ReferenceAttribute de");
    public final DataReferenceListAttribute<ExampleData2> referenceListAttribute = new DataReferenceListAttribute<ExampleData2>().setup(ExampleData2.class).en("ReferenceListAttribute").de("ReferenceListAttribute de");

    public final DataReferenceAttribute<ExampleData2> referenceAttributeCat = new DataReferenceAttribute<ExampleData2>().setup(ExampleData2.class).catalogueBased().en("ReferenceAttribute catalog based").de("ReferenceAttribute catalog based de");
    public final DataReferenceListAttribute<ExampleData2> referenceListAttributeCat = new DataReferenceListAttribute<ExampleData2>().setup(ExampleData2.class).catalogueBased().en("ReferenceListAttribute catalog based").de("ReferenceListAttribute catalog based de");
    public final DataReferenceListAttribute<ExampleData2> readOnlyReferenceListAttributeCat = new DataReferenceListAttribute<ExampleData2>().setup(ExampleData2.class).userReadOnly().catalogueBased().en("Readonly referenceListAttribute catalog based").de("Lesend ReferenceListAttribute catalog based de");

    public final URIAttribute uriAttribute = new URIAttribute().en("URI");
    public final URIListAttribute uriListAttribute = new URIListAttribute().en("URIList");

    public final LocalDateAttribute localDateAttribute = new LocalDateAttribute().en("local date");
    public final LocalDateTimeAttribute localDateTimeAttribute = new LocalDateTimeAttribute().en("local date time");
    public final ColorAttribute colorAttribute=new ColorAttribute().en("colorAttribute").de("colorAttribute de");
    public final LocaleAttribute localeAttribute =new LocaleAttribute().en("colorAttribute").de("colorAttribute de");

    public final DataReferenceAttribute<ExampleData2> referenceAttributereadonly = new DataReferenceAttribute<ExampleData2>().userReadOnly().defaultValue(new ExampleData2()).en("referenceAttributereadonly").de("referenceAttributereadonly de");


    public final StringAttribute specialAttribute=new StringAttribute().longText().en("specialAttribute").de("specialAttribute de");

    public ExampleData1() {
        config().addValidation(new Validation<ExampleData1>() {
            @Override
            public ValidationResult validate(ExampleData1 value) {
                if (value.integerAttribute.get()==null){
                    return new ValidationResult(false,new LanguageText().de("long = int"));
                }
                if (value.longAttribute.get()==null){
                    return new ValidationResult(false,new LanguageText().de("long = int"));
                }
                return new ValidationResult(value.integerAttribute.get().intValue()!=value.longAttribute.get().longValue(),new LanguageText().de("long = int"));
            }
        },integerAttribute,longAttribute);



        config().setAttributeListGroupedSupplier((List<Attribute<?,?>> defaultGroup)->attributeListGrouped(defaultGroup));

        config().setDisplayTextProvider(() -> stringAttribute.get());
        config().setDisplayTextDependencies(stringAttribute);
    }

    public Node customize(Node defaultVis) {
        final BorderPane borderPane = new BorderPane();
        borderPane.setCenter(defaultVis);
        borderPane.setBottom(new Button("random button als example for custom vis"));
        return borderPane;
    }

    private List<AttributeGroup> attributeListGrouped(List<Attribute<?,?>> defaultGroup ){
        List<Attribute<?,?>> result = new ArrayList<>(defaultGroup);

        result.remove(specialAttribute);
        ArrayList<Attribute<?,?>> group = new ArrayList<>();
        group.add(specialAttribute);
        AttributeGroup defaultdata  = new AttributeGroup("Data", result);
        AttributeGroup specialxyz = new AttributeGroup("Specialxyz", group);
        return Arrays.asList(defaultdata, specialxyz);
    }

}
