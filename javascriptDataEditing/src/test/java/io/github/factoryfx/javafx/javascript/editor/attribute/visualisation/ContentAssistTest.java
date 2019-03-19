package io.github.factoryfx.javafx.javascript.editor.attribute.visualisation;

import io.github.factoryfx.javascript.data.attributes.types.Externs;
import io.github.factoryfx.javascript.data.attributes.types.Javascript;
import org.junit.jupiter.api.Test;

public class ContentAssistTest {

    @Test
    public void printAssist() {
        Javascript js = new Javascript(" { }","/** @constant @readonly */ var x = {}","");
        new ContentAssist().findProposals(Externs.get(),js).forEach((i, p)->{
            System.out.println(i+"/"+p);
        });
    }

}