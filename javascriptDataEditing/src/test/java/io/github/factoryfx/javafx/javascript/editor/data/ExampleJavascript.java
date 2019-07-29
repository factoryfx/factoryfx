package io.github.factoryfx.javafx.javascript.editor.data;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.javascript.data.attributes.types.JavascriptAttribute;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

public class ExampleJavascript extends FactoryBase<Void,ExampleJavascript> {

    public final FactoryAttribute<Void,ExampleData1> data = new FactoryAttribute<Void,ExampleData1>().en("data");
    public final JavascriptAttribute<PrintStream> specialAttribute=new JavascriptAttribute<>(()-> List.of(data.get()),PrintStream.class).de("code");

    public ExampleJavascript() {
    }

}
