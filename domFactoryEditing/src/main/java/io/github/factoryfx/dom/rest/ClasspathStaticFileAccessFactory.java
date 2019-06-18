package io.github.factoryfx.dom.rest;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.PolymorphicFactoryBase;
import io.github.factoryfx.server.user.UserManagement;
import io.github.factoryfx.server.user.nop.NoUserManagement;

public class ClasspathStaticFileAccessFactory<R extends FactoryBase<?,R>> extends PolymorphicFactoryBase<StaticFileAccess,R> {


    @Override
    public Class<StaticFileAccess> getLiveObjectClass() {
        return StaticFileAccess.class;
    }

    @Override
    protected ClasspathStaticFileAccess createImpl() {
        return new ClasspathStaticFileAccess();
    }
}