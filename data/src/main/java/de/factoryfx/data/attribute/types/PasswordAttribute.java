package de.factoryfx.data.attribute.types;

import de.factoryfx.data.attribute.ImmutableValueAttribute;

import java.util.function.Function;

public class PasswordAttribute extends ImmutableValueAttribute<EncryptedString, PasswordAttribute> {

    public PasswordAttribute() {
        super(EncryptedString.class);
    }

    private Function<String, String> passwordHash;

    public PasswordAttribute hash(Function<String, String> passwordHash) {
        this.passwordHash = passwordHash;
        return this;
    }

    public String internal_hash(String pw) {
        if (passwordHash != null) {
            return passwordHash.apply(pw);
        }
        return pw;
    }

    public boolean internal_isValidKey(String key) {
        return new EncryptedStringAttribute.KeyValidator().validate(key);
    }
}