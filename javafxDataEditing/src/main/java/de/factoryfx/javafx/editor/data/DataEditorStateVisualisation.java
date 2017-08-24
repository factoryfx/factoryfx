package de.factoryfx.javafx.editor.data;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.AttributeChangeListener;
import de.factoryfx.data.attribute.AttributeGroup;
import de.factoryfx.data.attribute.WeakAttributeChangeListener;
import de.factoryfx.data.validation.ValidationError;
import de.factoryfx.javafx.editor.attribute.AttributeEditor;
import de.factoryfx.javafx.editor.attribute.AttributeEditorBuilder;
import de.factoryfx.javafx.util.UniformDesign;
import impl.org.controlsfx.skin.BreadCrumbBarSkin;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.controlsfx.control.BreadCrumbBar;
import org.controlsfx.glyphfont.FontAwesome;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DataEditorStateVisualisation extends BorderPane {
    private final UniformDesign uniformDesign;
    private final HashMap<Attribute<?,?>,AttributeEditor<?,?>> createdEditors=new HashMap<>();
    private final AttributeChangeListener validationListener;
    private final AttributeEditorBuilder attributeEditorBuilder;
    private final DataEditor dataEditor;
    private final BiFunction<Node,Data,Node> visCustomizer;

    public DataEditorStateVisualisation(Data currentData, List<Data> displayedEntities,Optional<Data> previousData, Optional<Data> nextData,  AttributeEditorBuilder attributeEditorBuilder, UniformDesign uniformDesign, DataEditor dataEditor, BiFunction<Node,Data,Node> visCustomizer){
        this.uniformDesign=uniformDesign;
        this.attributeEditorBuilder=attributeEditorBuilder;
        this.dataEditor = dataEditor;
        this.visCustomizer = visCustomizer;

        validationListener = (attribute, value) -> {
            updateValidation(currentData);
        };

        Data previousValue=null;
        if (displayedEntities.size()>1){
            previousValue=displayedEntities.get(displayedEntities.size()-2);
        }
        setCenter(createEditor(currentData,previousValue));
        setTop(createNavigation(displayedEntities,currentData,previousData,nextData));
    }

    private Node customizeVis(Node defaultVis, Data data){
        return visCustomizer.apply(defaultVis,data);
    }

    private Node createEditor(Data newValue, Data previousValue){
        if (newValue==null) {
            return new Label("empty");
        } else {
            newValue.internal().visitAttributesFlat((attributeVariableName, attribute) -> {
                attribute.internal_addListener(new WeakAttributeChangeListener<>(validationListener));
            });


            if (newValue.internal().attributeListGrouped().size()==1){
                final Node attributeGroupVisual = createAttributeGroupVisual(newValue.internal().attributeListGrouped().get(0).group, () -> newValue.internal().validateFlat(),previousValue);
                return customizeVis(attributeGroupVisual,newValue);
            } else {
                TabPane tabPane = new TabPane();
                for (AttributeGroup attributeGroup: newValue.internal().attributeListGrouped()) {

                    Tab tab=new Tab(uniformDesign.getText(attributeGroup.title));
                    tab.setClosable(false);
                    tab.setContent(createAttributeGroupVisual(attributeGroup.group,() -> newValue.internal().validateFlat(),previousValue));
                    tabPane.getTabs().add(tab);
                }
                return customizeVis(tabPane,newValue);
            }



        }
    }

    private void updateValidation(Data newValue) {
        final Map<Attribute<?,?>,List<ValidationError>> attributeToErrors = newValue.internal().validateFlatMapped();

        for (Map.Entry<Attribute<?,?>,List<ValidationError>> entry: attributeToErrors.entrySet()){
            final AttributeEditor<?,?> attributeEditor = createdEditors.get(entry.getKey());
            if (attributeEditor!=null){
                attributeEditor.reportValidation(entry.getValue());
            }
        }
    }


    private Node createNavigation(List<Data> displayedEntities, Data currentData, Optional<Data> previousData, Optional<Data> nextData){
        BreadCrumbBarWidthFixed<Data> breadCrumbBar = new BreadCrumbBarWidthFixed<>();


        ScrollPane scrollPaneBreadCrumbBar = new ScrollPane();
        scrollPaneBreadCrumbBar.setContent(breadCrumbBar);
        scrollPaneBreadCrumbBar.setHvalue(1.0);
        scrollPaneBreadCrumbBar.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPaneBreadCrumbBar.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPaneBreadCrumbBar.getStyleClass().add("transparent-scroll-pane");

        //force hvalue to 1 cause its buggy
        scrollPaneBreadCrumbBar.hvalueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue!=null && newValue.doubleValue()<1.0){
                scrollPaneBreadCrumbBar.setHvalue(1);
            }
        });

        breadCrumbBar.setCrumbFactory(param -> {
            BreadCrumbBarSkin.BreadCrumbButton breadCrumbButton = new BreadCrumbBarSkin.BreadCrumbButton("");
            if (param.getValue()!=null){
                breadCrumbButton.textProperty().bind(param.getValue().internal().getDisplayTextObservable());
            }
            if (currentData==param.getValue()){
                breadCrumbButton.setStyle("-fx-font-weight: bold;");
            }
            return breadCrumbButton;
        });
        breadCrumbBar.setOnCrumbAction(event -> {
            dataEditor.edit(event.getSelectedCrumb().getValue());
        });


//        List<Data> newhistory = new ArrayList<>();
//        for (Data data: displayedEntities){
//            newhistory.add(data);
//            if (data==currentData){
//                break;
//            }
//        }

        breadCrumbBar.setSelectedCrumb(BreadCrumbBar.buildTreeModel(displayedEntities.toArray(new Data[0])));
        breadCrumbBar.layout();



        HBox navigation = new HBox(3);
        navigation.getStyleClass().add("navigationhbox");
        navigation.setAlignment(Pos.TOP_LEFT);
        Button back = new Button("",uniformDesign.createIcon(FontAwesome.Glyph.CARET_LEFT).size(18));
        back.setOnAction(event -> dataEditor.back());
        back.setDisable(!previousData.isPresent());
        Button next = new Button("",uniformDesign.createIcon(FontAwesome.Glyph.CARET_RIGHT).size(18));
        next.setDisable(!nextData.isPresent());
        next.setOnAction(event -> dataEditor.next());

        navigation.getChildren().add(back);
        navigation.getChildren().add(next);


        navigation.getChildren().add(scrollPaneBreadCrumbBar);

        navigation.setPadding(new Insets(3,3,0,3));//workaround scrollpane fittoheight and  .setAlignment(Pos.TOP_LEFT); don't work in this cas

        HBox.setHgrow(scrollPaneBreadCrumbBar, Priority.ALWAYS);
        return navigation;
    }

    private Node createAttributeGroupVisual(List<Attribute<?,?>> attributeGroup, Supplier<List<ValidationError>> validation, Data oldValue) {
        if (attributeGroup.size()==1){
            final Attribute<?,?> attribute = attributeGroup.get(0);
            AttributeEditor<?,?> attributeEditor = attributeEditorBuilder.getAttributeEditor(attribute, dataEditor, validation, oldValue);
            attributeEditor.expand();
            createdEditors.put(attribute,attributeEditor);
            final Node content = attributeEditor.createContent();
            final VBox vBox = new VBox(3);
            vBox.setPadding(new Insets(3));
            VBox.setVgrow(content,Priority.ALWAYS);
            vBox.getChildren().addAll(new Label(uniformDesign.getLabelText(attribute)),content);
            return vBox;

        } else {
            GridPane grid = new GridPane();
            grid.setPadding(new Insets(3, 3, 3, 3));

            ColumnConstraints column1 = new ColumnConstraints();
            column1.setHgrow(Priority.SOMETIMES);
            column1.setMinWidth(100);
            column1.setPrefWidth(200);
            ColumnConstraints column2 = new ColumnConstraints();
            column2.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().addAll(column1, column2);


            int row = 0;
            for (Attribute<?,?> attribute: attributeGroup){
                Label label = addLabelContent(grid, row,uniformDesign.getLabelText(attribute));

                AttributeEditor<?,?> attributeEditor = attributeEditorBuilder.getAttributeEditor(attribute,dataEditor,validation,oldValue);
                int rowFinal=row;
                createdEditors.put(attribute,attributeEditor);
                addEditorContent(grid, rowFinal, attributeEditor.createContent(),label);

                RowConstraints rowConstraints = new RowConstraints();
                rowConstraints.setVgrow(Priority.ALWAYS);
                grid.getRowConstraints().add(rowConstraints);

                row++;
            }

            for (RowConstraints rowConstraint: grid.getRowConstraints()){
                rowConstraint.setMinHeight(36);
            }

            return wrapGrid(grid);
        }
    }

    private void addEditorContent(GridPane gridPane, int row, Node editorWidgetContent, Label label) {
        GridPane.setMargin(editorWidgetContent, new Insets(4, 0, 4, 0));
//        label.setLabelFor(editorWidgetContent);


        StackPane pane = new StackPane();
        pane.setAlignment(Pos.CENTER_LEFT);
        pane.getChildren().add(editorWidgetContent);
        pane.setPadding(new Insets(3,0,3,0));
        gridPane.add(pane, 1, row);

        if (row%2==0) {
            pane.setStyle("-fx-background-color: "+ highlightBackground + ";");
        }
    }


    private Label addLabelContent(GridPane gridPane, int row,String text) {
        String mnemonicLabelText=text;
        if (mnemonicLabelText!=null){
            mnemonicLabelText="_"+mnemonicLabelText;
        }
        Label label = new Label(mnemonicLabelText);
        label.setMnemonicParsing(true);
        label.setWrapText(true);
//        label.setTextOverrun(OverrunStyle.CLIP);
        GridPane.setMargin(label, new Insets(0, 9, 0, 0));
        StackPane pane = new StackPane();
        pane.setPadding(new Insets(3,3,3,0));
        pane.setAlignment(Pos.CENTER_LEFT);
        pane.getChildren().add(label);
        gridPane.add(pane, 0, row);

        if (row%2==0) {
            pane.setStyle("-fx-background-color: " + highlightBackground + ";");
        }
        return label;
    }
    final static String highlightBackground = "#FCFCFC";

    private Node wrapGrid(GridPane gridPane){
        ScrollPane scrollPane = new ScrollPane(gridPane);
        scrollPane.setFitToWidth(true);
//        root.setFitToHeight(scrollPaneFitToHeight);
        scrollPane.setStyle("-fx-background-color:transparent;");//hide border
//        root.disableProperty().edit(disabledProperty());
        return scrollPane;
    }
}
