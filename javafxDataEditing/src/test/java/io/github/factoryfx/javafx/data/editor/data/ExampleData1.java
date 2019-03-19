package io.github.factoryfx.javafx.data.editor.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

import io.github.factoryfx.data.Data;
import io.github.factoryfx.data.attribute.Attribute;
import io.github.factoryfx.data.attribute.AttributeGroup;
import io.github.factoryfx.data.attribute.DataReferenceAttribute;
import io.github.factoryfx.data.attribute.DataReferenceListAttribute;
import io.github.factoryfx.data.attribute.primitive.BooleanAttribute;
import io.github.factoryfx.data.attribute.primitive.DoubleAttribute;
import io.github.factoryfx.data.attribute.primitive.IntegerAttribute;
import io.github.factoryfx.data.attribute.primitive.LongAttribute;
import io.github.factoryfx.data.attribute.primitive.list.IntegerListAttribute;
import io.github.factoryfx.data.attribute.time.DurationAttribute;
import io.github.factoryfx.data.attribute.time.LocalDateAttribute;
import io.github.factoryfx.data.attribute.time.LocalDateTimeAttribute;
import io.github.factoryfx.data.attribute.types.FileContentAttribute;
import io.github.factoryfx.data.attribute.types.BigDecimalAttribute;
import io.github.factoryfx.data.attribute.types.ByteArrayAttribute;
import io.github.factoryfx.data.attribute.types.EncryptedStringAttribute;
import io.github.factoryfx.data.attribute.types.EnumAttribute;
import io.github.factoryfx.data.attribute.types.EnumListAttribute;
import io.github.factoryfx.data.attribute.types.I18nAttribute;
import io.github.factoryfx.data.attribute.types.LocaleAttribute;
import io.github.factoryfx.data.attribute.types.PasswordAttribute;
import io.github.factoryfx.data.attribute.types.StringAttribute;
import io.github.factoryfx.data.attribute.types.StringListAttribute;
import io.github.factoryfx.data.attribute.types.StringMapAttribute;
import io.github.factoryfx.data.attribute.types.URIAttribute;
import io.github.factoryfx.data.attribute.types.URIListAttribute;
import io.github.factoryfx.data.util.LanguageText;
import io.github.factoryfx.data.validation.RegexValidation;
import io.github.factoryfx.data.validation.Validation;
import io.github.factoryfx.data.validation.ValidationResult;
import io.github.factoryfx.factory.atrribute.FactoryPolymorphicReferenceAttribute;
import io.github.factoryfx.factory.atrribute.FactoryPolymorphicReferenceListAttribute;
import io.github.factoryfx.factory.testfactories.poly.ErrorPrinterFactory;
import io.github.factoryfx.factory.testfactories.poly.OutPrinterFactory;
import io.github.factoryfx.factory.testfactories.poly.Printer;
import io.github.factoryfx.javafx.data.attribute.ColorAttribute;


public class ExampleData1 extends Data {
    public final FactoryPolymorphicReferenceAttribute<Printer> reference = new FactoryPolymorphicReferenceAttribute<Printer>().setup(Printer.class,ErrorPrinterFactory.class,OutPrinterFactory.class).labelText("poly");
    public final PasswordAttribute passwordAttribute = new PasswordAttribute().en("PasswordAttribute").de("PasswordAttribute de");
    public final DurationAttribute durationAttribute = new DurationAttribute().en("durationAttribute").de("durationAttribute de");

    public final EncryptedStringAttribute encryptedStringAttribute=new EncryptedStringAttribute().en("encryptedStringAttribute").de("StringAttribute de").tooltipDe("tooltip xyz");

    public final FileContentAttribute fileContentAttribute =new FileContentAttribute().en("Base64Attribute sda jgsdajh gjasdja jhsadgjg ghfgfds hgf hgfthrwhff hgfhgf hf").de("Base64Attribute de").nullable();


    public final StringAttribute stringAttribute=new StringAttribute().en("StringAttribute gajsd jgsdajh gjasdja jhsadgjg ghf hgf hgfhff hgfhgf hf").de("StringAttribute de").defaultValue("blub");
    public final StringAttribute stringLongAttribute=new StringAttribute().longText().defaultExpanded().en("Long StringAttribute").de("Long StringAttribute de");
    public final StringAttribute stringHtmlAttribute=new StringAttribute().htmlText().en("stringHtmlAttribute").de("stringHtmlAttribute de");

    public final StringAttribute regexValidationNumber=new StringAttribute().en("regexValidationNumber").de("regexValidationNumber de").validation(new RegexValidation(Pattern.compile("[0-9]*")));
    public final BigDecimalAttribute bigDecimalAttribute=new BigDecimalAttribute().en("BigDecimalAttribute").de("BigDecimalAttribute de").addonText("EUR");
    public final BooleanAttribute booleanAttribute=new BooleanAttribute().en("BooleanAttribute").de("BooleanAttribute de");
    public final DoubleAttribute doubleAttribute=new DoubleAttribute().en("DoubleAttribute").de("DoubleAttribute de");
    public final EnumAttribute<ExampleEnum> enumAttribute=new EnumAttribute<>(ExampleEnum.class).en("EnumAttribute").de("EnumAttribute de").enEnum(ExampleEnum.EXAMPLE_1,"Example_1").deEnum(ExampleEnum.EXAMPLE_1,"Beispiel_1");
    public final EnumListAttribute<ExampleEnum> enumListAttribute=new EnumListAttribute<>(ExampleEnum.class).en("EnumListAttribute").de("EnumListAttribute de").enEnum(ExampleEnum.EXAMPLE_1,"Example_1").deEnum(ExampleEnum.EXAMPLE_1,"Beispiel_1");
    public final IntegerAttribute integerAttribute=new IntegerAttribute().en("IntegerAttribute").de("IntegerAttribute de");
    public final LongAttribute longAttribute=new LongAttribute().en("LongAttribute").de("LongAttribute de");
    public final StringListAttribute valueListAttribute=new StringListAttribute().en("StringListAttribute").de("StringListAttribute de");
    public final IntegerListAttribute integerListAttribute=new IntegerListAttribute().en("ValueListAttribute").de("ValueListAttribute de");
    public final StringMapAttribute mapAttribute=new StringMapAttribute().en("MapAttribute").de("MapAttribute de");
    public final I18nAttribute i18nAttribute=new I18nAttribute().en("i18nAttribute").de("i18nAttribute de").en("envalue").de("devalue");
    public final ByteArrayAttribute byteArrayAttribute=new ByteArrayAttribute().en("byteArrayAttribute").de("byteArrayAttribute de");

    public final DataReferenceAttribute<ExampleData2> referenceAttribute = new DataReferenceAttribute<>(ExampleData2.class).en("ReferenceAttribute").de("ReferenceAttribute de");
    public final DataReferenceListAttribute<ExampleData2> referenceListAttribute = new DataReferenceListAttribute<>(ExampleData2.class).en("ReferenceListAttribute").de("ReferenceListAttribute de");

    public final DataReferenceAttribute<ExampleData2> referenceAttributeCat = new DataReferenceAttribute<>(ExampleData2.class).catalogueBased().en("ReferenceAttribute catalog based").de("ReferenceAttribute catalog based de");
    public final DataReferenceListAttribute<ExampleData2> referenceListAttributeCat = new DataReferenceListAttribute<>(ExampleData2.class).catalogueBased().en("ReferenceListAttribute catalog based").de("ReferenceListAttribute catalog based de");
    public final DataReferenceListAttribute<ExampleData2> readOnlyReferenceListAttributeCat = new DataReferenceListAttribute<>(ExampleData2.class).userReadOnly().catalogueBased().en("Readonly referenceListAttribute catalog based").de("Lesend ReferenceListAttribute catalog based de");

    public final URIAttribute uriAttribute = new URIAttribute().en("URI");
    public final URIListAttribute uriListAttribute = new URIListAttribute().en("URIList");

    public final LocalDateAttribute localDateAttribute = new LocalDateAttribute().en("local date");
    public final LocalDateTimeAttribute localDateTimeAttribute = new LocalDateTimeAttribute().en("local date time");
    public final ColorAttribute colorAttribute=new ColorAttribute().en("colorAttribute").de("colorAttribute de");
    public final LocaleAttribute localeAttribute =new LocaleAttribute().en("colorAttribute").de("colorAttribute de");

    public final DataReferenceAttribute<ExampleData2> referenceAttributeReadonly = new DataReferenceAttribute<>(ExampleData2.class).userReadOnly().en("referenceAttributereadonly").de("referenceAttribute readonly de");
    public final FactoryPolymorphicReferenceListAttribute<Printer> polymorphicReferenceList = new FactoryPolymorphicReferenceListAttribute<Printer>().setup(Printer.class, ErrorPrinterFactory.class, OutPrinterFactory.class).en("polymorphicreferenceList").de("polymorphicreferenceList");


    public final StringAttribute specialAttribute=new StringAttribute().longText().en("specialAttribute").de("specialAttribute de");

    public ExampleData1() {
        config().addValidation((Validation<ExampleData1>) value -> {
            if (value.integerAttribute.get()==null){
                return new ValidationResult(false,new LanguageText().de("long = int"));
            }
            if (value.longAttribute.get()==null){
                return new ValidationResult(false,new LanguageText().de("long = int"));
            }
            return new ValidationResult(value.integerAttribute.get().intValue()!=value.longAttribute.get().longValue(),new LanguageText().de("long = int"));
        }, integerAttribute, longAttribute);



        config().setAttributeListGroupedSupplier(this::attributeListGrouped);

        config().setDisplayTextProvider(stringAttribute::get);
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
