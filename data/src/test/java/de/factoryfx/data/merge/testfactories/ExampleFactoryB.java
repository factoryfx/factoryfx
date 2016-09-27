package de.factoryfx.data.merge.testfactories;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ReferenceAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;

public class ExampleFactoryB extends IdData {
    public final StringAttribute stringAttribute= new StringAttribute(new AttributeMetadata().labelText("ExampleB1"));
    public final ReferenceAttribute<ExampleFactoryA> referenceAttribute = new ReferenceAttribute<>(ExampleFactoryA.class,new AttributeMetadata().labelText("ExampleB2"));
    public final ReferenceAttribute<ExampleFactoryC> referenceAttributeC = new ReferenceAttribute<>(ExampleFactoryC.class,new AttributeMetadata().labelText("ExampleC2"));


}
