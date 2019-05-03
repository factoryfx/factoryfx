module io.github.factoryfx.javafxFactoryEditing {
    requires transitive io.github.factoryfx.factory;
    requires transitive io.github.factoryfx.microserviceRestClient;
    requires com.fasterxml.jackson.databind;

    requires transitive javafx.graphics;
    requires transitive javafx.controls;
    requires transitive javafx.web;
    requires transitive javafx.fxml;
    requires com.google.common;
    requires transitive org.controlsfx.controls;


    exports io.github.factoryfx.javafx;

    exports io.github.factoryfx.javafx.view;
    exports io.github.factoryfx.javafx.factoryviewmanager;
    exports io.github.factoryfx.javafx.view.container;
    exports io.github.factoryfx.javafx.view.menu;


    exports io.github.factoryfx.javafx.stage;
    exports io.github.factoryfx.javafx.util;

    exports io.github.factoryfx.javafx.widget.factory;
    exports io.github.factoryfx.javafx.widget.factory.factorylog;
    exports io.github.factoryfx.javafx.widget.factory.history;
    exports io.github.factoryfx.javafx.widget.factory.diffdialog;
    exports io.github.factoryfx.javafx.widget.factory.tree;
    exports io.github.factoryfx.javafx.widget.factory.masterdetail;

    opens io.github.factoryfx.javafx.factoryviewmanager;//jackson



    exports io.github.factoryfx.javafx.editor.attribute;
    opens io.github.factoryfx.javafx.editor.attribute;
    exports io.github.factoryfx.javafx.editor.attribute.builder;
    exports io.github.factoryfx.javafx.editor.attribute.visualisation;
    exports io.github.factoryfx.javafx.editor.attribute.converter;
    exports io.github.factoryfx.javafx.editor;

    exports io.github.factoryfx.javafx.widget;
    exports io.github.factoryfx.javafx.widget.factory.listedit;
    exports io.github.factoryfx.javafx.widget.factorydiff;
    exports io.github.factoryfx.javafx.widget.select;
    exports io.github.factoryfx.javafx.widget.table;
    exports io.github.factoryfx.javafx.widget.validation;

    exports io.github.factoryfx.javafx.css;
    opens io.github.factoryfx.javafx.css;
}