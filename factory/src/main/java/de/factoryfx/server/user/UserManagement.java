package de.factoryfx.server.user;

import de.factoryfx.server.user.AuthorizedUser;

import java.util.Optional;

public interface UserManagement {

    Optional<AuthorizedUser> authenticate(String user, String password);
    boolean authorisationRequired();
}
