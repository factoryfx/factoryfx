package io.github.factoryfx.factory.attribute.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class EncryptedString implements Comparable<EncryptedString> {
    private final String encryptedString;

    @JsonCreator
    protected EncryptedString(@JsonProperty("encryptedString")String encryptedString) {
        this.encryptedString=encryptedString;
    }

    public EncryptedString(String value, String key) {
        try {
            byte[] decodedKey = Base64.getDecoder().decode(key);
            SecretKey secKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

            Cipher AesCipher = Cipher.getInstance("AES");
            AesCipher.init(Cipher.ENCRYPT_MODE, secKey);
            encryptedString=Base64.getEncoder().encodeToString(AesCipher.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        }
    }

    public String decrypt(String key) {
        try {
            byte[] decodedKey = Base64.getDecoder().decode(key);
            SecretKey secKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

            Cipher AesCipher = Cipher.getInstance("AES");
            AesCipher.init(Cipher.DECRYPT_MODE, secKey);
            return new String(AesCipher.doFinal(Base64.getDecoder().decode(encryptedString.getBytes(StandardCharsets.UTF_8))), StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    public String getEncryptedString(){
        return encryptedString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EncryptedString that = (EncryptedString) o;

        return encryptedString != null ? encryptedString.equals(that.encryptedString) : that.encryptedString == null;
    }

    @Override
    public int hashCode() {
        return encryptedString != null ? encryptedString.hashCode() : 0;
    }

    @Override
    public int compareTo(EncryptedString other) {
        if (this == other) return 0;
        if (other == null)
            return 1;
        int cmp = getClass().getName().compareTo(other.getClass().getName());
        if (cmp != 0)
            return cmp;

        if (encryptedString == null) {
            if (other.encryptedString == null)
                return 0;
            return -1;
        }
        if (other.encryptedString == null)
            return 1;
        return this.encryptedString.compareTo(other.encryptedString);
    }
}
