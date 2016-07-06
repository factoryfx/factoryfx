package de.factoryfx.user;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

public class NoUserManagement implements UserManagement {

    @Override
    public Optional<User> authenticate(String user, String password) {
        return Optional.of(new User(UUID.randomUUID().toString(),"", Locale.ENGLISH));
    }

    @Override
    public boolean authorisationRequired() {
        return false;
    }
}
