package de.factoryfx.data.merge.testfactories;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.DataReferenceAttribute;
import de.factoryfx.data.attribute.ReferenceAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;

public class ExampleFactoryB extends Data {
    public final StringAttribute stringAttribute= new StringAttribute(new AttributeMetadata().labelText("ExampleB1"));
    public final DataReferenceAttribute<ExampleFactoryA> referenceAttribute = new DataReferenceAttribute<>(ExampleFactoryA.class,new AttributeMetadata().labelText("ExampleB2"));
    public final DataReferenceAttribute<ExampleFactoryC> referenceAttributeC = new DataReferenceAttribute<>(ExampleFactoryC.class,new AttributeMetadata().labelText("ExampleC2"));


}
