module de.factoryfx.javafxDataEditing {
    requires javafx.graphics;
    requires javafx.controls;
    requires de.factoryfx.data;
    requires controlsfx;
    requires javafx.web;
    requires javafx.fxml;
    requires jackson.annotations;
    requires com.google.common;
    exports de.factoryfx.javafx.data.widget;
    exports de.factoryfx.javafx.data.util;
    exports de.factoryfx.javafx.data.attribute;
    exports de.factoryfx.javafx.data.editor.attribute;
    exports de.factoryfx.javafx.data.widget.table;
    exports de.factoryfx.javafx.data.widget.factorydiff;
    exports de.factoryfx.javafx.data.editor.data;
    exports de.factoryfx.javafx.data.widget.validation;
    exports de.factoryfx.javafx.data.editor.attribute.builder;
    exports de.factoryfx.javafx.data.stage;


}