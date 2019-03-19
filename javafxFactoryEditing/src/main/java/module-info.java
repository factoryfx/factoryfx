module io.github.factoryfx.javafxFactoryEditing {
    requires transitive io.github.factoryfx.factory;
    requires transitive io.github.factoryfx.javafxDataEditing;
    requires transitive io.github.factoryfx.microserviceRestClient;
    requires com.fasterxml.jackson.databind;

    exports io.github.factoryfx.javafx.factory;

    exports io.github.factoryfx.javafx.factory.view;
    exports io.github.factoryfx.javafx.factory.view.factoryviewmanager;
    exports io.github.factoryfx.javafx.factory.view.container;
    exports io.github.factoryfx.javafx.factory.view.menu;

    exports io.github.factoryfx.javafx.factory.editor;
    exports io.github.factoryfx.javafx.factory.editor.attribute;
    exports io.github.factoryfx.javafx.factory.stage;
    exports io.github.factoryfx.javafx.factory.util;

    exports io.github.factoryfx.javafx.factory.widget.factory;
    exports io.github.factoryfx.javafx.factory.widget.factory.factorylog;
    exports io.github.factoryfx.javafx.factory.widget.factory.history;
    exports io.github.factoryfx.javafx.factory.widget.factory.diffdialog;
    exports io.github.factoryfx.javafx.factory.widget.factory.datatree;
    exports io.github.factoryfx.javafx.factory.widget.factory.dataview;

    opens io.github.factoryfx.javafx.factory.view.factoryviewmanager;//jackson
    opens io.github.factoryfx.javafx.factory.editor.attribute;
}