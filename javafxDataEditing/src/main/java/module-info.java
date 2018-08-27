module de.factoryfx.javafxDataEditing {
    requires javafx.graphics;
    requires javafx.controls;
    requires de.factoryfx.data;
    requires controlsfx;
    requires javafx.web;
    requires javafx.fxml;
    requires jackson.annotations;
    requires com.google.common;

    exports de.factoryfx.javafx.data.attribute;
    exports de.factoryfx.javafx.data.editor.attribute;
    opens de.factoryfx.javafx.data.editor.attribute;
    exports de.factoryfx.javafx.data.editor.attribute.builder;
    exports de.factoryfx.javafx.data.editor.attribute.visualisation;
    exports de.factoryfx.javafx.data.editor.attribute.converter;
    exports de.factoryfx.javafx.data.editor.data;

    exports de.factoryfx.javafx.data.util;
    exports de.factoryfx.javafx.data.widget;
    exports de.factoryfx.javafx.data.widget.datalistedit;
    exports de.factoryfx.javafx.data.widget.dataview;
    exports de.factoryfx.javafx.data.widget.factorydiff;
    exports de.factoryfx.javafx.data.widget.select;
    exports de.factoryfx.javafx.data.widget.table;
    exports de.factoryfx.javafx.data.widget.tree;
    exports de.factoryfx.javafx.data.widget.validation;

    exports de.factoryfx.javafx.css;
    opens de.factoryfx.javafx.css;
}