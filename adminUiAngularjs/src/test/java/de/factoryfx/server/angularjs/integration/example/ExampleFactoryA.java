package de.factoryfx.server.angularjs.integration.example;

import java.util.regex.Pattern;

import de.factoryfx.data.attribute.types.BigDecimalAttribute;
import de.factoryfx.data.attribute.primitive.BooleanAttribute;
import de.factoryfx.data.attribute.types.ByteArrayAttribute;
import de.factoryfx.data.attribute.primitive.DoubleAttribute;
import de.factoryfx.data.attribute.types.EnumAttribute;
import de.factoryfx.data.attribute.types.I18nAttribute;
import de.factoryfx.data.attribute.primitive.IntegerAttribute;
import de.factoryfx.data.attribute.primitive.LongAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.attribute.types.StringListAttribute;
import de.factoryfx.data.attribute.types.StringMapAttribute;
import de.factoryfx.data.validation.RegexValidation;
import de.factoryfx.data.validation.StringRequired;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;
import de.factoryfx.server.angularjs.integration.Permissions;

public class ExampleFactoryA extends FactoryBase<ExampleLiveObjectA,ExampleVisitor> {

    public final StringAttribute stringAttribute=new StringAttribute().en("StringAttribute").de("StringAttribute de").permission(Permissions.PERMISSON_X).validation(StringRequired.VALIDATION);
    public final StringAttribute regexValidationNumber=new StringAttribute().en("regexValidationNumber").de("regexValidationNumber de").permission(Permissions.PERMISSON_X).validation(new RegexValidation(Pattern.compile("[0-9]*")));
    public final BigDecimalAttribute bigDecimalAttribute=new BigDecimalAttribute().en("BigDecimalAttribute").de("BigDecimalAttribute de").addonText("EUR");
    public final BooleanAttribute booleanAttribute=new BooleanAttribute().en("BooleanAttribute").de("BooleanAttribute de");
    public final DoubleAttribute doubleAttribute=new DoubleAttribute().en("DoubleAttribute").de("DoubleAttribute de");
    public final EnumAttribute<ExampleEnum> enumAttribute=new EnumAttribute<>(ExampleEnum.class).en("EnumAttribute").de("EnumAttribute de");
    public final IntegerAttribute integerAttribute=new IntegerAttribute().en("IntegerAttribute").de("IntegerAttribute de");
    public final LongAttribute longAttribute=new LongAttribute().en("LongAttribute").de("LongAttribute de");
    public final StringListAttribute valueListAttribute=new StringListAttribute().en("ValueListAttribute").de("ValueListAttribute de");
    public final StringMapAttribute mapAttribute=new StringMapAttribute().en("MapAttribute").de("MapAttribute de");
    public final I18nAttribute i18nAttribute=new I18nAttribute().en("i18nAttribute").en("envalue").de("devalue");
    public final ByteArrayAttribute byteArrayAttribute=new ByteArrayAttribute().en("byteArrayAttribute").de("byteArrayAttribute de");

    public final FactoryReferenceAttribute<ExampleLiveObjectB,ExampleFactoryB> referenceAttribute = new FactoryReferenceAttribute<>(ExampleFactoryB.class).en("ReferenceAttribute").de("ReferenceAttribute de");
    public final FactoryReferenceListAttribute<ExampleLiveObjectB,ExampleFactoryB> referenceListAttribute = new FactoryReferenceListAttribute<>(ExampleFactoryB.class).en("ReferenceListAttribute").de("ReferenceListAttribute de");

    public ExampleFactoryA(){
        configLiveCycle().setCreator(() -> new ExampleLiveObjectA(referenceAttribute.instance(), referenceListAttribute.instances()));
        configLiveCycle().setRuntimeQueryExecutor((visitor, exampleLiveObjectA) -> {
            visitor.exampleDates.add(new ExampleData("a","b","c"));
            visitor.exampleDates.add(new ExampleData("a2","b2","c2"));
        });
    }
}
