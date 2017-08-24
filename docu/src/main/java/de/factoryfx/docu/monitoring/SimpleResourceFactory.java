package de.factoryfx.docu.monitoring;

import de.factoryfx.factory.SimpleFactoryBase;

public class SimpleResourceFactory extends SimpleFactoryBase<SimpleResource,ServerVisitor> {
    @Override
    public SimpleResource createImpl() {
        return new SimpleResource();
    }
}
