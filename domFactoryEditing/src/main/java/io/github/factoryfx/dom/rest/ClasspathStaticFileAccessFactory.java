package io.github.factoryfx.dom.rest;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.PolymorphicFactoryBase;

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