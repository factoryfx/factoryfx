package de.factoryfx.data.attribute.types;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Assert;
import org.junit.Test;

public class EncryptedStringAttributeTest {

    @Test
    public void encrypt_test(){
        EncryptedStringAttribute attribute = new EncryptedStringAttribute(new AttributeMetadata());
        String key=attribute.createKey();
        attribute.set(new EncryptedString("test123üÄ",key));

        Assert.assertEquals("test123üÄ",attribute.decrypt(key));
    }

    @Test
    public void encrypt_json(){
        EncryptedStringAttribute attribute = new EncryptedStringAttribute(new AttributeMetadata());
        String key=attribute.createKey();
        attribute.set(new EncryptedString("test123üÄ",key));
        ObjectMapperBuilder.build().copy(attribute);
    }

}