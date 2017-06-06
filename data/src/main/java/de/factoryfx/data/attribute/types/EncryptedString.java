package de.factoryfx.data.attribute.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class EncryptedString {
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
}
