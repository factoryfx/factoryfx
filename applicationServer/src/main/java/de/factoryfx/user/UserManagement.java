package de.factoryfx.user;

import java.util.Optional;

public interface UserManagement {

    Optional<AuthorizedUser> authenticate(String user, String password);
    boolean authorisationRequired();
}
