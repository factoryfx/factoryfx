package io.github.factoryfx.server.user;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class AuthorizedUser {
    private final String user;
    private final Locale locale;
    private final Collection<String> permissions;

    public AuthorizedUser(String user, Locale locale, Set<String> permissions) {
        this.user = user;
        this.locale = locale;
        this.permissions = permissions;
    }

    public AuthorizedUser(String user, Locale locale, String... permissions) {
        this.user = user;
        this.locale = locale;
        this.permissions = new HashSet<>();
        this.permissions.addAll(Arrays.asList(permissions));
    }

    public void checkPermission(String permission) {
        if (permission != null && !permissions.contains(permission)) {
            throw new IllegalStateException("permission required: " + permission);
        }
    }

    public boolean checkPermissionValid(String permission) {
        return permission == null || permissions.contains(permission);
    }

    public Locale getLocale() {
        return locale;
    }

    public String getUserName() {
        return user;
    }

    public Collection<String> getPermissions() {
        return permissions;
    }
}
