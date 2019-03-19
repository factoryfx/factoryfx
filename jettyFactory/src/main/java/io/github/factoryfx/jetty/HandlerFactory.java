package io.github.factoryfx.jetty;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;

import java.util.logging.Handler;

public abstract class HandlerFactory<V,R extends FactoryBase<?,R>> extends SimpleFactoryBase<Handler,R> {
}
