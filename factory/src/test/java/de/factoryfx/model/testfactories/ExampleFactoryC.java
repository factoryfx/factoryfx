package de.factoryfx.model.testfactories;

import de.factoryfx.model.ClosedPreviousLiveObject;
import de.factoryfx.model.FactoryBase;
import de.factoryfx.model.attribute.AttributeMetadata;
import de.factoryfx.model.attribute.StringAttribute;

public class ExampleFactoryC extends FactoryBase<ExampleLiveObjectC,ExampleFactoryC> {
    public StringAttribute stringAttribute=new StringAttribute(new AttributeMetadata<>("ExampleB1"));

    @Override
    protected ExampleLiveObjectC createImp(ClosedPreviousLiveObject<ExampleLiveObjectC> closedPreviousLiveObject) {
        return null;
    }
}
