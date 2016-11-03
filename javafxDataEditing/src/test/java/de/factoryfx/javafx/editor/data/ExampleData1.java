package de.factoryfx.javafx.editor.data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ReferenceAttribute;
import de.factoryfx.data.attribute.ReferenceListAttribute;
import de.factoryfx.data.attribute.ValueListAttribute;
import de.factoryfx.data.attribute.types.BigDecimalAttribute;
import de.factoryfx.data.attribute.types.BooleanAttribute;
import de.factoryfx.data.attribute.types.ByteArrayAttribute;
import de.factoryfx.data.attribute.types.ColorAttribute;
import de.factoryfx.data.attribute.types.DoubleAttribute;
import de.factoryfx.data.attribute.types.EnumAttribute;
import de.factoryfx.data.attribute.types.I18nAttribute;
import de.factoryfx.data.attribute.types.IntegerAttribute;
import de.factoryfx.data.attribute.types.LocalDateAttribute;
import de.factoryfx.data.attribute.types.LocaleAttribute;
import de.factoryfx.data.attribute.types.LongAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.attribute.types.StringListAttribute;
import de.factoryfx.data.attribute.types.StringMapAttribute;
import de.factoryfx.data.attribute.types.TableAttribute;
import de.factoryfx.data.attribute.types.URIAttribute;
import de.factoryfx.data.attribute.types.URIListAttribute;
import de.factoryfx.data.validation.RegexValidation;
import de.factoryfx.data.validation.StringRequired;
import javafx.util.Pair;

public class ExampleData1 extends Data {

    public final TableAttribute tableAttribute = new TableAttribute(new AttributeMetadata().en("tableAttribute").de("tableAttribute de"));

    public final StringAttribute stringAttribute=new StringAttribute(new AttributeMetadata().en("StringAttribute").de("StringAttribute de")).validation(new StringRequired());
    public final StringAttribute stringLongAttribute=new StringAttribute(new AttributeMetadata().en("Long StringAttribute").de("Long StringAttribute de")).longText().validation(new StringRequired());

    public final StringAttribute regexValidationNumber=new StringAttribute(new AttributeMetadata().en("regexValidationNumber").de("regexValidationNumber de")).validation(new RegexValidation(Pattern.compile("[0-9]*")));
    public final BigDecimalAttribute bigDecimalAttribute=new BigDecimalAttribute(new AttributeMetadata().en("BigDecimalAttribute").de("BigDecimalAttribute de").addonText("EUR"));
    public final BooleanAttribute booleanAttribute=new BooleanAttribute(new AttributeMetadata().en("BooleanAttribute").de("BooleanAttribute de"));
    public final DoubleAttribute doubleAttribute=new DoubleAttribute(new AttributeMetadata().en("DoubleAttribute").de("DoubleAttribute de"));
    public final EnumAttribute<ExampleEnum> enumAttribute=new EnumAttribute<>(ExampleEnum.class,new AttributeMetadata().en("EnumAttribute").de("EnumAttribute de"));
    public final IntegerAttribute integerAttribute=new IntegerAttribute(new AttributeMetadata().en("IntegerAttribute").de("IntegerAttribute de"));
    public final LongAttribute longAttribute=new LongAttribute(new AttributeMetadata().en("LongAttribute").de("LongAttribute de"));
    public final StringListAttribute valueListAttribute=new StringListAttribute(new AttributeMetadata().en("ValueListAttribute").de("ValueListAttribute de"));
    public final ValueListAttribute<Integer> integerListAttribute=new ValueListAttribute<>(Integer.class,new AttributeMetadata().en("ValueListAttribute").de("ValueListAttribute de"),0);
    public final StringMapAttribute mapAttribute=new StringMapAttribute(new AttributeMetadata().en("MapAttribute").de("MapAttribute de"));
    public final I18nAttribute i18nAttribute=new I18nAttribute(new AttributeMetadata().en("i18nAttribute").de("i18nAttribute de")).en("envalue").de("devalue");
    public final ByteArrayAttribute byteArrayAttribute=new ByteArrayAttribute(new AttributeMetadata().en("byteArrayAttribute").de("byteArrayAttribute de"));

    public final ReferenceAttribute<ExampleData2> referenceAttribute = new ReferenceAttribute<>(ExampleData2.class,new AttributeMetadata().en("ReferenceAttribute").de("ReferenceAttribute de"));
    public final ReferenceListAttribute<ExampleData2> referenceListAttribute = new ReferenceListAttribute<>(ExampleData2.class,new AttributeMetadata().en("ReferenceListAttribute").de("ReferenceListAttribute de"));

    public final URIAttribute uriAttribute = new URIAttribute(new AttributeMetadata().en("URI"));
    public final URIListAttribute uriListAttribute = new URIListAttribute(new AttributeMetadata().en("URIList"));

    public final LocalDateAttribute localDateAttribute = new LocalDateAttribute(new AttributeMetadata().en("local date"));
    public final ColorAttribute colorAttribute=new ColorAttribute(new AttributeMetadata().en("colorAttribute").de("colorAttribute de"));
    public final LocaleAttribute localeAttribute =new LocaleAttribute(new AttributeMetadata().en("colorAttribute").de("colorAttribute de"));

    public final ReferenceAttribute<ExampleData2> referenceAttributereadonly = new ReferenceAttribute<>(ExampleData2.class,new AttributeMetadata().en("referenceAttributereadonly").de("referenceAttributereadonly de")).userReadOnly().defaultValue(new ExampleData2());


    public final StringAttribute specialAttribute=new StringAttribute(new AttributeMetadata().en("specialAttribute").de("specialAttribute de")).longText();

    public ExampleData1() {
        TableAttribute.Table value = new TableAttribute.Table();
        value.setColumnHeaders("Col1","Col2");
        tableAttribute.set(value);
    }

    String id= UUID.randomUUID().toString();
    @Override
    public Object getId() {
        return id;
    }

    @Override
    public void setId(Object object) {
        id=(String)object;
    }

    @Override
    public List<Pair<String,List<Attribute<?>>>> attributeListGrouped(){
        List<Pair<String,List<Attribute<?>>>> groups = new ArrayList<>(super.attributeListGrouped());
        groups.get(0).getValue().remove(specialAttribute);
        ArrayList<Attribute<?>> group = new ArrayList<>();
        group.add(specialAttribute);
        groups.add(new Pair<>("Specialxyz",group));
        return groups;
    }

}
