package de.factoryfx.javafx.widget;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableView;

public interface TableInitializer<T> {
    void initTable(TableView<T> tableView);

    default SimpleStringProperty wrap(String value) {
        return new SimpleStringProperty(value);
    }

    default <E> SimpleObjectProperty<E> wrap(E value) {
        return new SimpleObjectProperty<>(value);
    }

}
