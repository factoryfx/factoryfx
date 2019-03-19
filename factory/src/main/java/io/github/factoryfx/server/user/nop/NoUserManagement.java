package io.github.factoryfx.server.user.nop;

import io.github.factoryfx.server.user.AuthorizedUser;
import io.github.factoryfx.server.user.UserManagement;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

public class NoUserManagement implements UserManagement {

    @Override
    public Optional<AuthorizedUser> authenticate(String user, String password) {
        return Optional.of(new AuthorizedUser(UUID.randomUUID().toString(), Locale.ENGLISH));
    }

    @Override
    public boolean authorisationRequired() {
        return false;
    }
}
