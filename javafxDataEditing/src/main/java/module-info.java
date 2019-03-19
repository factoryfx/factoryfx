module io.github.factoryfx.javafxDataEditing {
    requires transitive javafx.graphics;
    requires transitive javafx.controls;
    requires transitive io.github.factoryfx.data;
    requires transitive javafx.web;
    requires transitive javafx.fxml;
    requires com.google.common;
    requires transitive org.controlsfx.controls;

    exports io.github.factoryfx.javafx.data.attribute;
    exports io.github.factoryfx.javafx.data.editor.attribute;
    opens io.github.factoryfx.javafx.data.editor.attribute;
    exports io.github.factoryfx.javafx.data.editor.attribute.builder;
    exports io.github.factoryfx.javafx.data.editor.attribute.visualisation;
    exports io.github.factoryfx.javafx.data.editor.attribute.converter;
    exports io.github.factoryfx.javafx.data.editor.data;

    exports io.github.factoryfx.javafx.data.util;
    exports io.github.factoryfx.javafx.data.widget;
    exports io.github.factoryfx.javafx.data.widget.datalistedit;
    exports io.github.factoryfx.javafx.data.widget.dataview;
    exports io.github.factoryfx.javafx.data.widget.factorydiff;
    exports io.github.factoryfx.javafx.data.widget.select;
    exports io.github.factoryfx.javafx.data.widget.table;
    exports io.github.factoryfx.javafx.data.widget.tree;
    exports io.github.factoryfx.javafx.data.widget.validation;

    exports io.github.factoryfx.javafx.css;
    opens io.github.factoryfx.javafx.css;

}