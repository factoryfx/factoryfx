package io.github.factoryfx.factory.typescript.generator.data;

import io.github.factoryfx.data.Data;
import io.github.factoryfx.data.attribute.primitive.*;
import io.github.factoryfx.data.attribute.time.*;
import io.github.factoryfx.data.attribute.types.*;

public class ExampleDataAll extends Data {
    public final ByteArrayAttribute byteArrayAttribute=new ByteArrayAttribute();
    public final I18nAttribute i18nAttribute=new I18nAttribute();
    public final EncryptedStringAttribute encryptedStringAttribute=new EncryptedStringAttribute();
    public final DoubleAttribute doubleAttribute=new DoubleAttribute();
    public final ByteAttribute byteAttribute=new ByteAttribute();
    public final BooleanAttribute booleanAttribute=new BooleanAttribute();
    public final LocalDateAttribute localDateAttribute=new LocalDateAttribute();
    public final EnumAttribute<ExampleEnum> enumAttribute=new EnumAttribute<>(ExampleEnum.class);
    public final CharAttribute charAttribute=new CharAttribute();
    public final LongAttribute longAttribute=new LongAttribute();
    public final StringAttribute stringAttribute=new StringAttribute();
    public final IntegerAttribute integerAttribute=new IntegerAttribute();
    public final LocalDateTimeAttribute localDateTimeAttribute=new LocalDateTimeAttribute();
    public final LocaleAttribute localeAttribute=new LocaleAttribute();
    public final DurationAttribute durationAttribute=new DurationAttribute();
    public final FileContentAttribute fileContentAttribute =new FileContentAttribute();
    public final LocalTimeAttribute localTimeAttribute=new LocalTimeAttribute();
    public final ObjectValueAttribute<Object> objectValueAttribute=new ObjectValueAttribute<>();
    public final ShortAttribute shortAttribute=new ShortAttribute();
    public final PasswordAttribute passwordAttribute=new PasswordAttribute();
    public final URIAttribute uriAttribute=new URIAttribute();
    public final BigDecimalAttribute bigDecimalAttribute=new BigDecimalAttribute();
    public final FloatAttribute floatAttribute=new FloatAttribute();
    public final StringListAttribute stringListAttribute=new StringListAttribute();
    public final EnumListAttribute<ExampleEnum> enumListAttribute=new EnumListAttribute<>(ExampleEnum.class);
    public final InstantAttribute instantAttribute=new InstantAttribute();
    public final BigIntegerAttribute bigIntegerAttribute=new BigIntegerAttribute();

}
