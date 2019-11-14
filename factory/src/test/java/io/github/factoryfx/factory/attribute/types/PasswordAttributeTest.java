package io.github.factoryfx.factory.attribute.types;

import org.junit.jupiter.api.Test;

import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;

public class PasswordAttributeTest {

    @Test
    public void test_json() {
        PasswordAttribute attribute = new PasswordAttribute();
        String key = EncryptedStringAttribute.createKey();
        attribute.set(new EncryptedString("test123üÄ", key));
        ObjectMapperBuilder.build().copy(attribute);
    }

}