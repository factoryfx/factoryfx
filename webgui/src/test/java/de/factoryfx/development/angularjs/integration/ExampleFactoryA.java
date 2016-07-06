package de.factoryfx.development.angularjs.integration;

import java.util.ArrayList;
import java.util.Optional;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.attribute.AttributeMetadata;
import de.factoryfx.factory.attribute.BigDecimalAttribute;
import de.factoryfx.factory.attribute.BooleanAttribute;
import de.factoryfx.factory.attribute.DoubleAttribute;
import de.factoryfx.factory.attribute.EnumAttribute;
import de.factoryfx.factory.attribute.IntegerAttribute;
import de.factoryfx.factory.attribute.LongAttribute;
import de.factoryfx.factory.attribute.MapAttribute;
import de.factoryfx.factory.attribute.ReferenceAttribute;
import de.factoryfx.factory.attribute.ReferenceListAttribute;
import de.factoryfx.factory.attribute.StringAttribute;
import de.factoryfx.factory.attribute.ValueListAttribute;
import de.factoryfx.factory.attribute.ValueSetAttribute;
import de.factoryfx.factory.validation.StringRequired;

public class ExampleFactoryA extends FactoryBase<ExampleLiveObjectA,ExampleFactoryA> {

    public static enum ExampleEnum{
        EXAMPLE_1,
        EXAMPLE_2,
        EXAMPLE_3;
    }

    public final StringAttribute stringAttribute=new StringAttribute(new AttributeMetadata().en("ExampleA1en").de("ExampleA1ger").permission(Permissions.PERMISSON_X)).validation(new StringRequired());
    public final BigDecimalAttribute bigDecimalAttribute=new BigDecimalAttribute(new AttributeMetadata().en("BigDecimalAttribute").de("ExampleA1ger"));
    public final BooleanAttribute booleanAttribute=new BooleanAttribute(new AttributeMetadata().en("BooleanAttribute").de("ExampleA1ger"));
    public final DoubleAttribute doubleAttribute=new DoubleAttribute(new AttributeMetadata().en("DoubleAttribute").de("ExampleA1ger"));
    public final EnumAttribute<ExampleEnum> enumAttribute=new EnumAttribute<>(ExampleEnum.class,new AttributeMetadata().en("EnumAttribute").de("ExampleA1ger"));
    public final IntegerAttribute integerAttribute=new IntegerAttribute(new AttributeMetadata().en("IntegerAttribute").de("ExampleA1ger"));
    public final LongAttribute longAttribute=new LongAttribute(new AttributeMetadata().en("longAttribute").de("ExampleA1ger"));
    public final ValueListAttribute valueListAttribute=new ValueListAttribute(new AttributeMetadata().en("ValueListAttribute").de("ExampleA1ger"));
    public final ValueSetAttribute valueSetAttribute=new ValueSetAttribute(new AttributeMetadata().en("ValueSetAttribute").de("ExampleA1ger"));
    public final MapAttribute<String,String> mapAttribute=new MapAttribute<>(new AttributeMetadata().en("MapAttribute").de("ExampleA1ger"));

    public final ReferenceAttribute<ExampleFactoryB> referenceAttribute = new ReferenceAttribute<>(ExampleFactoryB.class,new AttributeMetadata().en("ExampleA2en").de("ExampleA2de"));
    public final ReferenceListAttribute<ExampleFactoryB> referenceListAttribute = new ReferenceListAttribute<>(ExampleFactoryB.class,new AttributeMetadata().en("ExampleA3en").de("ExampleA2de"));

    @Override
    protected ExampleLiveObjectA createImp(Optional<ExampleLiveObjectA> previousLiveObject) {
        ArrayList<ExampleLiveObjectB> exampleLiveObjectBs = new ArrayList<>();
        referenceListAttribute.get().forEach(exampleFactoryB -> {
            exampleLiveObjectBs.add(exampleFactoryB.create());
        });

        return new ExampleLiveObjectA(referenceAttribute.get().create(), exampleLiveObjectBs);
    }
}
