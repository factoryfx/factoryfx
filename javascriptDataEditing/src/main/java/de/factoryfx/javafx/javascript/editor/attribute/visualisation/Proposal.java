package de.factoryfx.javafx.javascript.editor.attribute.visualisation;

public class Proposal {

    public final String insertString;


    public Proposal(String insertString) {
        this.insertString = insertString;
    }

    @Override
    public String toString() {
        return insertString;
    }
}
