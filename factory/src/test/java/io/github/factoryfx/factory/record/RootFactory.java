package io.github.factoryfx.factory.record;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;

public class RootFactory extends SimpleFactoryBase<Root, RootFactory> {

    public FactoryAttribute<RecordExampleA, RecordFactory<RecordExampleA, RecordExampleA.Dep,RootFactory>> recordA = new FactoryAttribute<RecordExampleA, RecordFactory<RecordExampleA, RecordExampleA.Dep,RootFactory>>().nullable();
    public FactoryAttribute<RecordExampleB, RecordFactory<RecordExampleB, RecordExampleB.Dep,RootFactory>> recordB = new FactoryAttribute<RecordExampleB, RecordFactory<RecordExampleB, RecordExampleB.Dep,RootFactory>>().nullable();
    @Override
    protected Root createImpl() {
        return new Root(recordA.instance(),recordB.instance());
    }
}
