package de.factoryfx.factory.typescript.generator.data;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.DataReferenceAttribute;
import de.factoryfx.data.attribute.DataReferenceListAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import javafx.beans.property.StringProperty;

public class ExampleData extends Data {
    public final StringAttribute attribute= new StringAttribute();
    public final DataReferenceAttribute<ExampleData2> ref= new DataReferenceAttribute<>(ExampleData2.class);
    public final DataReferenceListAttribute<ExampleData2> refList= new DataReferenceListAttribute<>(ExampleData2.class);
}