package de.factoryfx.javafx.javascript.editor.attribute.visualisation;

import de.factoryfx.javascript.data.attributes.types.Externs;
import de.factoryfx.javascript.data.attributes.types.Javascript;
import org.junit.jupiter.api.Test;

public class ContentAssistTest {

    @Test
    public void printAssist() {
        Javascript js = new Javascript(" { }","/** @constant @readonly */ var x = {}","");
        new ContentAssist().findProposals(Externs.get(),js).forEach((i,p)->{
            System.out.println(i+"/"+p);
        });
    }

}