package de.factoryfx.javafx.editor.data;

import java.util.Arrays;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ViewListReferenceAttribute;
import de.factoryfx.data.attribute.ViewReferenceAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.validation.ObjectRequired;
import de.factoryfx.data.validation.StringRequired;

public class ExampleData2 extends Data {

    public final StringAttribute stringAttribute=new StringAttribute(new AttributeMetadata().en("StringAttribute").de("StringAttribute de")).validation(new StringRequired()).defaultValue("123");



    public final ViewReferenceAttribute<ExampleData1,ExampleData1> refview= new ViewReferenceAttribute<>(new AttributeMetadata().en("refview"), (ExampleData1 root)-> {
            if ("1".equals(stringAttribute.get())) {
                return root;
            }
            return null;
        }).validation(new ObjectRequired<>());

    public final ViewListReferenceAttribute<ExampleData1,ExampleData1> listview= new ViewListReferenceAttribute<>(new AttributeMetadata().en("listview"), (ExampleData1 root)->{
            if ("1".equals(stringAttribute.get())){
                return Arrays.asList(root);
            }
            return null;
        }).validation(new ObjectRequired<>());




    public ExampleData2(){
        config().setDisplayTextProvider(() -> stringAttribute.get());
        config().setDisplayTextDependencies(stringAttribute);
    }

}
