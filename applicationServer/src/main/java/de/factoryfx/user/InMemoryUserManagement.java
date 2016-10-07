package de.factoryfx.user;

import java.util.List;
import java.util.Optional;

public class InMemoryUserManagement implements UserManagement {

    private final List<User> users;

    public InMemoryUserManagement(List<User> users) {
        this.users = users;
    }

    @Override
    public Optional<User> authenticate(String user, String password) {
        for (User existinguser: users){
            if (existinguser.matchUser(user, password)){
                return Optional.of(existinguser);
            }
        }

        return Optional.empty();
    }

    @Override
    public boolean authorisationRequired() {
        return true;
    }
}
