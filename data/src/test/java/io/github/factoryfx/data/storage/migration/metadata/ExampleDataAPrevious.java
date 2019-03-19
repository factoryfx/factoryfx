package io.github.factoryfx.data.storage.migration.metadata;

import io.github.factoryfx.data.Data;
import io.github.factoryfx.data.attribute.CopySemantic;
import io.github.factoryfx.data.attribute.DataReferenceAttribute;
import io.github.factoryfx.data.attribute.DataReferenceListAttribute;
import io.github.factoryfx.data.attribute.types.StringAttribute;
import io.github.factoryfx.data.merge.testdata.ExampleDataB;

public class ExampleDataAPrevious extends Data {
    public final StringAttribute stringAttribute = new StringAttribute().labelText("ExampleA1");
    public final DataReferenceAttribute<ExampleDataB> referenceAttribute = new DataReferenceAttribute<>(ExampleDataB.class).setCopySemantic(CopySemantic.SELF).labelText("ExampleA2");
    public final DataReferenceListAttribute<ExampleDataB> referenceListAttribute = new DataReferenceListAttribute<>(ExampleDataB.class).setCopySemantic(CopySemantic.SELF).labelText("ExampleA3");
    public final StringAttribute garbage = new StringAttribute();

}
