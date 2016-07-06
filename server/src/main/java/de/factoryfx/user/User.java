package de.factoryfx.user;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class User {
    public String user;
    public String password;
    public Locale locale;
    public Set<String> permissions=new HashSet<>();

    public boolean matchUser(String user, String password){
        return user.equals(user) && password.equals(password);
    }

    public User(String user, String password, Locale locale, List<String> permissions) {
        this.user = user;
        this.password = password;
        this.locale = locale;
        this.permissions.addAll(permissions);
    }

    public User(String user, String password, Locale locale, String... permissions) {
        this.user = user;
        this.password = password;
        this.locale = locale;
        this.permissions.addAll(Arrays.asList(permissions));
    }

    public void checkPermission(String permission){
        if (permission!=null && !permissions.contains(permission)){
            throw new IllegalStateException("permission required: "+permission);
        }
    }
}
