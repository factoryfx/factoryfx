package de.factoryfx.javafx.widget.table;

import java.text.DecimalFormat;

import de.factoryfx.data.Data;
import de.factoryfx.data.SearchTextMatchable;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.widget.Widget;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

public class TableControlWidget<T> implements Widget {
    private final HBox target = new HBox();
    private final TableView<T> tableView;
    TextField filterField;
    private final UniformDesign uniformDesign;
    private final TableMenu<T> tableMenu;

    public TableControlWidget(TableView<T> tableView, TableMenu<T> tableMenu,  UniformDesign uniformDesign) {
        this.tableMenu=tableMenu;
        this.tableView = tableView;
        this.uniformDesign = uniformDesign;

        setupTableControls();
        tableMenu.addMenu(tableView);
    }

    public TableControlWidget(TableView<T> tableView, UniformDesign uniformDesign) {
        this(tableView,new TableMenu<>(uniformDesign),uniformDesign);
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
        filterField = new TextField();//(CustomTextField) TextFields.createClearableTextField();

//        filterField.leftProperty().set(uniformDesign.createIcon(FontAwesome.Glyph.FILTER));
        filterField.disableProperty().bind(tableView.disabledProperty().or(tableView.itemsProperty().isNull()));
        filterField.setMinWidth(50);

        HBox.setHgrow(filterField, Priority.ALWAYS);
        final Label count = new Label("");
        FilterTextFieldListener<T> filterTextFieldListener = new FilterTextFieldListener<>();
        filterField.textProperty().addListener(filterTextFieldListener);

        InvalidationListener listener = (observableItems) -> {
            @SuppressWarnings("unchecked") ObservableList<T> list = ((SimpleObjectProperty<ObservableList<T>>) observableItems).get();
            if (!(list instanceof SortedList) && list != null) {
                FilteredList<T> filteredData = new FilteredList<>(list, p -> true);
                SortedList<T> sortedData = new SortedList<>(filteredData);
                tableView.setItems(sortedData);
                sortedData.comparatorProperty().bind(tableView.comparatorProperty());

                filterTextFieldListener.setFilteredData(filteredData);

                count.setText("" + filteredData.size());
                tableView.getItems().addListener((ListChangeListener<T>) c -> {
                    if (c != null) {
                        StringBuilder format = new StringBuilder();
                        for (int i = 0; i < String.valueOf(filteredData.getSource().size()).length(); i++) {
                            format.append("0");
                        }
                        DecimalFormat decimalFormat = new DecimalFormat(format.toString());
                        count.setText(decimalFormat.format(filteredData.size()));
                    }
                });
            }
            if (list == null) {
                count.setText("0");
            }
        };
        tableView.itemsProperty().addListener(listener);
        listener.invalidated(tableView.itemsProperty());

        target.getChildren().add(filterField);
        target.getChildren().add(new Separator(Orientation.VERTICAL));
        target.getChildren().add(count);

        target.setSpacing(3);
        target.setAlignment(Pos.CENTER_LEFT);
        target.setPadding(new Insets(2));

        target.setOpacity(0.35);

        int fadeTransitionDuration = 350;
        double minFadeValue = 0.20;
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
                if (!filterField.focusedProperty().get() && !tableView.focusedProperty().get()) {
                    target.getStyleClass().remove("selectedTableViewControl");
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
        FilteredList<T> filteredData;

        public FilterTextFieldListener() {
        }

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            filteredData.setPredicate(data -> {
                // If filter text is empty, display all.
                if (newValue==null || newValue.isEmpty()) {
                    return true;
                }
                if (data instanceof Data){
                    return ((Data)data).internal().matchSearchText(newValue);
                }
                if (data instanceof String){
                    return ((String) data).toLowerCase().contains(newValue.toLowerCase());
                }
                if (data instanceof SearchTextMatchable) {
                    return ((SearchTextMatchable)data).matchSearchText(newValue);
                }

                return true;
            });
        }

        public FilteredList<T> getFilteredData() {
            return filteredData;
        }

        public void setFilteredData(FilteredList<T> filteredData) {
            this.filteredData = filteredData;
        }
    }
}
