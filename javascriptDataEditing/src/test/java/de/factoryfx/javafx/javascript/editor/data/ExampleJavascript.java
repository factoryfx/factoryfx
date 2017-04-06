package de.factoryfx.javafx.javascript.editor.data;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ReferenceAttribute;
import de.factoryfx.javascript.data.attributes.types.JavascriptAttribute;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

public class ExampleJavascript  extends Data {

    public final ReferenceAttribute<ExampleData1> data = new ReferenceAttribute<ExampleData1>(ExampleData1.class,new AttributeMetadata().en("data"));
    public final JavascriptAttribute<Void> specialAttribute=new JavascriptAttribute<>(new AttributeMetadata().de("code"),()-> Arrays.asList(data.get()),Void.class);

    public ExampleJavascript() {
    }


    String id= UUID.randomUUID().toString();

}
