package de.factoryfx.user;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class AuthorizedUser {
    public String user;
    public Locale locale;
    public Collection<String> permissions=new HashSet<>();

    public AuthorizedUser(String user, Locale locale, Set<String> permissions) {
        this.user = user;
        this.locale = locale;
        this.permissions.addAll(permissions);
    }

    public AuthorizedUser(String user, Locale locale, String... permissions) {
        this.user = user;
        this.locale = locale;
        this.permissions.addAll(Arrays.asList(permissions));
    }

    public void checkPermission(String permission){
        if (permission!=null && !permissions.contains(permission)){
            throw new IllegalStateException("permission required: "+permission);
        }
    }

    public boolean checkPermissionValid(String permission){
        if (permission!=null && !permissions.contains(permission)){
            return false;
        }
        return true;
    }
}
