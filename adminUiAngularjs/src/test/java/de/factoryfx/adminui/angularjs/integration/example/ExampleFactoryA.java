package de.factoryfx.adminui.angularjs.integration.example;

import java.util.Optional;
import java.util.regex.Pattern;

import de.factoryfx.adminui.angularjs.integration.Permissions;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.attribute.AttributeMetadata;
import de.factoryfx.factory.attribute.ReferenceAttribute;
import de.factoryfx.factory.attribute.ReferenceListAttribute;
import de.factoryfx.factory.attribute.util.BigDecimalAttribute;
import de.factoryfx.factory.attribute.util.BooleanAttribute;
import de.factoryfx.factory.attribute.util.ByteArrayAttribute;
import de.factoryfx.factory.attribute.util.DoubleAttribute;
import de.factoryfx.factory.attribute.util.EnumAttribute;
import de.factoryfx.factory.attribute.util.I18nAttribute;
import de.factoryfx.factory.attribute.util.IntegerAttribute;
import de.factoryfx.factory.attribute.util.LongAttribute;
import de.factoryfx.factory.attribute.util.StringAttribute;
import de.factoryfx.factory.attribute.util.StringListAttribute;
import de.factoryfx.factory.attribute.util.StringMapAttribute;
import de.factoryfx.factory.validation.RegexValidation;
import de.factoryfx.factory.validation.StringRequired;

public class ExampleFactoryA extends FactoryBase<ExampleLiveObjectA,ExampleFactoryA> {

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

    public final ReferenceAttribute<ExampleLiveObjectB,ExampleFactoryB> referenceAttribute = new ReferenceAttribute<>(ExampleFactoryB.class,new AttributeMetadata().en("ReferenceAttribute").de("ReferenceAttribute de"));
    public final ReferenceListAttribute<ExampleLiveObjectB,ExampleFactoryB> referenceListAttribute = new ReferenceListAttribute<>(ExampleFactoryB.class,new AttributeMetadata().en("ReferenceListAttribute").de("ReferenceListAttribute de"));

    @Override
    protected ExampleLiveObjectA createImp(Optional<ExampleLiveObjectA> previousLiveObject) {
        return new ExampleLiveObjectA(referenceAttribute.instance(), referenceListAttribute.instances());
    }
}
