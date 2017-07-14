package de.factoryfx.user.nop;

import de.factoryfx.factory.PolymorphicFactoryBase;
import de.factoryfx.user.UserManagement;

public class NoUserManagementFactory extends PolymorphicFactoryBase<UserManagement,Void> {


    @Override
    public Class<UserManagement> getLiveObjectClass() {
        return UserManagement.class;
    }

    @Override
    public UserManagement createImpl() {
        return new NoUserManagement();
    }
}
