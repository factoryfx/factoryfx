package de.factoryfx.data.attribute.types;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.KeyGenerator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ImmutableValueAttribute;

public class EncryptedStringAttribute extends ImmutableValueAttribute<EncryptedString> {

    public EncryptedStringAttribute(AttributeMetadata attributeMetadata) {
        super(attributeMetadata, EncryptedString.class);
    }

    @JsonCreator
    EncryptedStringAttribute(EncryptedString initialValue) {
        super(null, EncryptedString.class);
        set(initialValue);
    }

    @JsonIgnore
    private boolean longText = false;

    /**
     * for long text textarea instead of textfield is used for editing
     */
    @JsonIgnore
    public EncryptedStringAttribute longText() {
        longText = true;
        return this;
    }

    @JsonIgnore
    public boolean isLongText() {
        return longText;
    }


    public String createKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            return Base64.getEncoder().encodeToString(keyGen.generateKey().getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String decrypt(String key) {
        return get().decrypt(key);
    }

    @Override
    protected Attribute<EncryptedString> createNewEmptyInstance() {
        return new EncryptedStringAttribute(metadata);
    }
}


