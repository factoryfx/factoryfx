package de.factoryfx.javafx.javascript.editor.data;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.DataReferenceAttribute;
import de.factoryfx.javascript.data.attributes.types.JavascriptAttribute;

import java.io.PrintStream;
import java.util.Arrays;

public class ExampleJavascript extends Data {

    public final DataReferenceAttribute<ExampleData1> data = new DataReferenceAttribute<>(ExampleData1.class).en("data");
    public final JavascriptAttribute<PrintStream> specialAttribute=new JavascriptAttribute<>(()-> Arrays.asList(data.get()),PrintStream.class).de("code");

    public ExampleJavascript() {
    }

}
