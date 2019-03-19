package io.github.factoryfx.factory.typescript.generator.data;

import io.github.factoryfx.data.attribute.types.EncryptedStringAttribute;
import io.github.factoryfx.data.jackson.ObjectMapperBuilder;
import io.github.factoryfx.data.util.LanguageText;

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
        data.enumAttribute.set(null);
        data.charAttribute.set('a');
        data.longAttribute.set(9L);
        data.stringAttribute.set("text");
        data.integerAttribute.set(8);
        data.localDateTimeAttribute.set(LocalDateTime.now());
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
        data.instantAttribute.set(Instant.now());
        data.bigIntegerAttribute.set(new BigInteger("56756372572547253765427654376257643527656775656757576"));


        String value = ObjectMapperBuilder.build().writeValueAsString(data);
        System.out.println(value);
        System.out.println(data.instantAttribute.get().get(ChronoField.MICRO_OF_SECOND));


    }

}
