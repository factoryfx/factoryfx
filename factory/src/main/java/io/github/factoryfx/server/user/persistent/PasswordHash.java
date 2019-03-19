package io.github.factoryfx.server.user.persistent;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

public class PasswordHash {

    public String hash(String password){
        HashFunction hf = Hashing.sha256();
        HashCode hc = hf.newHasher().putString(password, Charsets.UTF_8).hash();
        return hc.toString();
    }

}
