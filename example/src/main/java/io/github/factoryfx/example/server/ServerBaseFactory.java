package io.github.factoryfx.example.server;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.jetty.builder.JettyServerRootFactory;

public abstract class ServerBaseFactory<L> extends SimpleFactoryBase<L, JettyServerRootFactory> {
}
