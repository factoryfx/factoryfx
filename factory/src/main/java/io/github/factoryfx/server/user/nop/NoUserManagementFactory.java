package io.github.factoryfx.server.user.nop;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.server.user.UserManagement;

public class NoUserManagementFactory<R extends FactoryBase<?,R>> extends SimpleFactoryBase<UserManagement,R> {

    @Override
    protected UserManagement createImpl() {
        return new NoUserManagement();
    }
}
