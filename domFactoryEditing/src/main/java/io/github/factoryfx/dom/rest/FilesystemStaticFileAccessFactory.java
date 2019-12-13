package io.github.factoryfx.dom.rest;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.types.StringAttribute;

public class FilesystemStaticFileAccessFactory<R extends FactoryBase<?,R>>  extends SimpleFactoryBase<StaticFileAccess,R> {
    public final StringAttribute basePath = new StringAttribute();//should end width /

    @Override
    protected FilesystemStaticFileAccess createImpl() {
        return new FilesystemStaticFileAccess(basePath.get());
    }

}
