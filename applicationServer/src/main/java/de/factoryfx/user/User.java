package de.factoryfx.user;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class User {
    public String user;
    public String password;
    public Locale locale;
    public Set<String> permissions=new HashSet<>();


    public boolean matchUser(String user, String password){
        return this.user.equals(user) && this.password.equals(password);
    }

    public User(String user, String password, Locale locale, Collection<String> permissions) {
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

    public AuthorizedUser toAuthorizedUser() {
        return new AuthorizedUser(user,locale,permissions);
    }
}
