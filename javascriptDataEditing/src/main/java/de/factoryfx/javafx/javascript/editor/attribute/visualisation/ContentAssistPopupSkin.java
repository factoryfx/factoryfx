package de.factoryfx.javafx.javascript.editor.attribute.visualisation;

//import impl.org.controlsfx.skin.AutoCompletePopup;
import javafx.beans.binding.Bindings;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.Skin;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.MouseButton;
import org.controlsfx.control.textfield.AutoCompletionBinding;


public class ContentAssistPopupSkin /*implements Skin<AutoCompletePopup<String>>*/ {

//
//
//    private final AutoCompletePopup<String> control;
//    private final ListView<String> suggestionList;
//    final static int LIST_CELL_HEIGHT = 24;
//
//    public ContentAssistPopupSkin(AutoCompletePopup<String> control){
//        this.control = control;
//        suggestionList = new ListView<>(control.getSuggestions());
//
//        suggestionList.getStyleClass().add(AutoCompletePopup.DEFAULT_STYLE_CLASS);
//
//        suggestionList.getStylesheets().add(AutoCompletionBinding.class
//                .getResource("autocompletion.css").toExternalForm()); //$NON-NLS-1$
//
//        suggestionList.prefHeightProperty().bind(
//                Bindings.min(control.visibleRowCountProperty(), Bindings.size(suggestionList.getItems()))
//                .multiply(LIST_CELL_HEIGHT).add(18));
//        suggestionList.setCellFactory(TextFieldListCell.forListView(control.getConverter()));
//
//        suggestionList.prefWidthProperty().bind(control.prefWidthProperty());
//        suggestionList.maxWidthProperty().bind(control.maxWidthProperty());
//        suggestionList.minWidthProperty().bind(control.minWidthProperty());
//        registerEventListener();
//    }
//
//    private void registerEventListener(){
//        suggestionList.setOnMouseClicked(me -> {
//            if (me.getButton() == MouseButton.PRIMARY){
//                onSuggestionChoosen(suggestionList.getSelectionModel().getSelectedItem());
//            }
//        });
//
//
//        suggestionList.setOnKeyPressed(ke -> {
//            switch (ke.getCode()) {
//                case ENTER:
//                    onSuggestionChoosen(suggestionList.getSelectionModel().getSelectedItem());
//                    break;
//                case ESCAPE:
//                    if (control.isHideOnEscape()) {
//                        control.hide();
//                    }
//                    break;
//                default:
//                    break;
//            }
//        });
//    }
//
//    private void onSuggestionChoosen(String suggestion){
//        if(suggestion != null) {
//            Event.fireEvent(control, new AutoCompletePopup.SuggestionEvent<>(suggestion));
//        }
//    }
//
//
//    @Override
//    public Node getNode() {
//        return suggestionList;
//    }
//
//    @Override
//    public AutoCompletePopup<String> getSkinnable() {
//        return control;
//    }
//
//    @Override
//    public void dispose() {
//    }
//
//    public void selectItem(String item) {
//        suggestionList.scrollTo(item);
//        MultipleSelectionModel<String> selectionModel = suggestionList.getSelectionModel();
//        selectionModel.clearSelection();
//        selectionModel.select(item);
//    }
//
//    public void unselect() {
//        suggestionList.getSelectionModel().clearSelection();
//    }

}
