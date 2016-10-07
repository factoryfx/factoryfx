package de.factoryfx.user;

import java.util.Optional;

public interface UserManagement {

    Optional<User> authenticate(String user, String password);
    boolean authorisationRequired();
}
