package io.github.factoryfx.javafx.data.editor.data;

import java.util.Collections;

import io.github.factoryfx.data.Data;
import io.github.factoryfx.data.attribute.DataViewListReferenceAttribute;
import io.github.factoryfx.data.attribute.DataViewReferenceAttribute;
import io.github.factoryfx.data.attribute.types.StringAttribute;

public class ExampleData2 extends Data {

    public final StringAttribute stringAttribute=new StringAttribute().en("StringAttribute").de("StringAttribute de").defaultValue("123");

    public final DataViewReferenceAttribute<ExampleData1,ExampleData1> refview= new DataViewReferenceAttribute<>((ExampleData1 root)-> {
            if ("1".equals(stringAttribute.get())) {
                return root;
            }
            return null;
        }).en("refview");

    public final DataViewListReferenceAttribute<ExampleData1,ExampleData1> listview= new DataViewListReferenceAttribute<>((ExampleData1 root)->{
            if ("1".equals(stringAttribute.get())){
                return Collections.singletonList(root);
            }
            return null;
        }).en("listview");


    public ExampleData2(){
        config().setDisplayTextProvider(() -> stringAttribute.get());
        config().setDisplayTextDependencies(stringAttribute);
    }

}
