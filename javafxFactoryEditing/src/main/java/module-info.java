module de.factoryfx.javafxFactoryEditing {
    requires javafx.graphics;
    requires javafx.controls;
    requires de.factoryfx.factory;
    requires de.factoryfx.javafxDataEditing;
    requires de.factoryfx.data;
    requires de.factoryfx.microserviceRestClient;
    requires controlsfx;
    requires com.google.common;

    exports de.factoryfx.javafx.factory;

    exports de.factoryfx.javafx.factory.view;
    exports de.factoryfx.javafx.factory.view.factoryviewmanager;
    exports de.factoryfx.javafx.factory.view.container;
    exports de.factoryfx.javafx.factory.view.menu;

    exports de.factoryfx.javafx.factory.editor;
    exports de.factoryfx.javafx.factory.editor.attribute;
    exports de.factoryfx.javafx.factory.stage;
    exports de.factoryfx.javafx.factory.util;

    exports de.factoryfx.javafx.factory.widget.factory;
    exports de.factoryfx.javafx.factory.widget.factory.factorylog;
    exports de.factoryfx.javafx.factory.widget.factory.history;
    exports de.factoryfx.javafx.factory.widget.factory.diffdialog;
    exports de.factoryfx.javafx.factory.widget.factory.datatree;
}