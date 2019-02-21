package de.factoryfx.data.attribute.types;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Test;

public class PasswordAttributeTest {

    @Test
    public void test_json(){
        PasswordAttribute attribute = new PasswordAttribute();
        String key=new EncryptedStringAttribute().createKey();
        attribute.set(new EncryptedString("test123üÄ",key));
        ObjectMapperBuilder.build().copy(attribute);
    }

}