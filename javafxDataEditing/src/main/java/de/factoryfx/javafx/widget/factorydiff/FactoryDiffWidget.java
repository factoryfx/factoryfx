package de.factoryfx.javafx.widget.factorydiff;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import de.factoryfx.data.attribute.AttributeJsonWrapper;
import de.factoryfx.data.merge.AttributeDiffInfo;
import de.factoryfx.data.merge.MergeDiffInfo;
import de.factoryfx.data.util.LanguageText;
import de.factoryfx.javafx.editor.attribute.AttributeEditor;
import de.factoryfx.javafx.editor.attribute.AttributeEditorBuilder;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.widget.Widget;
import de.factoryfx.javafx.widget.table.TableControlWidget;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
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
import org.controlsfx.glyphfont.FontAwesome;
import org.fxmisc.richtext.StyleClassedTextArea;

public class FactoryDiffWidget implements Widget {
    private UniformDesign uniformDesign;
    private LanguageText columnFactory=new LanguageText().en("data").de("Objekt");
    private LanguageText columnField=new LanguageText().en("field").de("Feld");
    private LanguageText columnPrevious=new LanguageText().en("previous").de("Alt");
    private LanguageText columnNew=new LanguageText().en("new").de("Neu");
    private LanguageText titleDiff=new LanguageText().en("difference").de("Unterschied");
    private LanguageText titlePrevious=new LanguageText().en("previous value ").de("Alter Wert");
    private LanguageText titleNew=new LanguageText().en("new value").de("Neuer Wert");
    private LanguageText noChangesFound=new LanguageText().en("No changes found").de("keine Änderungen gefunden");

    private final AttributeEditorBuilder attributeEditorBuilder;

    public FactoryDiffWidget(UniformDesign uniformDesign, AttributeEditorBuilder attributeEditorBuilder){
        this.uniformDesign=uniformDesign;
        this.attributeEditorBuilder = attributeEditorBuilder;
    }

    private List<AttributeDiffInfoExtended> diff=new ArrayList<>();
    private Consumer<List<AttributeDiffInfoExtended>> diffListUpdater;

    @Override
    public Node createContent() {
        BorderPane previousValueDisplay = new BorderPane();
        previousValueDisplay.setOpacity(0.6);
        BorderPane newValueDisplay = new BorderPane();
        newValueDisplay.setOpacity(0.6);
        StyleClassedTextArea diffDisplay = new StyleClassedTextArea();
        diffDisplay.getStyleClass().add("diffTextField");
        diffDisplay.setEditable(false);

        TableView<AttributeDiffInfoExtended> diffTableView = createDiffTableViewTable();
        final ObservableList<AttributeDiffInfoExtended> diffList = FXCollections.observableArrayList();
        diffTableView.setItems(diffList);

        BorderPane borderPane = new BorderPane();

        SplitPane verticalSplitPane = new SplitPane();
        verticalSplitPane.setOrientation(Orientation.VERTICAL);
        verticalSplitPane.getItems().add(diffTableView);

        VBox diffBox = new VBox(3);
        VBox.setVgrow(diffDisplay, Priority.ALWAYS);
        diffBox.getChildren().add(diffDisplay);
        HBox diffJumpButtons = new HBox(3);
        diffJumpButtons.setAlignment(Pos.CENTER);
        diffJumpButtons.setPadding(new Insets(3));
        Button up = new Button();
        uniformDesign.addIcon(up, FontAwesome.Glyph.CHEVRON_CIRCLE_UP);
        diffJumpButtons.getChildren().add(up);
        Button down = new Button();
        uniformDesign.addIcon(down, FontAwesome.Glyph.CHEVRON_CIRCLE_DOWN);
        diffJumpButtons.getChildren().add(down);
        diffBox.getChildren().add(diffJumpButtons);

        SplitPane diffValuesPane = new SplitPane();
        diffValuesPane.getItems().add(addTitle(previousValueDisplay, uniformDesign.getText(titlePrevious)));
        diffValuesPane.getItems().add(addTitle(diffBox, uniformDesign.getText(titleDiff)));
        diffValuesPane.getItems().add(addTitle(newValueDisplay, uniformDesign.getText(titleNew)));
        diffValuesPane.setDividerPositions(0.333, 0.6666);
        verticalSplitPane.getItems().add(diffValuesPane);

        diffTableView.getSelectionModel().selectedItemProperty().addListener(observable -> {
            AttributeDiffInfo diffItem = diffTableView.getSelectionModel().getSelectedItem().attributeDiffInfo;
            if (diffItem != null) {
                List<String> previousLines = convertToList(diffItem.previousValueDisplayText.getDisplayText());
                List<String> newLines = convertToList(diffItem.newValueValueDisplayText.map(AttributeJsonWrapper::getDisplayText).orElse("removed"));
                Patch<String> patch = DiffUtils.diff(previousLines, newLines);

                List<StyleClassArea> styleChanges = new ArrayList<>();

                int previousOriginalPosition = 0;
                StringBuilder diffString = new StringBuilder();
                //String  lastOriginalStringDelta="";

                final List<Integer> diffPositions = new ArrayList<>();
                for (Delta<String> delta : patch.getDeltas()) {
                    String originalStringDelta = delta.getOriginal().getLines().stream().collect(Collectors.joining());
                    String revisitedStringDelta = delta.getRevised().getLines().stream().collect(Collectors.joining());
                    final String unchanged = previousLines.subList(previousOriginalPosition, delta.getOriginal().getPosition()).stream().collect(Collectors.joining());

                    diffString.append(unchanged);
                    diffPositions.add(diffString.toString().length());
                    styleChanges.add(new StyleClassArea(diffString.length() - unchanged.length(), diffString.length(), "diffUnchanged"));
                    diffString.append(originalStringDelta);
                    styleChanges.add(new StyleClassArea(diffString.length() - originalStringDelta.length(), diffString.length(), "diffOld"));
                    diffString.append(revisitedStringDelta);
                    styleChanges.add(new StyleClassArea(diffString.length() - revisitedStringDelta.length(), diffString.length(), "diffNew"));

                    previousOriginalPosition = delta.getOriginal().getPosition() + delta.getOriginal().size();
                    //lastOriginalStringDelta=originalStringDelta;
                }
                if (previousOriginalPosition < previousLines.size()) {
                    final String unchanged = previousLines.subList(previousOriginalPosition, previousLines.size()).stream().collect(Collectors.joining());
                    diffString.append(unchanged);
                    styleChanges.add(new StyleClassArea(diffString.length() - unchanged.length(), diffString.length(), "diffUnchanged"));
                }

                diffDisplay.replaceText(diffString.toString());
                for (StyleClassArea styleClassArea : styleChanges) {
                    if (styleClassArea.end <= diffString.toString().length()) {
                        diffDisplay.setStyleClass(styleClassArea.start, styleClassArea.end, styleClassArea.cssclass);
                    }
                }

                if (!patch.getDeltas().isEmpty()) {
                    int firstChange = patch.getDeltas().get(0).getOriginal().getPosition();
                    diffDisplay.positionCaret(firstChange);
                    diffDisplay.selectRange(firstChange, firstChange);
                }

                diffPosition = 0;
                if (!diffPositions.isEmpty()) {
                    diffPosition = diffPositions.get(0);
                }
                up.setOnAction(event -> {
                    for (Integer pos : diffPositions) {
                        if (pos < diffPosition) {
                            diffDisplay.selectRange(pos, pos);
                            diffPosition = pos;
                            break;
                        }
                    }
                });
                down.setOnAction(event -> {
                    for (Integer pos : diffPositions) {
                        if (pos > diffPosition) {
                            diffDisplay.selectRange(pos, pos);
                            diffPosition = pos;
                            break;
                        }
                    }
                });

                final Optional<AttributeEditor<?>> previousAttributeEditor = attributeEditorBuilder.getAttributeEditor(diffItem.previousValueDisplayText.createAttribute(), null, null, null);
                previousAttributeEditor.get().expand();
                final Node previousAttributeEditorContent = previousAttributeEditor.get().createContent();
                previousAttributeEditorContent.setDisable(true);
                previousValueDisplay.setCenter(previousAttributeEditorContent);

                if (diffItem.newValueValueDisplayText.isPresent()) {
                    final Optional<AttributeEditor<?>> newAttributeEditor = attributeEditorBuilder.getAttributeEditor(diffItem.newValueValueDisplayText.get().createAttribute(), null, null, null);
                    newAttributeEditor.get().expand();
                    final Node content = newAttributeEditor.get().createContent();
                    content.setDisable(true);
                    newValueDisplay.setCenter(content);
                } else {
                    newValueDisplay.setCenter(null);
                }

//                previousValueDisplay.replaceText(diffItem.previousValueDisplayText);
//                newValueDisplay.replaceText(diffItem.newValueValueDisplayText);
            }
        });

        diffListUpdater = attributeDiffInfoExtendeds -> {
            diffList.setAll(attributeDiffInfoExtendeds);
            diffTableView.getSelectionModel().selectFirst();
        };
        diffListUpdater.accept(diff);
        borderPane.setCenter(verticalSplitPane);
        return borderPane;
    }

    int diffPosition;

    private TableView<AttributeDiffInfoExtended> createDiffTableViewTable(){
        TableView<AttributeDiffInfoExtended> tableView = new TableView<>();
        tableView.setPlaceholder(new Label(uniformDesign.getText(noChangesFound)));
        {
            TableColumn<AttributeDiffInfoExtended, String> factoryColumn = new TableColumn<>(uniformDesign.getText(columnFactory));
            factoryColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().attributeDiffInfo.parentDisplayText));
            tableView.getColumns().add(factoryColumn);

            TableColumn<AttributeDiffInfoExtended, String> fieldColumn = new TableColumn<>(uniformDesign.getText(columnField));
            fieldColumn.setCellValueFactory(param -> new SimpleStringProperty(uniformDesign.getText(param.getValue().attributeDiffInfo.previousValueDisplayText.createAttribute().metadata.labelText)));
            tableView.getColumns().add(fieldColumn);

            TableColumn<AttributeDiffInfoExtended, String> previousColumn = new TableColumn<>(uniformDesign.getText(columnPrevious));
            previousColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().attributeDiffInfo.previousValueDisplayText.getDisplayText()));
            tableView.getColumns().add(previousColumn);

            TableColumn<AttributeDiffInfoExtended, String> newColumn = new TableColumn<>(uniformDesign.getText(columnNew));
            newColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().attributeDiffInfo.newValueValueDisplayText.map(AttributeJsonWrapper::getDisplayText).orElse("removed")));
            tableView.getColumns().add(newColumn);

        }




        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setRowFactory((param) -> new TableRow<AttributeDiffInfoExtended>() {
            @Override
            protected void updateItem(AttributeDiffInfoExtended mergeResultEntry, boolean empty) {
                super.updateItem(mergeResultEntry, empty);
                if (mergeResultEntry != null && mergeResultEntry.conflict) {
                    getStyleClass().add("conflictRow");
                } else {
                    getStyleClass().remove("conflictRow");
                }
                if (mergeResultEntry != null && mergeResultEntry.violation) {
                    getStyleClass().add("permissionViolationRow");
                } else {
                    getStyleClass().remove("permissionViolationRow");
                }
            }
        });

        tableView.fixedCellSizeProperty().set(24);

        new TableControlWidget<>(tableView,uniformDesign).hide();
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
        Pattern wordAndWhitespace = Pattern.compile("\\s*[^\\s]+\\s*");
        Pattern splitTrailingWhitespaces = Pattern.compile("(.+?)([\\s]+)",Pattern.DOTALL);
        Matcher wordMatcher = wordAndWhitespace.matcher(value);
        ArrayList<String> result = new ArrayList<>();
        while (wordMatcher.find()) {
            Matcher whitespaceMatcher = splitTrailingWhitespaces.matcher(wordMatcher.group());
            if (whitespaceMatcher.matches()) {
                result.add(whitespaceMatcher.group(1));
                result.add(whitespaceMatcher.group(2));
            } else {
                result.add(wordMatcher.group());
            }
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

    public void updateMergeDiff(List<AttributeDiffInfo> diffList) {
        this.diff=(diffList.stream().map(i->new AttributeDiffInfoExtended(true,false,false,i)).collect(Collectors.toList()));
        if (diffListUpdater!=null){
            diffListUpdater.accept(diff);
        }
    }

    public void updateMergeDiff(MergeDiffInfo mergeDiff) {
        diff = new ArrayList<>();
        diff.addAll(mergeDiff.mergeInfos.stream().map(info->new AttributeDiffInfoExtended(true,false,false, info)).collect(Collectors.toList()));
        diff.addAll(mergeDiff.conflictInfos.stream().map(info->new AttributeDiffInfoExtended(false,true,false, info)).collect(Collectors.toList()));
        diff.addAll(mergeDiff.permissionViolations.stream().map(info->new AttributeDiffInfoExtended(false,false,true, info)).collect(Collectors.toList()));
        if (diffListUpdater!=null){
            diffListUpdater.accept(diff);
        }
    }

}
