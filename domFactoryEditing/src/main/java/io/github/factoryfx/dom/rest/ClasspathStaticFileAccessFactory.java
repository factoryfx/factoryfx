package io.github.factoryfx.dom.rest;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;

public class ClasspathStaticFileAccessFactory<R extends FactoryBase<?,R>> extends SimpleFactoryBase<StaticFileAccess,R> {

    @Override
    protected ClasspathStaticFileAccess createImpl() {
        return new ClasspathStaticFileAccess();
    }
}