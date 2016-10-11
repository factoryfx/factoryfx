package de.factoryfx.javafx.editor.data;

import java.util.UUID;
import java.util.regex.Pattern;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ReferenceAttribute;
import de.factoryfx.data.attribute.ReferenceListAttribute;
import de.factoryfx.data.attribute.ValueListAttribute;
import de.factoryfx.data.attribute.types.*;
import de.factoryfx.data.validation.RegexValidation;
import de.factoryfx.data.validation.StringRequired;

public class ExampleData1 extends Data {

    public final StringAttribute stringAttribute=new StringAttribute(new AttributeMetadata().en("StringAttribute").de("StringAttribute de")).validation(new StringRequired());
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

    String id= UUID.randomUUID().toString();
    @Override
    public Object getId() {
        return id;
    }

    @Override
    public void setId(Object object) {
        id=(String)object;
    }
}
