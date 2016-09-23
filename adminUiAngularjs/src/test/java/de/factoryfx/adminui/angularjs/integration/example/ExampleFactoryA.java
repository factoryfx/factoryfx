package de.factoryfx.adminui.angularjs.integration.example;

import java.util.Optional;
import java.util.regex.Pattern;

import de.factoryfx.adminui.angularjs.integration.Permissions;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.util.BigDecimalAttribute;
import de.factoryfx.data.attribute.util.BooleanAttribute;
import de.factoryfx.data.attribute.util.ByteArrayAttribute;
import de.factoryfx.data.attribute.util.DoubleAttribute;
import de.factoryfx.data.attribute.util.EnumAttribute;
import de.factoryfx.data.attribute.util.I18nAttribute;
import de.factoryfx.data.attribute.util.IntegerAttribute;
import de.factoryfx.data.attribute.util.LongAttribute;
import de.factoryfx.data.attribute.util.StringAttribute;
import de.factoryfx.data.attribute.util.StringListAttribute;
import de.factoryfx.data.attribute.util.StringMapAttribute;
import de.factoryfx.data.validation.RegexValidation;
import de.factoryfx.data.validation.StringRequired;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;

public class ExampleFactoryA extends FactoryBase<ExampleLiveObjectA> {

    public final StringAttribute stringAttribute=new StringAttribute(new AttributeMetadata().en("StringAttribute").de("StringAttribute de").permission(Permissions.PERMISSON_X)).validation(new StringRequired());
    public final StringAttribute regexValidationNumber=new StringAttribute(new AttributeMetadata().en("regexValidationNumber").de("regexValidationNumber de").permission(Permissions.PERMISSON_X)).validation(new RegexValidation(Pattern.compile("[0-9]*")));
    public final BigDecimalAttribute bigDecimalAttribute=new BigDecimalAttribute(new AttributeMetadata().en("BigDecimalAttribute").de("BigDecimalAttribute de").addonText("EUR"));
    public final BooleanAttribute booleanAttribute=new BooleanAttribute(new AttributeMetadata().en("BooleanAttribute").de("BooleanAttribute de"));
    public final DoubleAttribute doubleAttribute=new DoubleAttribute(new AttributeMetadata().en("DoubleAttribute").de("DoubleAttribute de"));
    public final EnumAttribute<ExampleEnum> enumAttribute=new EnumAttribute<>(ExampleEnum.class,new AttributeMetadata().en("EnumAttribute").de("EnumAttribute de"));
    public final IntegerAttribute integerAttribute=new IntegerAttribute(new AttributeMetadata().en("IntegerAttribute").de("IntegerAttribute de"));
    public final LongAttribute longAttribute=new LongAttribute(new AttributeMetadata().en("LongAttribute").de("LongAttribute de"));
    public final StringListAttribute valueListAttribute=new StringListAttribute(new AttributeMetadata().en("ValueListAttribute").de("ValueListAttribute de"));
    public final StringMapAttribute mapAttribute=new StringMapAttribute(new AttributeMetadata().en("MapAttribute").de("MapAttribute de"));
    public final I18nAttribute i18nAttribute=new I18nAttribute(new AttributeMetadata().en("i18nAttribute").de("i18nAttribute de")).en("envalue").de("devalue");
    public final ByteArrayAttribute byteArrayAttribute=new ByteArrayAttribute(new AttributeMetadata().en("byteArrayAttribute").de("byteArrayAttribute de"));

    public final FactoryReferenceAttribute<ExampleLiveObjectB,ExampleFactoryB> referenceAttribute = new FactoryReferenceAttribute<>(ExampleFactoryB.class,new AttributeMetadata().en("ReferenceAttribute").de("ReferenceAttribute de"));
    public final FactoryReferenceListAttribute<ExampleLiveObjectB,ExampleFactoryB> referenceListAttribute = new FactoryReferenceListAttribute<>(ExampleFactoryB.class,new AttributeMetadata().en("ReferenceListAttribute").de("ReferenceListAttribute de"));

    @Override
    protected ExampleLiveObjectA createImp(Optional<ExampleLiveObjectA> previousLiveObject) {
        return new ExampleLiveObjectA(referenceAttribute.instance(), referenceListAttribute.instances());
    }
}
