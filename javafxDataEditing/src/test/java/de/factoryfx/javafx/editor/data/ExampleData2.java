package de.factoryfx.javafx.editor.data;

import java.util.Arrays;
import java.util.Collections;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.DataViewListReferenceAttribute;
import de.factoryfx.data.attribute.DataViewReferenceAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.validation.ObjectRequired;
import de.factoryfx.data.validation.StringRequired;

public class ExampleData2 extends Data {

    public final StringAttribute stringAttribute=new StringAttribute().en("StringAttribute").de("StringAttribute de").validation(StringRequired.VALIDATION).defaultValue("123");

    public final DataViewReferenceAttribute<ExampleData1,ExampleData1> refview= new DataViewReferenceAttribute<>((ExampleData1 root)-> {
            if ("1".equals(stringAttribute.get())) {
                return root;
            }
            return null;
        }).validation(new ObjectRequired<>()).en("refview");

    public final DataViewListReferenceAttribute<ExampleData1,ExampleData1> listview= new DataViewListReferenceAttribute<>((ExampleData1 root)->{
            if ("1".equals(stringAttribute.get())){
                return Collections.singletonList(root);
            }
            return null;
        }).validation(new ObjectRequired<>()).en("listview");


    public ExampleData2(){
        config().setDisplayTextProvider(() -> stringAttribute.get());
        config().setDisplayTextDependencies(stringAttribute);
    }

}
