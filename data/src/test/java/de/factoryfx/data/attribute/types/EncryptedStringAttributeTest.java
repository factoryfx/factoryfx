package de.factoryfx.data.attribute.types;

import de.factoryfx.data.attribute.AttributeMetadata;
import org.junit.Assert;
import org.junit.Test;

public class EncryptedStringAttributeTest {

    @Test
    public void encrypt_test(){
        EncryptedStringAttribute attribute = new EncryptedStringAttribute(new AttributeMetadata());
        String key=attribute.createKey();
        attribute.encrypt("test123üÄ",key);

        Assert.assertEquals("test123üÄ",attribute.decrypt(key));
    }

}