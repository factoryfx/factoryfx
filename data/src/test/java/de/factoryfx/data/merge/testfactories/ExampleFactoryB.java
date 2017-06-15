package de.factoryfx.data.merge.testfactories;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.DataReferenceAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;

public class ExampleFactoryB extends Data {
    public final StringAttribute stringAttribute= new StringAttribute().labelText("ExampleB1");
    public final DataReferenceAttribute<ExampleFactoryA> referenceAttribute = new DataReferenceAttribute<>(ExampleFactoryA.class).labelText("ExampleB2");
    public final DataReferenceAttribute<ExampleFactoryC> referenceAttributeC = new DataReferenceAttribute<>(ExampleFactoryC.class).labelText("ExampleC2");


}
