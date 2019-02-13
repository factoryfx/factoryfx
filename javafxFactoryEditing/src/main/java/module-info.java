module de.factoryfx.javafxFactoryEditing {
    requires controlsfx;

    requires transitive de.factoryfx.factory;
    requires transitive de.factoryfx.javafxDataEditing;
    requires transitive de.factoryfx.microserviceRestClient;
    requires com.fasterxml.jackson.databind;

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
    exports de.factoryfx.javafx.factory.widget.factory.dataview;

    opens de.factoryfx.javafx.factory.view.factoryviewmanager;//jackson
    opens de.factoryfx.javafx.factory.editor.attribute;
}