package de.factoryfx.data.attribute.types;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ValueAttribute;

public class EncryptedStringAttribute extends ValueAttribute<String> {

    public EncryptedStringAttribute(AttributeMetadata attributeMetadata) {
        super(attributeMetadata,String.class);
    }

    @JsonCreator
    EncryptedStringAttribute(String initialValue) {
        super(null,String.class);
        set(initialValue);
    }

    @JsonIgnore
    private boolean longText=false;
    /** for long text texare instead of textfield is used for editing*/
    @JsonIgnore
    public EncryptedStringAttribute longText(){
        longText=true;
        return this;
    }

    @JsonIgnore
    public boolean isLongText(){
        return longText;
    }


    public String createKey(){
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            return Base64.getEncoder().encodeToString(keyGen.generateKey().getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public void encrypt(String value, String key){
        try {
            byte[] decodedKey = Base64.getDecoder().decode(key);
            SecretKey secKey= new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

            Cipher AesCipher = Cipher.getInstance("AES");
            AesCipher.init(Cipher.ENCRYPT_MODE, secKey);
            set(Base64.getEncoder().encodeToString(AesCipher.doFinal(value.getBytes(StandardCharsets.UTF_8))));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        }
    }

    public String decrypt(String key){
        try {
            byte[] decodedKey = Base64.getDecoder().decode(key);
            SecretKey secKey= new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

            Cipher AesCipher = Cipher.getInstance("AES");
            AesCipher.init(Cipher.DECRYPT_MODE, secKey);
            return new String(AesCipher.doFinal(Base64.getDecoder().decode(get().getBytes(StandardCharsets.UTF_8))));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

}
