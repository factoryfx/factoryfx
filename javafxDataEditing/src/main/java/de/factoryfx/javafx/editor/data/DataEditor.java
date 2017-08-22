package de.factoryfx.javafx.editor.data;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.*;
import de.factoryfx.data.validation.ValidationError;
import de.factoryfx.javafx.editor.attribute.AttributeEditor;
import de.factoryfx.javafx.editor.attribute.AttributeEditorBuilder;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.widget.Widget;
import impl.org.controlsfx.skin.BreadCrumbBarSkin;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.BreadCrumbBar;
import org.controlsfx.glyphfont.FontAwesome;

public class DataEditor implements Widget {
    static final int HISTORY_LIMIT = 20;

    private final AttributeEditorBuilder attributeEditorBuilder;
    private final UniformDesign uniformDesign;
    static class ResettableSimpleObjectProperty extends SimpleObjectProperty<Data> {
        final List<WeakReference<ChangeListener<? super Data>>> changeListeners = new ArrayList<>();
        final List<WeakReference<InvalidationListener>> invalidateListeners = new ArrayList<>();
        @Override
        public void addListener(ChangeListener<? super Data> listener) {
            synchronized (changeListeners) {
                changeListeners.add(new WeakReference<>(listener));
            }
            super.addListener(listener);
        }

        @Override
        public void addListener(InvalidationListener listener) {
            synchronized (invalidateListeners) {
                invalidateListeners.add(new WeakReference<>(listener));
            }
            super.addListener(listener);
        }
        public void reset() {
            synchronized (changeListeners) {
                for (WeakReference<ChangeListener<? super Data>> l : changeListeners) {
                    ChangeListener cl = l.get();
                    if (cl != null)
                        removeListener(cl);
                }
                changeListeners.clear();
            }
            synchronized (invalidateListeners) {
                for (WeakReference<InvalidationListener> l : invalidateListeners) {
                    InvalidationListener il = l.get();
                    if (il != null)
                        removeListener(il);
                }
                invalidateListeners.clear();
            }
        }

    }
    ResettableSimpleObjectProperty bound = new ResettableSimpleObjectProperty();
    private ChangeListener<Data> dataChangeListener;
    private AttributeChangeListener validationListener;
    ObservableList<Data> displayedEntities= FXCollections.observableArrayList();

    public DataEditor(AttributeEditorBuilder attributeEditorBuilder, UniformDesign uniformDesign) {
        this.attributeEditorBuilder = attributeEditorBuilder;
        this.uniformDesign = uniformDesign;
    }

    public ReadOnlyObjectProperty<Data> editData(){
        return bound;
    }

    public void edit(Data newValue) {
        Data current = bound.get();
        bound.set(newValue);
        if (!displayedEntities.contains(newValue)){
            removeUpToCurrent(current);
            displayedEntities.add(newValue);
            if (displayedEntities.size()>HISTORY_LIMIT){
                displayedEntities.remove(0);
            }
        } else {
            int indexOfCurrent = displayedEntities.indexOf(current);
            int indexOfNewValue = displayedEntities.indexOf(newValue);
            if (indexOfNewValue > indexOfCurrent) {
                removeUpToCurrent(current);
                displayedEntities.add(newValue);
                if (displayedEntities.size()>HISTORY_LIMIT){
                    displayedEntities.remove(0);
                }
            }
        }

    }

    public void editExisting(Data newValue) {
        bound.set(newValue);
    }

    public void resetHistory(){
        displayedEntities.setAll(bound.get());
    }
    public void setHistory(List<Data> data){
        displayedEntities.addAll(data);
    }

    public void reset(){
        displayedEntities= FXCollections.observableArrayList();
        bound.reset();
        bound.set(null);
        if (dataChangeListener != null)
            bound.addListener(dataChangeListener);
    }

    private void removeUpToCurrent(Data current) {
        if (current == null)
            return;
        int idx = displayedEntities.indexOf(current);
        if (idx >= 0) {
            displayedEntities.remove(idx+1,displayedEntities.size());
        }
    }


    HashMap<Attribute<?,?>,AttributeEditor<?,?>> createdEditors=new HashMap<>();

    private Node wrapGrid(GridPane gridPane){
        ScrollPane scrollPane = new ScrollPane(gridPane);
        scrollPane.setFitToWidth(true);
//        root.setFitToHeight(scrollPaneFitToHeight);
        scrollPane.setStyle("-fx-background-color:transparent;");//hide border
//        root.disableProperty().edit(disabledProperty());
        return scrollPane;
    }


//    List<ValidationError> attributeValidationError = new ArrayList<>();
//    for (ValidationError validationError: validation.get()){
//        final Attribute<?> attributeItem = validationError.attribute;
//        if (attribute==attributeItem){
//            attributeValidationError.add(validationError);
//        }
//    }
//    validationResult.set(attributeValidationError);

    BiFunction<Node,Data,Node> visCustomizer= (node, data) -> node;

    public void setVisCustomizer(BiFunction<Node,Data,Node> visCustomizer){
        this.visCustomizer=visCustomizer;
    }

    private Node customizeVis(Node defaultVis, Data data){
        return visCustomizer.apply(defaultVis,data);
    }

    private static class ValueAttributeCreator{
        public final String name;
        public final Supplier<ImmutableValueAttribute<?,?>> attributeCreator;

        private ValueAttributeCreator(String name, Supplier<ImmutableValueAttribute<?,?>> attributeCreator) {
            this.name = name;
            this.attributeCreator = attributeCreator;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Node createContent() {
        BorderPane result = new BorderPane();

        if (dataChangeListener!=null) {
            bound.removeListener(dataChangeListener);
        }

        BiConsumer<Node,Data> updateVis= (defaultVis, data) -> {
            result.setCenter(customizeVis(defaultVis,data));
        };

        dataChangeListener = (observable, oldValue, newValue) -> {

            createdEditors.forEach((key, value1) -> value1.unbind());
            createdEditors.clear();
            if (validationListener!=null && oldValue!=null){
                oldValue.internal().visitAttributesFlat((attributeVariableName, attribute) -> {
                    attribute.internal_removeListener(validationListener);
                });
            }
            if (newValue==null) {
                result.setCenter(new Label("empty"));
            } else {

                if (newValue.internal().attributeListGrouped().size()==1){
                    final Node attributeGroupVisual = createAttributeGroupVisual(newValue.internal().attributeListGrouped().get(0).group, () -> newValue.internal().validateFlat(),oldValue);
                    updateVis.accept(attributeGroupVisual,newValue);
                } else {
                    TabPane tabPane = new TabPane();
                    for (AttributeGroup attributeGroup: newValue.internal().attributeListGrouped()) {

                        Tab tab=new Tab(uniformDesign.getText(attributeGroup.title));
                        tab.setClosable(false);
                        tab.setContent(createAttributeGroupVisual(attributeGroup.group,() -> newValue.internal().validateFlat(),oldValue));
                        tabPane.getTabs().add(tab);
                    }
                    updateVis.accept(tabPane,newValue);
                }

                validationListener = (attribute, value) -> {
                    updateValidation(newValue);

//                    //if child data has errors we want to show that as well
//                    List<Data> childrenData = new ArrayList<>();
//                    if (value instanceof Data){
//                        childrenData.add((Data)value);
//                    }
//                    if (value instanceof List){
//                        ((List)value).forEach(data -> {
//                            if (data instanceof Data){
//                                childrenData.add((Data)data);
//                            }
//                        });
//                    }
//                    childrenData.forEach(data -> validationErrors.addAll(data.internal().validateFlat()));


                };
                newValue.internal().visitAttributesFlat((attributeVariableName, attribute) -> {
                    attribute.internal_addListener(new WeakAttributeChangeListener<>(validationListener));
                });
                updateValidation(newValue);

            }


        };
        bound.addListener(dataChangeListener);
        dataChangeListener.changed(bound,bound.get(),bound.get());



        result.setTop(createNavigation());
        return result;
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

    private Node createAttributeGroupVisual(List<Attribute<?,?>> attributeGroup, Supplier<List<ValidationError>> validation, Data oldValue) {
        if (attributeGroup.size()==1){
            final Attribute<?,?> attribute = attributeGroup.get(0);
            AttributeEditor<?,?> attributeEditor = attributeEditorBuilder.getAttributeEditor(attribute, this, validation, oldValue);
//                attributeEditor.setReadOnly();
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
//        grid.setHgap(3);
//             grid.setVgap(3);
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

                AttributeEditor<?,?> attributeEditor = attributeEditorBuilder.getAttributeEditor(attribute,this,validation,oldValue);
                int rowFinal=row;
//                    attributeEditor.setReadOnly();
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

    private Label addLabelContent(GridPane gridPane, int row,String text) {
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

    private Node createNavigation(){
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
            if (bound.get()==param.getValue()){
                breadCrumbButton.setStyle("-fx-font-weight: bold;");
            }
            return breadCrumbButton;
        });
        breadCrumbBar.setOnCrumbAction(event -> {
            edit(event.getSelectedCrumb().getValue());
//            breadCrumbBar.setSelectedCrumb(BreadCrumbBar.buildTreeModel(displayedEntities.toArray(new Data[0])));
        });

        Runnable updateBreadCrumbBar= () -> {
            List<Data> newhistory = new ArrayList<>();
            for (Data data: displayedEntities){
                newhistory.add(data);
                if (data==bound.get()){
                    break;
                }
            }

            breadCrumbBar.setSelectedCrumb(BreadCrumbBar.buildTreeModel(newhistory.toArray(new Data[0])));
            breadCrumbBar.layout();
        };
        displayedEntities.addListener((ListChangeListener<Data>) c -> {
            updateBreadCrumbBar.run();
        });
        InvalidationListener breadCrumbInvalidationListener = observable -> {
            updateBreadCrumbBar.run();
        };
        bound.addListener(breadCrumbInvalidationListener);



        HBox navigation = new HBox(3);
        navigation.getStyleClass().add("navigationhbox");
        navigation.setAlignment(Pos.TOP_LEFT);
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


        navigation.getChildren().add(scrollPaneBreadCrumbBar);

        navigation.setPadding(new Insets(3,3,0,3));//workaround scrollpane fittoheight and  .setAlignment(Pos.TOP_LEFT); dont work in this cas

        HBox.setHgrow(scrollPaneBreadCrumbBar,Priority.ALWAYS);
        return navigation;
    }

    private void showFactoryHistory(String id) {
//        new DiffDialog().
    }

    private Optional<Data> previousData(){
        int index = displayedEntities.indexOf(bound.get())-1;
        if (index>=0){
            return Optional.ofNullable(displayedEntities.get(index));
        }
        return Optional.empty();
    }

    private Optional<Data> nextData(){
        int index = displayedEntities.indexOf(bound.get())+1;
        if (index<displayedEntities.size()){
            return Optional.ofNullable(displayedEntities.get(index));
        }
        return Optional.empty();
    }

    void back(){
        previousData().ifPresent(this::editExisting);
    }

    void next(){
        nextData().ifPresent(this::editExisting);
    }

}
