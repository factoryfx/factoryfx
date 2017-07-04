package de.factoryfx.data.merge.testfactories;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.DataReferenceAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;

public class ExampleDataB extends Data {
    public final StringAttribute stringAttribute= new StringAttribute().labelText("ExampleB1");
    public final DataReferenceAttribute<ExampleDataA> referenceAttribute = new DataReferenceAttribute<>(ExampleDataA.class).labelText("ExampleB2");
    public final DataReferenceAttribute<ExampleDataC> referenceAttributeC = new DataReferenceAttribute<>(ExampleDataC.class).labelText("ExampleC2");


}
