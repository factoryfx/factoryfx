package io.github.factoryfx.factory.typescript.generator.testserver;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.*;
import io.github.factoryfx.factory.attribute.primitive.*;
import io.github.factoryfx.factory.attribute.time.*;
import io.github.factoryfx.factory.attribute.types.*;
import io.github.factoryfx.jetty.JettyServerFactory;
import org.eclipse.jetty.server.Server;

import java.util.List;

public class TestServerFactory extends SimpleFactoryBase<Server, TestServerFactory> {
    public final FactoryAttribute<TestServerFactory,Server, JettyServerFactory<TestServerFactory>> server = new FactoryAttribute<>();

    public final ByteArrayAttribute byteArrayAttribute=new ByteArrayAttribute().nullable();
    public final I18nAttribute i18nAttribute=new I18nAttribute().nullable();
    public final EncryptedStringAttribute encryptedStringAttribute=new EncryptedStringAttribute().nullable();
    public final DoubleAttribute doubleAttribute=new DoubleAttribute().nullable();
    public final ByteAttribute byteAttribute=new ByteAttribute().nullable();
    public final BooleanAttribute booleanAttribute=new BooleanAttribute().nullable();
    public final LocalDateAttribute localDateAttribute=new LocalDateAttribute().nullable();
    public final EnumAttribute<ExampleEnum> enumAttribute=new EnumAttribute<>(ExampleEnum.class).nullable();
    public final CharAttribute charAttribute=new CharAttribute().nullable();
    public final LongAttribute longAttribute=new LongAttribute().nullable();
    public final StringAttribute stringAttribute=new StringAttribute();
    public final IntegerAttribute integerAttribute=new IntegerAttribute().nullable();
    public final LocalDateTimeAttribute localDateTimeAttribute=new LocalDateTimeAttribute().nullable();
    public final LocaleAttribute localeAttribute=new LocaleAttribute().nullable();
    public final DurationAttribute durationAttribute=new DurationAttribute().nullable();
    public final FileContentAttribute fileContentAttribute =new FileContentAttribute().nullable();
    public final LocalTimeAttribute localTimeAttribute=new LocalTimeAttribute().nullable();
    public final ObjectValueAttribute<Object> objectValueAttribute=new ObjectValueAttribute<>().nullable();
    public final ShortAttribute shortAttribute=new ShortAttribute().nullable();
    public final PasswordAttribute passwordAttribute=new PasswordAttribute().nullable();
    public final URIAttribute uriAttribute=new URIAttribute().nullable();
    public final BigDecimalAttribute bigDecimalAttribute=new BigDecimalAttribute().nullable();
    public final FloatAttribute floatAttribute=new FloatAttribute().nullable();
    public final StringListAttribute stringListAttribute=new StringListAttribute().nullable();
    public final EnumListAttribute<ExampleEnum> enumListAttribute=new EnumListAttribute<>(ExampleEnum.class).nullable();
    public final InstantAttribute instantAttribute=new InstantAttribute().nullable();
    public final BigIntegerAttribute bigIntegerAttribute=new BigIntegerAttribute().nullable();

    public final FactoryAttribute<TestServerFactory,Void, ExampleFactory> exampleFactory = new FactoryAttribute<>();
    public final FactoryListAttribute<TestServerFactory,Void, ExampleFactory> exampleListFactory = new FactoryListAttribute<>();
    public final FactoryViewAttribute<TestServerFactory,Void,ExampleFactory> view=new FactoryViewAttribute<>((root)->root.exampleFactory.get());
    public final FactoryViewListAttribute<TestServerFactory,Void,ExampleFactory> viewList=new FactoryViewListAttribute<>((root)-> {
        return List.of(root.exampleFactory.get(),root.exampleFactory.get(),root.exampleFactory.get());
    });



    @Override
    protected Server createImpl() {
        return server.instance();
    }
}