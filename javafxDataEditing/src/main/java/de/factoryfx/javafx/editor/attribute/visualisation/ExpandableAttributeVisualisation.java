package de.factoryfx.javafx.editor.attribute.visualisation;

import java.util.function.Function;

import de.factoryfx.javafx.editor.attribute.AttributeEditorVisualisation;
import de.factoryfx.javafx.util.UniformDesign;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.controlsfx.glyphfont.FontAwesome;

public abstract class ExpandableAttributeVisualisation<T> implements AttributeEditorVisualisation<T> {

    private final UniformDesign uniformDesign;
    private final SimpleBooleanProperty expanded = new SimpleBooleanProperty(false);

    public ExpandableAttributeVisualisation(UniformDesign uniformDesign) {
        this.uniformDesign = uniformDesign;
    }

    @Override
    public Node createContent(SimpleObjectProperty<T> boundTo) {
        VBox detailView = createDetailView(boundTo);
        return createExpandableEditorWrapper(boundTo,detailView, getSummaryIcon(), t -> getSummaryText(boundTo));
    }
    protected abstract FontAwesome.Glyph getSummaryIcon();

    protected abstract String getSummaryText(SimpleObjectProperty<T> boundTo);

    protected abstract VBox createDetailView(SimpleObjectProperty<T> boundTo);

    public <T> VBox createExpandableEditorWrapper(SimpleObjectProperty<T> boundTo, VBox detailView, FontAwesome.Glyph icon, Function<T,String> summaryTextProvider) {
        VBox root = new VBox();

        VBox.setVgrow(detailView, Priority.ALWAYS);
        detailView.setBorder(new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
        detailView.setPadding(new Insets(3));
        VBox.setMargin(detailView,new Insets(3,0,0,0));

        Label iconLabel = new Label();
        uniformDesign.addIcon(iconLabel,icon);

        ToggleButton expandButton=new ToggleButton();
        expandButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue!=null && newValue){
                root.getChildren().add(detailView);
            } else {
                root.getChildren().remove(detailView);
            }
        });
        uniformDesign.addIcon(expandButton, FontAwesome.Glyph.ANGLE_DOWN);

        HBox summary=new HBox(3);
        summary.setAlignment(Pos.CENTER_LEFT);
        Label label = new Label();
        InvalidationListener listener = observable -> {
            if (boundTo.get() == null) {
                label.setText("<empty>");
            } else {
                label.setText(summaryTextProvider.apply(boundTo.get()));
            }
        };
        boundTo.addListener(listener);
        listener.invalidated(null);
        summary.getChildren().addAll(expandButton,iconLabel,label);
        root.getChildren().addAll(summary);

        expandButton.selectedProperty().bindBidirectional(expanded);
        return root;
    }

    @Override
    public void expand() {
        expanded.set(true);
    }
}
