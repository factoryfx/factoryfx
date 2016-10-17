package de.factoryfx.javafx.editor.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.javafx.editor.attribute.AttributeEditor;
import de.factoryfx.javafx.editor.attribute.AttributeEditorFactory;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.widget.Widget;
import impl.org.controlsfx.skin.BreadCrumbBarSkin;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import org.controlsfx.control.BreadCrumbBar;
import org.controlsfx.glyphfont.FontAwesome;

public class DataEditor implements Widget {
    static final int HISTORY_LIMIT = 20;

    private final AttributeEditorFactory attributeEditorFactory;
    private final UniformDesign uniformDesign;
    SimpleObjectProperty<Data> bound = new SimpleObjectProperty<>();

    public DataEditor(AttributeEditorFactory attributeEditorFactory, UniformDesign uniformDesign) {
        this.attributeEditorFactory = attributeEditorFactory;
        this.uniformDesign = uniformDesign;
    }

    public ReadOnlyObjectProperty<Data> editData(){
        return bound;
    }

    public void edit(Data newValue) {
        if (!displayedEntities.contains(newValue)){
            displayedEntities.add(newValue);
            if (displayedEntities.size()>HISTORY_LIMIT){
                displayedEntities.remove(0);
            }
        }
        bound.set(newValue);
    }

    List<AttributeEditor<?>> createdEditors=new ArrayList<>();
    BooleanBinding scrollerVisible;

    @Override
    public Node createContent() {


        GridPane grid = new GridPane();
        grid.setHgap(3);
        grid.setVgap(3);
        grid.setPadding(new Insets(3, 3, 3, 3));

        ColumnConstraints column1 = new ColumnConstraints();
        column1.setHgrow(Priority.SOMETIMES);
        column1.setMinWidth(100);
        column1.setPrefWidth(200);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(column1, column2);

        ChangeListener<Data> dataChangeListener = (observable, oldValue, newValue) -> {
            grid.getChildren().clear();
            createdEditors.stream().forEach(AttributeEditor::unbind);
            createdEditors.clear();

            if (newValue!=null){
                int row = 0;
                for (Attribute<?> attribute: newValue.attributeList()) {
                    addLabelContent(grid, row,uniformDesign.getLabelText(attribute));

                    Optional<AttributeEditor<?>> attributeEditor = attributeEditorFactory.getAttributeEditor(attribute,this);
                    int rowFinal=row;
                    if (attributeEditor.isPresent()){
                        createdEditors.add(attributeEditor.get());
                        addEditorContent(grid, rowFinal, attributeEditor.get().createContent());
                    } else {
                        addEditorContent(grid, rowFinal, new Label("unsupported attribute:"+attribute.getAttributeType().dataType+", "+attribute.getAttributeType().listItemType));
                    }



                    RowConstraints rowConstraints = new RowConstraints();
                    rowConstraints.setVgrow(Priority.ALWAYS);
                    grid.getRowConstraints().add(rowConstraints);

                    row++;
                }
            }

        };
        bound.addListener(dataChangeListener);
        dataChangeListener.changed(bound,bound.get(),bound.get());

        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);
//        root.setFitToHeight(scrollPaneFitToHeight);
        scrollPane.setStyle("-fx-background-color:transparent;");//hide border
//        root.disableProperty().edit(disabledProperty());

        BorderPane rootPane = new BorderPane();
        rootPane.setTop(createNavigation());
        rootPane.setCenter(scrollPane);



        return rootPane;
    }

    private void addLabelContent(GridPane gridPane, int row,String text) {
        String mnemonicLabelText=text;
        if (mnemonicLabelText!=null){
            mnemonicLabelText="_"+mnemonicLabelText;
        }
        Label label = new Label(mnemonicLabelText);
        label.setMnemonicParsing(true);
//        if (icon!=null){
//            label.setGraphic(icon.get());
//        }
        label.setWrapText(true);
        GridPane.setMargin(label, new Insets(0, 9, 0, 0));
        gridPane.add(label, 0, row);
    }

    private void addEditorContent(GridPane gridPane, int row, Node editorWidgetContent) {
        GridPane.setMargin(editorWidgetContent, new Insets(4, 0, 4, 0));
       // TODO
//        label.setLabelFor(editorWidgetContent);
        gridPane.add(editorWidgetContent, 1, row);
    }


    ObservableList<Data> displayedEntities= FXCollections.observableArrayList();
    private Node createNavigation(){
        BreadCrumbBarWidthFixed<Data> breadCrumbBar = new BreadCrumbBarWidthFixed<>();
        breadCrumbBar.setCrumbFactory(param -> {
            BreadCrumbBarSkin.BreadCrumbButton breadCrumbButton = new BreadCrumbBarSkin.BreadCrumbButton("");
            if (param.getValue()!=null){
                //TODO updatable binding
                breadCrumbButton.textProperty().bind(new SimpleStringProperty(param.getValue().getDisplayText()));
            }
            if (displayedEntities.size()-1==displayedEntities.indexOf(param.getValue())){
                breadCrumbButton.setStyle("-fx-font-weight: bold;");
            }
            return breadCrumbButton;
        });
        breadCrumbBar.setOnCrumbAction(event -> {
            edit(event.getSelectedCrumb().getValue());
            breadCrumbBar.setSelectedCrumb(BreadCrumbBar.buildTreeModel(displayedEntities.toArray(new Data[0])));
        });
        displayedEntities.addListener((ListChangeListener<Data>) c -> {
            breadCrumbBar.setSelectedCrumb(BreadCrumbBar.buildTreeModel(displayedEntities.toArray(new Data[0])));
        });



        breadCrumbBar.setAutoNavigationEnabled(false);


        HBox navigation = new HBox(3);
        navigation.getStyleClass().add("navigationhbox");
        navigation.setAlignment(Pos.CENTER_LEFT);
        Button back = new Button("",uniformDesign.createIcon(FontAwesome.Glyph.CARET_LEFT).size(18));
        back.setOnAction(event -> back());
        BooleanBinding backDisabled = Bindings.createBooleanBinding(() -> !previousData().isPresent(),displayedEntities,bound);
        back.disableProperty().bind(backDisabled);
        Button next = new Button("",uniformDesign.createIcon(FontAwesome.Glyph.CARET_RIGHT).size(18));
        BooleanBinding nextDisabled = Bindings.createBooleanBinding(() -> !nextData().isPresent(),displayedEntities,bound);
        next.disableProperty().bind(nextDisabled);
        next.setOnAction(event -> next());
        navigation.getChildren().add(back);
        navigation.getChildren().add(next);


        BorderPane.setMargin(breadCrumbBar, new Insets(3,0,3,0));
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(breadCrumbBar);
        scrollPane.setHvalue(1.0);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("transparent-scroll-pane");
        scrollPane.setPadding(new Insets(3,0,0,0));//workaround scrollpane fittoheight dont work in this cas
        navigation.getChildren().add(scrollPane);
        Slider scroller = new Slider();
        scroller.setMin(0);
        scroller.setMax(1.0);
        scroller.valueProperty().bindBidirectional(scrollPane.hvalueProperty());
        scroller.setPrefWidth(50);
        navigation.getChildren().add(scroller);

        scrollerVisible = Bindings.createBooleanBinding(() -> scrollPane.getBoundsInParent().getWidth()<breadCrumbBar.getWidth(),scrollPane.boundsInParentProperty());
        scroller.visibleProperty().bind(scrollerVisible);


        HBox.setHgrow(scrollPane,Priority.ALWAYS);
        return navigation;
    }

    private Optional<Data> previousData(){
        int index = displayedEntities.indexOf(bound.get())-1;
        if (index>=0){
            return Optional.of(displayedEntities.get(index));
        }
        return Optional.empty();
    }

    private Optional<Data> nextData(){
        int index = displayedEntities.indexOf(bound.get())+1;
        if (index<displayedEntities.size()){
            return Optional.of(displayedEntities.get(index));
        }
        return Optional.empty();
    }

    void back(){
        previousData().ifPresent(this::edit);
    }

    void next(){
        nextData().ifPresent(this::edit);
    }

}
