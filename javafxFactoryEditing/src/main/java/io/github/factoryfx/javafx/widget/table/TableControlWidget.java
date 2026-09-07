package io.github.factoryfx.javafx.widget.table;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Duration;

import com.google.common.base.Strings;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.javafx.util.UniformDesign;
import io.github.factoryfx.javafx.widget.Widget;

public class TableControlWidget<T> implements Widget {
    private final HBox target = new HBox();
    private final TableView<T> tableView;
    private final TextField filterField;

    public TableControlWidget(TableView<T> tableView, TableMenu<T> tableMenu) {
        this.tableView = tableView;
        this.filterField = new TextField();
        setupTableControls();
        tableMenu.addMenu(tableView);
    }

    public TableControlWidget(TableView<T> tableView, UniformDesign uniformDesign) {
        this(tableView, new TableMenu<>(uniformDesign));
    }

    public void clearFilter() {
        filterField.clear();
    }

    @Override
    public Node createContent() {
        return target;
    }

    public TableControlWidget<T> hide() {
        target.visibleProperty().set(false);
        return this;
    }

    public void setFilterText(String filter) {
        filterField.setText(filter);
    }

    private void setupTableControls() {
        filterField.disableProperty().bind(tableView.disabledProperty().or(tableView.itemsProperty().isNull()));
        filterField.setMinWidth(50);

        HBox.setHgrow(filterField, Priority.ALWAYS);

        final Label count = new Label("");
        FilterTextFieldListener<T> filterTextFieldListener = new FilterTextFieldListener<>();
        filterField.textProperty().addListener(filterTextFieldListener);

        //the items are replaced with an unmodifiable filtered/sorted view of the original list.
        //re-wrap whenever someone later replaces the items via tableView.setItems(...), otherwise the
        //filter field and the count label would silently operate on the discarded list. A SortedList is
        //left untouched: it is either our own wrap (recursive trigger from setItems below) or an external
        //wrapper around our chain (e.g. ControlsFX TableFilter), which keeps the filter functional
        InvalidationListener itemsListener = observable -> {
            ObservableList<T> items = tableView.getItems();
            if (items instanceof SortedList) {
                return;
            }
            FilteredList<T> filteredList = new FilteredList<>(items == null ? FXCollections.emptyObservableList() : items, null);
            SortedList<T> sortedList = new SortedList<>(filteredList);
            tableView.setItems(sortedList);
            sortedList.comparatorProperty().bind(tableView.comparatorProperty());

            filterTextFieldListener.setFilteredList(filteredList);
            filterTextFieldListener.changed(filterField.textProperty(), null, filterField.getText());

            count.setText(String.valueOf(filteredList.size()));
            filteredList.addListener((ListChangeListener.Change<? extends T> change) -> count.setText(String.valueOf(change.getList().size())));
        };
        tableView.itemsProperty().addListener(itemsListener);
        itemsListener.invalidated(tableView.itemsProperty());

        target.getChildren().add(new Label("Filter:"));
        target.getChildren().add(filterField);
        target.getChildren().add(new Separator(Orientation.VERTICAL));
        target.getChildren().add(count);

        target.setSpacing(3);
        target.setAlignment(Pos.CENTER_LEFT);
        target.setPadding(new Insets(2));

        int fadeTransitionDuration = 350;
        double minFadeValue = 0.20;
        target.setOpacity(minFadeValue);
        ChangeListener<Boolean> focusChangeListener = (observable, oldValue, newValue) -> {
            target.getStyleClass().remove("selectedTableViewControl");
            if (newValue) {
                if (!filterField.focusedProperty().get()) {
                    target.getStyleClass().add("selectedTableViewControl");
                }
                if (target.getOpacity() < 0.999999) {
                    FadeTransition ft = new FadeTransition(Duration.millis(fadeTransitionDuration), target);
                    ft.setFromValue(minFadeValue);
                    ft.setToValue(1.0);
                    ft.play();
                }
            } else {
                if (!filterField.focusedProperty().get() && !tableView.focusedProperty().get() && Strings.isNullOrEmpty(filterField.getText())) {
                    FadeTransition ft = new FadeTransition(Duration.millis(fadeTransitionDuration), target);
                    ft.setFromValue(1.0);
                    ft.setToValue(minFadeValue);
                    ft.play();
                }
            }
        };
        tableView.focusedProperty().addListener(focusChangeListener);
        filterField.focusedProperty().addListener(focusChangeListener);

        //workaround for bug http://stackoverflow.com/questions/37423748/javafx-tablecolumns-headers-not-aligned-with-cells-due-to-vertical-scrollbar
        Platform.runLater(tableView::refresh);

    }

    private static class FilterTextFieldListener<T> implements ChangeListener<String> {
        private FilteredList<T> filteredList;

        void setFilteredList(FilteredList<T> filteredList) {
            this.filteredList = filteredList;
        }

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            filteredList.setPredicate(data -> {
                // If filter text is empty, display all.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                if (data instanceof FactoryBase<?, ?> fb) {
                    return fb.internal().matchSearchText(newValue);
                }
                if (data instanceof String s) {
                    return s.toLowerCase().contains(newValue.toLowerCase());
                }
                if (data instanceof SearchTextMatchable stm) {
                    return stm.matchSearchText(newValue);
                }
                return true;
            });
        }
    }
}
