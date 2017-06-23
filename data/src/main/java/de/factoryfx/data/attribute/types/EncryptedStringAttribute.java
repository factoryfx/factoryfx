package de.factoryfx.data.attribute.types;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.ImmutableValueAttribute;

public class EncryptedStringAttribute extends ImmutableValueAttribute<EncryptedString,EncryptedStringAttribute> {

    public EncryptedStringAttribute() {
        super(EncryptedString.class);
    }

    @JsonCreator
    EncryptedStringAttribute(EncryptedString initialValue) {
        super(EncryptedString.class);
        set(initialValue);
    }

    @JsonCreator
    EncryptedStringAttribute(String initialValue) {
        super(EncryptedString.class);
        set(new EncryptedString(initialValue));
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

    public boolean isValidKey(String key) {
        try {
            byte[] decodedKey = Base64.getDecoder().decode(key);
            SecretKey secKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

            Cipher AesCipher = Cipher.getInstance("AES");
            AesCipher.init(Cipher.ENCRYPT_MODE, secKey);
            Base64.getEncoder().encodeToString(AesCipher.doFinal("test".getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | IllegalArgumentException e) {
            return false;
        }
        return true;
    }
}


