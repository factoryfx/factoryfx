package io.github.factoryfx.factory.attribute.types;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.factoryfx.factory.attribute.ImmutableValueAttribute;

public class EncryptedStringAttribute extends ImmutableValueAttribute<EncryptedString,EncryptedStringAttribute> {

    public EncryptedStringAttribute() {
        super();
    }

    @JsonIgnore
    private boolean longText = false;

    /**
     * for long text textarea instead of textfield is used for editing
     * @return self
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


    public static String createKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            return Base64.getEncoder().encodeToString(keyGen.generateKey().getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public EncryptedStringAttribute encrypt(String value, String key) {
        this.set(new EncryptedString(value,key));
        return this;
    }

    public EncryptedStringAttribute set(String value, String key) {
        return encrypt(value,key);
    }

    public String decrypt(String key) {
        return get().decrypt(key);
    }

    public boolean internal_isValidKey(String key) {
        return new KeyValidator().validate(key);
    }

    public static class KeyValidator {
        public boolean validate(String key) {
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
}


