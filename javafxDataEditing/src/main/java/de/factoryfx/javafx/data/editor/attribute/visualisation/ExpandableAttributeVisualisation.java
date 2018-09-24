package de.factoryfx.javafx.data.editor.attribute.visualisation;

import java.util.function.Function;

import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.javafx.data.editor.attribute.AttributeVisualisation;
import de.factoryfx.javafx.data.editor.attribute.ValueAttributeVisualisation;
import de.factoryfx.javafx.data.util.UniformDesign;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.controlsfx.glyphfont.FontAwesome;

public class ExpandableAttributeVisualisation<T, A extends Attribute<T,A>> implements AttributeVisualisation {

    private final UniformDesign uniformDesign;
    private final SimpleBooleanProperty expanded = new SimpleBooleanProperty(false);
    private final ValueAttributeVisualisation<T, A> attributeVisualisation;
    private final SimpleStringProperty title = new SimpleStringProperty();
    private final Function<T,String> titleTextProvider;
    private final FontAwesome.Glyph  icon;

    public ExpandableAttributeVisualisation(ValueAttributeVisualisation<T, A> attributeEditorVisualisation, UniformDesign uniformDesign, Function<T,String> titleTextProvider, FontAwesome.Glyph icon) {
        this(attributeEditorVisualisation,uniformDesign,titleTextProvider,icon,false);
    }

    public ExpandableAttributeVisualisation(ValueAttributeVisualisation<T, A> attributeVisualisation, UniformDesign uniformDesign, Function<T,String> titleTextProvider, FontAwesome.Glyph icon, boolean expanded) {
        this.uniformDesign= uniformDesign;
        this.attributeVisualisation = attributeVisualisation;
        this.titleTextProvider = titleTextProvider;
        this.icon = icon;
        this.expanded.set(expanded);
    }


    public VBox createExpandableEditorWrapper(boolean readonly) {
        VBox root = new VBox();
        Node detailView;
        if (readonly){
            detailView = attributeVisualisation.createReadOnlyVisualisation();
        } else {
            detailView = attributeVisualisation.createVisualisation();
        }

        Pane detailViewWrapper=new Pane();
        detailViewWrapper.getChildren().add(detailView);

        VBox.setVgrow(detailView, Priority.ALWAYS);
        detailViewWrapper.setBorder(new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
        detailViewWrapper.setPadding(new Insets(3));
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
        label.textProperty().bind(title);
        summary.getChildren().addAll(expandButton,iconLabel,label);
        root.getChildren().addAll(summary);

        expandButton.selectedProperty().bindBidirectional(expanded);

        root.setAlignment(Pos.CENTER_LEFT);

        ChangeListener<T> changeListener = (observable, oldValue, newValue) -> {
            updateTitleText(newValue);
        };
        attributeVisualisation.observableAttributeValue.addListener(changeListener);
        updateTitleText(attributeVisualisation.observableAttributeValue.get());

        return root;
    }

    private void updateTitleText(T newValue) {
        if (newValue == null) {
            title.set("<empty>");
        } else {
            title.set(titleTextProvider.apply(newValue));
        }
    }

    @Override
    public Node createVisualisation() {
        return createExpandableEditorWrapper(false);
    }

    @Override
    public Node createReadOnlyVisualisation() {
        return createExpandableEditorWrapper(true);
    }

    @Override
    public void expand() {
        expanded.set(true);
    }

    @Override
    public void setReadOnly() {
        attributeVisualisation.setReadOnly();
    }

    @Override
    public void destroy() {
        attributeVisualisation.destroy();
    }
}
