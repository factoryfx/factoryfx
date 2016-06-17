package de.factoryfx.richclient.framework.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.factoryfx.factory.merge.MergeDiff;
import de.factoryfx.factory.merge.MergeResultEntry;
import de.factoryfx.richclient.framework.Constants;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.controlsfx.glyphfont.FontAwesome;
import org.fxmisc.richtext.StyleClassedTextArea;

public class FactoryDiffWidget implements Widget {
    private final SimpleObjectProperty<MergeDiff> mergeResultModel= new SimpleObjectProperty<>();

    public static class MergeResultEntryWrapper{
        public final MergeResultEntry<?> entry;
        public final boolean conflict;

        public MergeResultEntryWrapper(MergeResultEntry<?> entry, boolean conflict) {
            this.entry = entry;
            this.conflict = conflict;
        }
    }

    @Override
    public Node createContent() {
        StyleClassedTextArea previousValueDisplay = new StyleClassedTextArea();
        previousValueDisplay.setEditable(false);
        previousValueDisplay.setOpacity(0.6);
        StyleClassedTextArea newValueDisplay = new StyleClassedTextArea ();
        newValueDisplay.setEditable(false);
        newValueDisplay.setOpacity(0.6);
        StyleClassedTextArea diffDisplay = new StyleClassedTextArea ();
        diffDisplay.getStyleClass().add("diffTextField");
        diffDisplay.setEditable(false);

        TableView<MergeResultEntryWrapper> diffTableView = createDiffTableViewTable();
        final ObservableList<MergeResultEntryWrapper> diffList= FXCollections.observableArrayList();
        diffTableView.setItems(diffList);


        BorderPane borderPane = new BorderPane();

        SplitPane verticalSplitPane = new SplitPane();
        verticalSplitPane.setOrientation(Orientation.VERTICAL);
        verticalSplitPane.getItems().add(addTitle(diffTableView,"Changes"));

        VBox diffBox = new VBox(3);
        VBox.setVgrow(diffDisplay,Priority.ALWAYS);
        diffBox.getChildren().add(diffDisplay);
        HBox diffJumpButtons = new HBox(3);
        diffJumpButtons.setAlignment(Pos.CENTER);
        diffJumpButtons.setPadding(new Insets(3));
        Button up = new Button("", Constants.FONT_AWESOME.create(FontAwesome.Glyph.CHEVRON_CIRCLE_UP));
        diffJumpButtons.getChildren().add(up);
        Button down = new Button("", Constants.FONT_AWESOME.create(FontAwesome.Glyph.CHEVRON_CIRCLE_DOWN));
        diffJumpButtons.getChildren().add(down);
        diffBox.getChildren().add(diffJumpButtons);

        SplitPane diffValuesPane = new SplitPane();
        diffValuesPane.getItems().add(addTitle(previousValueDisplay,"Previous value"));
        diffValuesPane.getItems().add(addTitle(diffBox,"Difference"));
        diffValuesPane.getItems().add(addTitle(newValueDisplay,"New value"));
        diffValuesPane.setDividerPositions(0.333,0.6666);
        verticalSplitPane.getItems().add(diffValuesPane);



        diffTableView.getSelectionModel().selectedItemProperty().addListener(observable -> {
            MergeResultEntry<?> diffItem=diffTableView.getSelectionModel().getSelectedItem().entry;
            if (diffItem!=null){
                Patch<String> patch = DiffUtils.diff(
                        convertToList(diffItem.mergeResultEntryInfo.previousValueDisplayText),
                        convertToList(diffItem.mergeResultEntryInfo.newValueValueDisplayText)
                );
                String originalText=diffItem.mergeResultEntryInfo.previousValueDisplayText;

                List<StyleClassArea> styleChanges= new ArrayList<>();

                int previousOriginalPosition=0;
                StringBuilder diffString = new StringBuilder();
                String  lastOriginalStringDelta="";

                final List<Integer> diffPositions=new ArrayList<>();
                for (Delta<String> delta: patch.getDeltas()) {
                    String originalStringDelta = delta.getOriginal().getLines().stream().collect(Collectors.joining());
                    String revisitedStringDelta = delta.getRevised().getLines().stream().collect(Collectors.joining());
                    final String unchanged=originalText.substring(previousOriginalPosition,delta.getOriginal().getPosition()).replace(lastOriginalStringDelta,"");

                    diffString.append(unchanged);
                    diffPositions.add(diffString.toString().length());
                    styleChanges.add(new StyleClassArea(diffString.length()-unchanged.length(), diffString.length(), "diffUnchanged"));
                    diffString.append(originalStringDelta);
                    styleChanges.add(new StyleClassArea(diffString.length()-originalStringDelta.length(), diffString.length(), "diffOld"));
                    diffString.append(revisitedStringDelta);
                    styleChanges.add(new StyleClassArea(diffString.length()-revisitedStringDelta.length(), diffString.length(), "diffNew"));

                    previousOriginalPosition=delta.getOriginal().getPosition();
                    lastOriginalStringDelta=originalStringDelta;
                }
                final String unchanged = originalText.substring(previousOriginalPosition, originalText.length()).replace(lastOriginalStringDelta,"");
                diffString.append(unchanged);
                styleChanges.add(new StyleClassArea(diffString.length()-unchanged.length(), diffString.length(), "diffUnchanged"));

                diffDisplay.replaceText(diffString.toString());
                for (StyleClassArea styleClassArea: styleChanges){
                    diffDisplay.setStyleClass(styleClassArea.start,styleClassArea.end,styleClassArea.cssclass);
                }

                if (!patch.getDeltas().isEmpty()){
                    int firstChange=patch.getDeltas().get(0).getOriginal().getPosition();
                    diffDisplay.positionCaret(firstChange);
                    diffDisplay.selectRange(firstChange, firstChange);
                }

                diffPosition=0;
                if (!diffPositions.isEmpty()){
                    diffPosition=diffPositions.get(0);
                }
                up.setOnAction(event -> {
                    for (Integer pos: diffPositions){
                        if (pos<diffPosition){
                            diffDisplay.selectRange(pos,pos);
                            diffPosition=pos;
                            break;
                        }
                    }
                });
                down.setOnAction(event -> {
                    for (Integer pos: diffPositions){
                        if (pos>diffPosition){
                            diffDisplay.selectRange(pos,pos);
                            diffPosition=pos;
                            break;
                        }
                    }
                });

                previousValueDisplay.replaceText(diffItem.mergeResultEntryInfo.previousValueDisplayText);
                newValueDisplay.replaceText(diffItem.mergeResultEntryInfo.newValueValueDisplayText);
            }
        });


        InvalidationListener invalidationListener = observable -> {
            MergeDiff mergeDiff = mergeResultModel.get();
            if (mergeDiff != null) {
                diffList.clear();
                diffList.addAll(mergeDiff.getMergeInfos().stream().map(mergeResultEntry -> new MergeResultEntryWrapper(mergeResultEntry,false)).collect(Collectors.toList()));
                diffList.addAll(mergeDiff.getConflictInfos().stream().map(mergeResultEntry -> new MergeResultEntryWrapper(mergeResultEntry,true)).collect(Collectors.toList()));
                diffTableView.getSelectionModel().selectFirst();

            }
        };
        mergeResultModel.addListener(invalidationListener);
        invalidationListener.invalidated(mergeResultModel);


        borderPane.setCenter(verticalSplitPane);
        return borderPane;
    }

    int diffPosition;

    private TableView<MergeResultEntryWrapper> createDiffTableViewTable(){
        TableView<MergeResultEntryWrapper> tableView = new TableView<>();
        tableView.setPlaceholder(new Label("No changes found"));
        {
//            TableColumn<MergeResultEntryWrapper, String> pathColumn = new TableColumn<>("Path");
//            pathColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().entry.getPathDisplayText()));
//            tableView.getColumns().add(pathColumn);

            TableColumn<MergeResultEntryWrapper, String> entityColumn = new TableColumn<>("Data Item");
//            entityColumn.getStyleClass().add("dividerColumn");
            entityColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().entry.parent.getDisplayText()));
            tableView.getColumns().add(entityColumn);

//            TableColumn<MergeResultEntryWrapper, String> filedColumn = new TableColumn<>("Field");
//            filedColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().entry.getPreviousField()));
//            tableView.getColumns().add(filedColumn);


//            TableColumn<MergeResultEntryWrapper, String> changeColumn = new TableColumn<>("change type");
            /*
            valueColumn.setCellFactory(param -> {
                TableCell<MergeResultEntryWrapper, String> mergeResultEntryWrapperStringTableCell = new TableCell<>();
                mergeResultEntryWrapperStringTableCell.getStyleClass().add("dividerCellLeft");
                return mergeResultEntryWrapperStringTableCell;
            });
            */

            TableColumn<MergeResultEntryWrapper, String> oldValueColumn = new TableColumn<>("Previous value");
            oldValueColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().entry.mergeResultEntryInfo.previousValueDisplayText));
            tableView.getColumns().add(oldValueColumn);

            TableColumn<MergeResultEntryWrapper, String> valueColumn = new TableColumn<>("New value");
            valueColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().entry.mergeResultEntryInfo.newValueValueDisplayText));
            tableView.getColumns().add(valueColumn);
        }

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setRowFactory(new Callback<TableView<MergeResultEntryWrapper>, TableRow<MergeResultEntryWrapper>>() {
            @Override
            public TableRow<MergeResultEntryWrapper> call(TableView<MergeResultEntryWrapper> param) {
                return new TableRow<MergeResultEntryWrapper>(){
                    @Override
                    protected void updateItem(MergeResultEntryWrapper mergeResultEntryWrapper, boolean empty){
                        super.updateItem(mergeResultEntryWrapper, empty);
                        if (mergeResultEntryWrapper!=null && mergeResultEntryWrapper.conflict) {
                            getStyleClass().add("conflictRow");
                        } else {
                            getStyleClass().remove("conflictRow");
                        }
                    }
                };
            }
        });

        tableView.fixedCellSizeProperty().set(24);

        return tableView;
    }

    private static class StyleClassArea{
        public String cssclass;
        public int start;
        public int end;

        public StyleClassArea(int start, int end, String cssclass) {
            this.cssclass = cssclass;
            this.start = start;
            this.end = end;
        }
    }

    private List<String> convertToList(String value){
        //Arrays.asList(value.split(""))  slower
        ArrayList<String> result = new ArrayList<>(value.length());
        for (int i = 0;i < value.length(); i++){
            result.add(String.valueOf(value.charAt(i)));
        }
        return result;
    }

    private Node addTitle(Node node, String title){
        VBox vBox = new VBox();
        vBox.setSpacing(3);
        Label label = new Label(title);
        label.getStyleClass().add("titleLabel");
        vBox.getChildren().add(label);
        VBox.setVgrow(node, Priority.ALWAYS);
        vBox.getChildren().add(node);
        return vBox;
    }



    public void updateMergeResult(MergeDiff mergeResult) {
        mergeResultModel.set(mergeResult);
    }

}
