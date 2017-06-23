package de.factoryfx.data.attribute.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.attribute.ImmutableValueAttribute;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.function.Function;

public class PasswordAttribute extends ImmutableValueAttribute<EncryptedString, PasswordAttribute> {
    
    public PasswordAttribute() {
        super(EncryptedString.class);
    }

    @JsonCreator
    PasswordAttribute(EncryptedString initialValue) {
        super(EncryptedString.class);
        set(initialValue);
    }

    @JsonCreator
    PasswordAttribute(String initialValue) {
        super(EncryptedString.class);
        set(new EncryptedString(initialValue));
    }
    private Function<String,String> passwordHash;
    public PasswordAttribute hash(Function<String,String> passwordHash){
        this.passwordHash=passwordHash;
        return this;
    }

    public String internal_hash(String pw){
        if (passwordHash!=null){
            return passwordHash.apply(pw);
        }
        return pw;
    }

    public boolean internal_isValidKey(String key) {
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
