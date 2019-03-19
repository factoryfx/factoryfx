package io.github.factoryfx.factory.typescript.generator.data;

import io.github.factoryfx.data.Data;
import io.github.factoryfx.data.attribute.DataReferenceAttribute;
import io.github.factoryfx.data.attribute.DataReferenceListAttribute;
import io.github.factoryfx.data.attribute.types.StringAttribute;

public class ExampleData extends Data {
    public final StringAttribute attribute= new StringAttribute().en("labelEn\"\'\\").de("labelDe");
    public final DataReferenceAttribute<ExampleData2> ref= new DataReferenceAttribute<>(ExampleData2.class);
    public final DataReferenceListAttribute<ExampleData2> refList= new DataReferenceListAttribute<>(ExampleData2.class);
}
