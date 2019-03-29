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


    exports io.github.factoryfx.javafx.factory;

    exports io.github.factoryfx.javafx.factory.view;
    exports io.github.factoryfx.javafx.factory.factoryviewmanager;
    exports io.github.factoryfx.javafx.factory.view.container;
    exports io.github.factoryfx.javafx.factory.view.menu;


    exports io.github.factoryfx.javafx.factory.stage;
    exports io.github.factoryfx.javafx.factory.util;

    exports io.github.factoryfx.javafx.factory.widget.factory;
    exports io.github.factoryfx.javafx.factory.widget.factory.factorylog;
    exports io.github.factoryfx.javafx.factory.widget.factory.history;
    exports io.github.factoryfx.javafx.factory.widget.factory.diffdialog;
    exports io.github.factoryfx.javafx.factory.widget.factory.datatree;
    exports io.github.factoryfx.javafx.factory.widget.factory.dataview;

    opens io.github.factoryfx.javafx.factory.factoryviewmanager;//jackson



    exports io.github.factoryfx.javafx.factory.editor.attribute;
    opens io.github.factoryfx.javafx.factory.editor.attribute;
    exports io.github.factoryfx.javafx.factory.editor.attribute.builder;
    exports io.github.factoryfx.javafx.factory.editor.attribute.visualisation;
    exports io.github.factoryfx.javafx.factory.editor.attribute.converter;
    exports io.github.factoryfx.javafx.factory.editor.data;

    exports io.github.factoryfx.javafx.factory.widget;
    exports io.github.factoryfx.javafx.factory.widget.datalistedit;
    exports io.github.factoryfx.javafx.factory.widget.dataview;
    exports io.github.factoryfx.javafx.factory.widget.factorydiff;
    exports io.github.factoryfx.javafx.factory.widget.select;
    exports io.github.factoryfx.javafx.factory.widget.table;
    exports io.github.factoryfx.javafx.factory.widget.tree;
    exports io.github.factoryfx.javafx.factory.widget.validation;

    exports io.github.factoryfx.javafx.factory.css;
    opens io.github.factoryfx.javafx.factory.css;
    exports io.github.factoryfx.javafx.factory.editor;
}