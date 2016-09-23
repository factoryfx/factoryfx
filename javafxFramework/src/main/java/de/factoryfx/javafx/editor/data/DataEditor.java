package de.factoryfx.javafx.editor.data;

import java.util.Locale;
import java.util.Optional;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.javafx.editor.attribute.AttributeEditor;
import de.factoryfx.javafx.editor.attribute.AttributeEditorFactory;
import de.factoryfx.javafx.widget.Widget;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

public class DataEditor implements Widget {

    final AttributeEditorFactory attributeEditorFactory;
    SimpleObjectProperty<Data> bound = new SimpleObjectProperty<>();

    public DataEditor(AttributeEditorFactory attributeEditorFactory) {
        this.attributeEditorFactory = attributeEditorFactory;
    }

    public void bind(Data newValue) {
        bound.set(newValue);
    }

    @Override
    public Node createContent() {
        ScrollPane root;

        GridPane grid = new GridPane();
//        grid.setHgap(3);
//        grid.setVgap(6);
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

            if (newValue!=null){
                int row = 0;
                for (Attribute<?> attribute: newValue.attrributeList()) {
                    //TOO locale configurable , unformdesign?
                    addLabelContent(grid, row,attribute.metadata.labelText.getPreferred(Locale.ENGLISH));

                    attributeEditorFactory.getAttributeEditor(attribute);
                    Optional<AttributeEditor<?>> attributeEditor = attributeEditorFactory.getAttributeEditor(attribute);
                    int rowFinal=row;
                    attributeEditor.ifPresent(attributeEditor1 -> addEditorContent(grid, rowFinal, attributeEditor1.createContent()));
                    if (attributeEditor.isPresent()){
                        addEditorContent(grid, rowFinal, attributeEditor.get().createContent());
                        attributeEditor.get().bindAnyway(attribute);
                    } else {
                        addEditorContent(grid, rowFinal, new Label("unsupported attribute"));
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





        root = new ScrollPane(grid);
        root.setFitToWidth(true);
//        root.setFitToHeight(scrollPaneFitToHeight);
        root.setStyle("-fx-background-color:transparent;");//hide border
//        root.disableProperty().bind(disabledProperty());
        return root;
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
}
