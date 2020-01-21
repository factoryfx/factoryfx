package io.github.factoryfx.factory.attribute.types;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;

public class EncryptedStringAttributeTest {

    @Test
    public void encrypt_test(){
        EncryptedStringAttribute attribute = new EncryptedStringAttribute();
        String key=EncryptedStringAttribute.createKey();
        attribute.set(new EncryptedString("test123üÄ",key));

        Assertions.assertEquals("test123üÄ",attribute.decrypt(key));
    }

    @Test
    public void test_json(){
        EncryptedStringAttribute attribute = new EncryptedStringAttribute();
        String key=EncryptedStringAttribute.createKey();
        attribute.set(new EncryptedString("test123üÄ",key));
        ObjectMapperBuilder.build().copy(attribute);
    }

    @Test
    public void encrypt_encrypt(){
        EncryptedStringAttribute attribute = new EncryptedStringAttribute();
        String key=EncryptedStringAttribute.createKey();
        attribute.encrypt("test123üÄ",key);

        Assertions.assertEquals("test123üÄ",attribute.decrypt(key));
    }

    @Test
    public void set_null(){
        EncryptedStringAttribute attribute = new EncryptedStringAttribute().nullable();
        attribute.set(null,"key");
        Assertions.assertNull(attribute.get());
    }

    @Test
    public void get_null(){
        EncryptedStringAttribute attribute = new EncryptedStringAttribute().nullable();
        attribute.set(null,"key");
        Assertions.assertNull(attribute.decrypt("key"));
    }
}