package de.factoryfx.javafx.editor.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.*;
import de.factoryfx.data.attribute.primitive.list.IntegerListAttribute;
import de.factoryfx.data.attribute.time.DurationAttribute;
import de.factoryfx.data.attribute.time.LocalDateAttribute;
import de.factoryfx.data.attribute.time.LocalDateTimeAttribute;
import de.factoryfx.data.attribute.types.*;
import de.factoryfx.data.attribute.primitive.BooleanAttribute;
import de.factoryfx.data.attribute.primitive.DoubleAttribute;
import de.factoryfx.data.attribute.primitive.IntegerAttribute;
import de.factoryfx.data.attribute.primitive.LongAttribute;
import de.factoryfx.data.util.LanguageText;
import de.factoryfx.data.validation.RegexValidation;
import de.factoryfx.data.validation.StringRequired;
import de.factoryfx.data.validation.Validation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.util.Pair;

public class ExampleData1 extends Data {
    public final DurationAttribute durationAttribute = new DurationAttribute().en("durationAttribute").de("durationAttribute de");

    public final DataReferenceAttribute<DynamicData> dynamicDataAttribute = new DataReferenceAttribute<DynamicData>().setup(DynamicData.class).en("dynamicDataAttribute").de("dynamicDataAttribute de");

    public final EncryptedStringAttribute encryptedStringAttribute=new EncryptedStringAttribute().en("encryptedStringAttribute").de("StringAttribute de");

    public final StringAttribute stringAttribute=new StringAttribute().en("StringAttribute gajsd jgsdajh gjasdja jhsadgjg ghf hgf hgfhff hgfhgf hf").de("StringAttribute de").validation(new StringRequired()).defaultValue("blub");
    public final StringAttribute stringLongAttribute=new StringAttribute().longText().defaultExpanded(true).validation(new StringRequired()).en("Long StringAttribute").de("Long StringAttribute de");

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
            public LanguageText getValidationDescription() {
                return new LanguageText().de("long = int");
            }

            @Override
            public boolean validate(ExampleData1 value) {
                if (value.integerAttribute.get()==null){
                    return true;
                }
                if (value.longAttribute.get()==null){
                    return true;
                }
                return value.integerAttribute.get().intValue()==value.longAttribute.get().longValue();
            }
        },integerAttribute,longAttribute);



        config().setAttributeListGroupedSupplier((List<Attribute<?,?>> defaultGroup)->attributeListGrouped(defaultGroup));

        config().setDisplayTextProvider(() -> stringAttribute.get());
        config().setDisplayTextDependencies(stringAttribute);
    }



    String id= UUID.randomUUID().toString();

    public Node customize(Node defaultVis) {
        final BorderPane borderPane = new BorderPane();
        borderPane.setCenter(defaultVis);
        borderPane.setBottom(new Button("random button"));
        return borderPane;
    }



    private List<Pair<String,List<Attribute<?,?>>>> attributeListGrouped(List<Attribute<?,?>> defaultGroup ){
        List<Attribute<?,?>> result = new ArrayList<>(defaultGroup);

        result.remove(specialAttribute);
        ArrayList<Attribute<?,?>> group = new ArrayList<>();
        group.add(specialAttribute);
        Pair<String, List<Attribute<?,?>>> defaultdata  = new Pair<>("Data", result);
        Pair<String, List<Attribute<?,?>>> specialxyz = new Pair<>("Specialxyz", group);
        return Arrays.asList(defaultdata, specialxyz);
    }

}
