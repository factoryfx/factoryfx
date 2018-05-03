package de.factoryfx.server.user.nop;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.PolymorphicFactoryBase;
import de.factoryfx.server.user.nop.NoUserManagement;
import de.factoryfx.server.user.UserManagement;

public class NoUserManagementFactory<V,R extends FactoryBase<?,V,R>> extends PolymorphicFactoryBase<UserManagement,V,R> {


    @Override
    public Class<UserManagement> getLiveObjectClass() {
        return UserManagement.class;
    }

    @Override
    public UserManagement createImpl() {
        return new NoUserManagement();
    }
}
