package de.factoryfx.data.storage.migration.metadata;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.CopySemantic;
import de.factoryfx.data.attribute.DataReferenceAttribute;
import de.factoryfx.data.attribute.DataReferenceListAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.merge.testdata.ExampleDataB;

public class ExampleDataAPrevious extends Data {
    public final StringAttribute stringAttribute = new StringAttribute().labelText("ExampleA1");
    public final DataReferenceAttribute<ExampleDataB> referenceAttribute = new DataReferenceAttribute<>(ExampleDataB.class).setCopySemantic(CopySemantic.SELF).labelText("ExampleA2");
    public final DataReferenceListAttribute<ExampleDataB> referenceListAttribute = new DataReferenceListAttribute<>(ExampleDataB.class).setCopySemantic(CopySemantic.SELF).labelText("ExampleA3");
    public final StringAttribute garbage = new StringAttribute();

}
