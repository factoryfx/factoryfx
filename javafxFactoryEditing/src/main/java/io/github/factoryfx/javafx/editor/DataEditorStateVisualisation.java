package io.github.factoryfx.javafx.editor;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.AttributeAndMetadata;
import io.github.factoryfx.factory.attribute.AttributeGroup;
import io.github.factoryfx.factory.validation.ValidationError;
import io.github.factoryfx.javafx.editor.attribute.AttributeVisualisationMappingBuilder;
import io.github.factoryfx.javafx.util.ObservableFactoryDisplayText;
import io.github.factoryfx.javafx.util.UniformDesign;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import org.controlsfx.control.BreadCrumbBar;
import org.controlsfx.glyphfont.FontAwesome;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DataEditorStateVisualisation extends BorderPane {
    private final UniformDesign uniformDesign;
    private final AttributeVisualisationMappingBuilder attributeVisualisationMappingBuilder;
    private final DataEditor dataEditor;
    private final BiFunction<Node, FactoryBase<?, ?>, Node> visCustomizer;

    public DataEditorStateVisualisation(FactoryBase<?, ?> currentData, ArrayDeque<FactoryBase<?, ?>> displayedEntities, boolean previousDataEmpty, boolean nextDataEmpty, AttributeVisualisationMappingBuilder attributeVisualisationMappingBuilder, UniformDesign uniformDesign, DataEditor dataEditor, BiFunction<Node, FactoryBase<?, ?>, Node> visCustomizer, boolean showNavigation, boolean showUsages) {
        this.uniformDesign = uniformDesign;
        this.attributeVisualisationMappingBuilder = attributeVisualisationMappingBuilder;
        this.dataEditor = dataEditor;
        this.visCustomizer = visCustomizer;

        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);

        FactoryBase<?, ?> previousValue = null;
        if (displayedEntities.size() > 1) {
            previousValue = displayedEntities.peek();
        }
        splitPane.getItems().add(createEditor(currentData, previousValue));
        setCenter(splitPane);

        if (showNavigation) {
            setTop(createNavigation(displayedEntities, currentData, previousDataEmpty, nextDataEmpty));
        }
        if (currentData != null && showUsages) {
            createUsages(currentData, splitPane);
        }
    }

    private void createUsages(FactoryBase<?, ?> currentData, SplitPane splitPane) {
        Set<FactoryBase<?, ?>> usages = new HashSet<>();
        List<? extends FactoryBase<?, ?>> factoryBases = currentData.utility().getRoot().internal().collectChildrenDeep();
        for (FactoryBase<?, ?> factoryBase : factoryBases) {
            for (FactoryBase<?, ?> child : factoryBase.internal().collectChildrenFlat()) {
                if (child == currentData) {
                    usages.add(factoryBase);
                }
            }
        }
        TableView<FactoryBase<?, ?>> table = new TableView<>();
        table.getItems().addAll(usages);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<FactoryBase<?, ?>, String> factory = new TableColumn<>("Factory");
        table.getColumns().add(factory);
        factory.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().internal().getDisplayText()));

        TableColumn<FactoryBase<?, ?>, String> path = new TableColumn<>("Path");
        table.getColumns().add(path);
        path.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().internal().getPathFromRoot().stream().map(f -> f.internal().getDisplayText()).collect(Collectors.joining("/"))));

        table.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                dataEditor.navigate(table.getSelectionModel().getSelectedItem());
            }
        });

        VBox vBox = new VBox(3);
        uniformDesign.setBackground(vBox);

        Label header = new Label("Dependent factories");
        vBox.getChildren().add(header);
        header.setFont(new Font(16));
        header.setStyle("-fx-font-weight: bold;-fx-text-fill: white;");
        VBox.setMargin(header, new Insets(3, 3, 0, 3));

        Label description = new Label("Factory is referenced in the factories: (Changes affect these factories.)");
        vBox.getChildren().add(description);
        description.setStyle("-fx-text-fill: white;");
        VBox.setMargin(description, new Insets(0, 3, 0, 3));

        vBox.getChildren().add(table);
        VBox.setMargin(table, new Insets(3));
        VBox.setVgrow(table, Priority.ALWAYS);

        if (usages.size() > 1) {
            splitPane.getItems().add(vBox);
            splitPane.setDividerPositions(0.7, 0.3);
        }
    }

    private Node customizeVis(Node defaultVis, FactoryBase<?, ?> data) {
        return visCustomizer.apply(defaultVis, data);
    }

    private Node createEditor(FactoryBase<?, ?> newValue, FactoryBase<?, ?> previousValue) {
        if (newValue == null) {
            return new Label("empty");
        } else {

            if (newValue.internal().attributeListGrouped().size() == 1) {
                final Node attributeGroupVisual = createAttributeGroupVisual(newValue.internal().attributeListGrouped().get(0).group, previousValue, () -> newValue.internal().validateFlat());
                return customizeVis(attributeGroupVisual, newValue);
            } else {
                TabPane tabPane = new TabPane();
                for (AttributeGroup attributeGroup : newValue.internal().attributeListGrouped()) {

                    Tab tab = new Tab(uniformDesign.getText(attributeGroup.title));
                    tab.setClosable(false);
                    tab.setContent(createAttributeGroupVisual(attributeGroup.group, previousValue, () -> newValue.internal().validateFlat()));
                    tabPane.getTabs().add(tab);
                }
                return customizeVis(tabPane, newValue);
            }

        }
    }

    private Node createNavigation(ArrayDeque<FactoryBase<?, ?>> displayedEntities, FactoryBase<?, ?> currentData, boolean previousDataEmpty, boolean nextDataEmpty) {
        BreadCrumbBar<FactoryBase<?, ?>> breadCrumbBar = new BreadCrumbBar<>();

        ScrollPane scrollPaneBreadCrumbBar = new ScrollPane();
        scrollPaneBreadCrumbBar.setContent(breadCrumbBar);
        scrollPaneBreadCrumbBar.setHvalue(1.0);
        scrollPaneBreadCrumbBar.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPaneBreadCrumbBar.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPaneBreadCrumbBar.getStyleClass().add("transparent-scroll-pane");

        //force hvalue to 1 cause its buggy
        scrollPaneBreadCrumbBar.hvalueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.doubleValue() < 1.0) {
                scrollPaneBreadCrumbBar.setHvalue(1);
            }
        });

        breadCrumbBar.setCrumbFactory(param -> {
            //            Button BreadCrumbButton breadCrumbButton = new BreadCrumbBarSkin.BreadCrumbButton("");
            Button breadCrumbButton = new Button();
            breadCrumbButton.setStyle("-fx-background-radius: 0");

            if (param.getValue() != null) {
                breadCrumbButton.textProperty().bind(new ObservableFactoryDisplayText(param.getValue()));
            }
            if (currentData == param.getValue()) {
                breadCrumbButton.setStyle("-fx-background-radius: 0; -fx-font-weight: bold;");
            }
            return breadCrumbButton;
        });
        breadCrumbBar.setOnCrumbAction(event -> dataEditor.navigateBack(event.getSelectedCrumb().getValue()));

        //        List<Data> newhistory = new ArrayList<>();
        //        for (Data data: displayedEntities){
        //            newhistory.add(data);
        //            if (data==currentData){
        //                break;
        //            }
        //        }

        ArrayList<FactoryBase<?, ?>> breadcrumbData = new ArrayList<>(displayedEntities);
        Collections.reverse(breadcrumbData);
        breadcrumbData.add(currentData);
        breadCrumbBar.setSelectedCrumb(BreadCrumbBar.buildTreeModel(breadcrumbData.toArray(new FactoryBase<?, ?>[0])));
        breadCrumbBar.layout();

        HBox navigation = new HBox(3);
        navigation.getStyleClass().add("navigationhbox");
        navigation.setAlignment(Pos.TOP_LEFT);
        Button back = new Button("", uniformDesign.createIcon(FontAwesome.Glyph.CARET_LEFT));
        back.prefHeightProperty().bind(breadCrumbBar.heightProperty().add(1));
        back.setOnAction(event -> dataEditor.back());
        back.setDisable(previousDataEmpty);
        Button next = new Button("", uniformDesign.createIcon(FontAwesome.Glyph.CARET_RIGHT));
        next.prefHeightProperty().bind(breadCrumbBar.heightProperty().add(1));
        next.setDisable(nextDataEmpty);
        next.setOnAction(event -> dataEditor.next());

        navigation.getChildren().add(back);
        navigation.getChildren().add(next);

        navigation.getChildren().add(scrollPaneBreadCrumbBar);

        navigation.setPadding(new Insets(3, 3, 0, 3));//workaround scrollpane fittoheight and  .setAlignment(Pos.TOP_LEFT); don't work in this cas

        HBox.setHgrow(scrollPaneBreadCrumbBar, Priority.ALWAYS);
        return navigation;
    }

    List<AttributeGroupEditor> createdAttributeGroupEditor = new ArrayList<>();//prevent gc for weak listeners

    private Node createAttributeGroupVisual(List<AttributeAndMetadata> attributeGroup, FactoryBase<?, ?> oldValue, Supplier<List<ValidationError>> additionalValidation) {
        AttributeGroupEditor attributeGroupEditor = new AttributeGroupEditor(attributeGroup, oldValue, attributeVisualisationMappingBuilder, dataEditor, uniformDesign, additionalValidation);
        createdAttributeGroupEditor.add(attributeGroupEditor);
        return attributeGroupEditor.createContent();
    }

    public void destroy() {
        for (AttributeGroupEditor attributeGroupEditor : createdAttributeGroupEditor) {
            attributeGroupEditor.destroy();
        }
    }
}
