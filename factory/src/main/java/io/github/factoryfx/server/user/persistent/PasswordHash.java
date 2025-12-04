package io.github.factoryfx.server.user.persistent;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public class PasswordHash {

    public String hash(String password){
        HashFunction hf = Hashing.sha256();
        HashCode hc = hf.newHasher().putString(password, StandardCharsets.UTF_8).hash();
        return hc.toString();
    }

}
