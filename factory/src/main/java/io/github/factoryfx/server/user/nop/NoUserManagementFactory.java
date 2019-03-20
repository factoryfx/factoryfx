package io.github.factoryfx.server.user.nop;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.PolymorphicFactoryBase;
import io.github.factoryfx.server.user.UserManagement;

public class NoUserManagementFactory<R extends FactoryBase<?,R>> extends PolymorphicFactoryBase<UserManagement,R> {


    @Override
    public Class<UserManagement> getLiveObjectClass() {
        return UserManagement.class;
    }

    @Override
    public UserManagement createImpl() {
        return new NoUserManagement();
    }
}
