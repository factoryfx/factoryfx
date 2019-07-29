package io.github.factoryfx.dom.rest;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.PolymorphicFactoryBase;
import io.github.factoryfx.factory.attribute.types.StringAttribute;

public class FilesystemStaticFileAccessFactory<R extends FactoryBase<?,R>>  extends PolymorphicFactoryBase<StaticFileAccess,R> {
    public final StringAttribute basePath = new StringAttribute();//should end width /

    @Override
    public Class<StaticFileAccess> getLiveObjectClass() {
        return StaticFileAccess.class;
    }

    @Override
    protected FilesystemStaticFileAccess createImpl() {
        return new FilesystemStaticFileAccess(basePath.get());
    }

}
