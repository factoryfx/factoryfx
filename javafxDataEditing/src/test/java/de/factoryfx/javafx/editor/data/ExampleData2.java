package de.factoryfx.javafx.editor.data;

import java.util.UUID;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.validation.StringRequired;

public class ExampleData2 extends Data {

    public final StringAttribute stringAttribute=new StringAttribute(new AttributeMetadata().en("StringAttribute").de("StringAttribute de")).validation(new StringRequired());

    String id= UUID.randomUUID().toString();
    @Override
    public Object getId() {
        return id;
    }

    @Override
    public void setId(Object id) {
        this.id=(String)id;
    }
}
