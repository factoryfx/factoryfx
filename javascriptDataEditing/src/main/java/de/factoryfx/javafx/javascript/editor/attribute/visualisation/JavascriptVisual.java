package de.factoryfx.javafx.javascript.editor.attribute.visualisation;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.javascript.jscomp.CheckLevel;
import com.google.javascript.jscomp.JSError;
import com.google.javascript.jscomp.SourceFile;
import de.factoryfx.javascript.data.attributes.types.Javascript;
import impl.org.controlsfx.skin.AutoCompletePopup;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Skin;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.StyleSpansBuilder;

/**
 *
 * @param <A> api class
 */
public class JavascriptVisual<A> {

    public JavascriptVisual(List<SourceFile> externs) {
        this.externs = externs;
    }

    /**
     *
     * @param <A> api class
     */
    static class RootNode<A> extends StackPane {

        final Consumer<List<JSError>> processErrorsAndWarnings = this::processErrorsAndWarnings;
        final Consumer<NavigableMap<Integer,List<Proposal>>> processProoposals = this::processProposals;
        final Consumer<List<Span>> processHighlighting = this::processHighlighting;

        final ContentAssistant contentAssistant;
        final ErrorsAndWarningsAssistant errorsAndWarningsAssistant;
        final CodeHighlightingAssistant codeHighlightingAssistant = new CodeHighlightingAssistant(new WeakReference<>(processHighlighting));

        final CodeArea codeArea = new CodeArea();
        final ListView<JSError> errorsAndWarnings = new ListView<>();
        final ContentAssistPopup popup = new ContentAssistPopup();
        final ChangeListener<Javascript<A>> onUpdateScript;
        NavigableMap<Integer, List<Proposal>> currentProposals;

        RootNode(List<SourceFile> externs, SimpleObjectProperty<Javascript<A>> boundTo) {
            this.getStylesheets().add(getClass().getResource("jsstyle.css").toExternalForm());
            List<SourceFile> externalSources = new ArrayList<>(externs);
            this.contentAssistant = new ContentAssistant(externalSources, new WeakReference<>(processProoposals));
            this.errorsAndWarningsAssistant = new ErrorsAndWarningsAssistant(externalSources, new WeakReference<>(processErrorsAndWarnings));
            codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
            onUpdateScript = (observable, oldValue, newValue) ->{
                if (newValue != null && !newValue.getCode().equals(codeArea.getText())) {
                    codeArea.replaceText(newValue.getCode());
                }
                updateAssistants(boundTo.get());
            };
            boundTo.addListener(new WeakChangeListener<>(onUpdateScript));
            codeArea.onKeyPressedProperty().set(this::handleKeys);
            codeArea.onKeyReleasedProperty().set(this::handleKeys);
            codeArea.setPopupWindow(popup);
            if (boundTo.get() != null)
                codeArea.insertText(0,boundTo.get().getCode());
            codeArea.textProperty().addListener((a,b,newValue)->{
                if (boundTo.get() == null || boundTo.get().getCode() == null) {
                    boundTo.set(new Javascript<A>(newValue));
                } else if (!boundTo.get().getCode().equals(newValue)) {
                    boundTo.set(boundTo.get().copyWithNewCode(newValue));
                }

            });
            errorsAndWarnings.setCellFactory(lv->{
                ListCell<JSError> cell = new ListCell<>();
                Function<JSError,String> toText = e->{
                    return e.getType().key + ". " + e.description + " at line " +
                            (e.getLineNumber() != -1 ? String.valueOf(e.getLineNumber()) : "(unknown line)") +
                            " : " + (e.getCharno() != -1 ? String.valueOf(e.getCharno()+1) : "(unknown column)");
                };
                cell.itemProperty().addListener((a,b,newValue)->{
                    cell.getStyleClass().removeIf(c-> Arrays.stream(CheckLevel.values()).anyMatch(l->l.name().equals(c)));
                    if (newValue != null) {
                        cell.getStyleClass().add(newValue.getType().level.name());
                        cell.setText(toText.apply(newValue).replaceAll("\\s+"," "));
                    } else {
                        cell.setText("");
                    }
                });
                cell.setOnMouseClicked(e->{
                    jumpToError(cell);
                    Platform.runLater(codeArea::requestFocus);
                });
                cell.setOnKeyTyped(e->{
                    if (e.getCode() == KeyCode.ENTER) {
                        jumpToError(cell);
                    }
                    else if (!e.getCode().isNavigationKey()) {
                        Platform.runLater(() -> {
                            codeArea.requestFocus();
                            codeArea.getOnKeyTyped().handle(e);
                        });
                    }
                });
                return cell;



            });
            errorsAndWarnings.getSelectionModel().selectedItemProperty().addListener((a,oldValue,newValue)->{
            });


            updateAssistants(boundTo.get());
            SplitPane area = new SplitPane();
            area.setOrientation(Orientation.VERTICAL);
            area.setDividerPosition(0,0.8);
            area.getItems().add(codeArea);
            area.getItems().add(errorsAndWarnings);
            SplitPane.setResizableWithParent(codeArea,true);
            getChildren().add(area);
            StackPane.setMargin(area, Insets.EMPTY);
        }

        private void jumpToError(ListCell<JSError> cell) {
            JSError newValue = cell.getItem();
            if (newValue != null) {
                Platform.runLater(()-> {
                    codeArea.positionCaret(codeArea.position(newValue.lineNumber-1, newValue.getCharno()).toOffset());
                });
            }
        }

        private void handleKeys(KeyEvent keyEvent) {
            if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED && keyEvent.isControlDown() && keyEvent.getCode() == KeyCode.SPACE ) {
                if (!popup.isShowing()) {
                    popup.show(this.sceneProperty().get().getWindow());
                    updateProposals();
                }
            }
            if (popup.isShowing()) {
                if ( keyEvent.getEventType() == KeyEvent.KEY_RELEASED &&
                     keyEvent.getCode() != KeyCode.UP &&
                     keyEvent.getCode() != KeyCode.DOWN) {
                    updateProposals();
                }
                if (popup.getSuggestions().isEmpty())
                    popup.hide();
            }
        }

        private void updateProposals() {
            if (currentProposals == null || currentProposals.isEmpty())
                return;
            List<Proposal> proposals = currentProposals.floorEntry(codeArea.getCaretPosition()).getValue();
            String text = codeArea.getText();
            int pos = codeArea.getCaretPosition();
            int pos2 = pos;
            while (pos > 0 && Character.isJavaIdentifierPart(text.charAt(pos-1))) {
                --pos;
            }
            String prefix = text.substring(pos,pos2);
            while (pos2 < text.length() && Character.isJavaIdentifierPart(text.charAt(pos2))) {
                ++pos2;
            }
            ArrayList<Proposal> copy = new ArrayList<>(proposals);
            final int from = pos;
            final int to = pos2;
            Consumer<String> applySuggestion = s -> {
                codeArea.replaceText(from, to, s);
            };
            popup.onSuggestionProperty().setValue(v->{
                applySuggestion.accept(v.getSuggestion());
                popup.hide();
            });
            popup.getSuggestions().clear();

            //TODO list is just workaround, finde better way with closure compiler
            List<String> garbageSuggestions = new ArrayList<>();
            garbageSuggestions.add("hasOwnProperty()");
            garbageSuggestions.add("isPrototypeOf()");
            garbageSuggestions.add("propertyIsEnumerable()");
            garbageSuggestions.add("toJSON()");
            garbageSuggestions.add("toLocaleString()");
            garbageSuggestions.add("toSource()");
            garbageSuggestions.add("toString()");
            garbageSuggestions.add("unwatch()");
            garbageSuggestions.add("valueOf()");
            garbageSuggestions.add("watch()");
            garbageSuggestions.add("constructor");
            List<String> suggestions = copy.stream().map(s -> s.insertString).filter(s -> !garbageSuggestions.contains(s)).collect(Collectors.toList());

            popup.getSuggestions().addAll(suggestions);
            popup.unselect();
            for (String suggestion : suggestions) {
                if (suggestion.startsWith(prefix)) {
                    popup.selectItem(suggestion);
                }
            }
        }

        private void updateAssistants(Javascript<?> javascript) {
            if (javascript == null)
                javascript = new Javascript<>("");
            contentAssistant.accept(javascript);
            errorsAndWarningsAssistant.accept(javascript);
            codeHighlightingAssistant.accept(javascript);
        }

        private void processHighlighting(List<Span> spans) {
            if (!spans.isEmpty()) {
                spans.sort(Comparator.comparingInt(s2 -> s2.from));
                StyleSpansBuilder<Collection<String>> spansBuilder
                        = new StyleSpansBuilder<>();
                int last = 0;
                int textLength = codeArea.getText().length();
                for (Span s : spans) {
                    int newFrom = Math.min(s.from,textLength);
                    if (newFrom > last) {
                        spansBuilder.add(Collections.emptyList(), newFrom - last);
                    }
                    spansBuilder.add(Collections.singleton(s.style()), s.len);
                    last = s.from + s.len;
                }
                if (last < textLength) {
                    spansBuilder.add(Collections.emptyList(), textLength - last);
                }
                try {
                    codeArea.setStyleSpans(0, spansBuilder.create());
                } catch (IllegalArgumentException ignored) {

                }
            }
        }

        private void processErrorsAndWarnings(List<JSError> jsErrors) {
            if (!jsErrors.isEmpty()){
                if (!errorsAndWarnings.getStyleClass().contains("error")){
                    errorsAndWarnings.getStyleClass().add("error");
                }
            } else {
                errorsAndWarnings.getStyleClass().removeAll("error");
            }
            errorsAndWarnings.getItems().setAll(jsErrors);
        }


        private void processProposals(NavigableMap<Integer, List<Proposal>> p) {
            currentProposals = p;
        }
    }

    private final List<SourceFile> externs;

    public Node createContent(SimpleObjectProperty<? extends Javascript<?>> boundTo) {
        return new RootNode(externs,boundTo);
    }

    static final class ContentAssistPopup extends AutoCompletePopup<String> {

        public void selectItem(String item) {
            ((ContentAssistPopupSkin)getSkin()).selectItem(item);
        }

        @Override
        protected Skin<?> createDefaultSkin() {
            return new ContentAssistPopupSkin(this);
        }

        public void unselect() {
            ((ContentAssistPopupSkin)getSkin()).unselect();
        }
    }

}
