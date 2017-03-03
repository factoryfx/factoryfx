package de.factoryfx.data.merge.testfactories;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.StringAttribute;

public class ExampleFactoryC extends Data {
    public final StringAttribute stringAttribute= new StringAttribute(new AttributeMetadata().labelText("ExampleB1"));

}
