package io.github.factoryfx.factory.typescript.generator.data;

import io.github.factoryfx.dom.rest.DynamicDataDictionary;
import io.github.factoryfx.factory.attribute.types.EncryptedStringAttribute;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.util.LanguageText;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Locale;

public class ExampleDataAllJsonExample {
    public static void main(String[] args) {
        byte[] bytes = {10, -20};//-128 to 127

        ExampleDataAll data = new ExampleDataAll();
        data.byteArrayAttribute.set(bytes);
        data.i18nAttribute.set(new LanguageText().en("texten").de("textde"));
        data.encryptedStringAttribute.set("dfgd", EncryptedStringAttribute.createKey());
        data.doubleAttribute.set(0.5);
        data.byteAttribute.set((byte)10);
        data.booleanAttribute.set(true);
        data.localDateAttribute.set(LocalDate.now());
        data.enumAttribute.set(ExampleEnum.VALUE1);
        data.charAttribute.set('a');
        data.longAttribute.set(9L);
        data.stringAttribute.set("text");
        data.integerAttribute.set(8);
        data.localDateTimeAttribute.set(LocalDateTime.parse("2018-12-04T17:25:58.7759195"));
        data.localeAttribute.set(Locale.ENGLISH);
        data.durationAttribute.set(Duration.ofSeconds(4));
        data.fileContentAttribute.set(bytes);
        data.localTimeAttribute.set(LocalTime.now());
        data.objectValueAttribute.set(new Object());
        data.shortAttribute.set((short)3);
        data.passwordAttribute.setPasswordNotHashed("dfgd",EncryptedStringAttribute.createKey());
        data.uriAttribute.setUnchecked("http://google.de");
        data.bigDecimalAttribute.set(new BigDecimal(3));
        data.floatAttribute.set(0.6f);
        data.stringListAttribute.set(List.of("ab","cd"));
        data.instantAttribute.set(Instant.parse("2018-12-12T10:24:55.026232600Z"));
        data.bigIntegerAttribute.set(new BigInteger("56756372572547253765427654376257643527656775656757576"));
        data.enumListAttribute.add(ExampleEnum.VALUE1);
        data.enumListAttribute.add(ExampleEnum.VALUE2);

        data.factoryPolymorphicAttribute.set(new ExampleData());


        String value = ObjectMapperBuilder.build().writeValueAsString(data);
        System.out.println(value);
        System.out.println(data.instantAttribute.get().get(ChronoField.MICRO_OF_SECOND));

        data.internal().finalise();
        System.out.println(ObjectMapperBuilder.build().writeValueAsString(new DynamicDataDictionary(data)));
    }

}
