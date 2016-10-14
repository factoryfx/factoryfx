package de.factoryfx.javafx.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.types.I18nAttribute;
import de.factoryfx.data.util.LanguageText;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

public class UniformDesign {

    private GlyphFont fontAwesome;

    private final Color dangerColor;
    private final Color warningColor;
    private final Color infoColor;
    private final Color successColor;
    private final Color primaryColor;
    private final Color borderColor;
    private final Locale locale;

    public UniformDesign(Locale locale, Color dangerColor, Color warningColor, Color infoColor, Color successColor, Color primaryColor, Color borderColor) {
        this.locale = locale;
        this.dangerColor = dangerColor;
        this.warningColor = warningColor;
        this.infoColor = infoColor;
        this.successColor = successColor;
        this.primaryColor = primaryColor;
        this.borderColor = borderColor;
    }

    public void addIcon(Labeled component, FontAwesome.Glyph icon){
        component.setGraphic(getFontAwesome().create(icon));
    }

    public void addDangerIcon(Labeled component, FontAwesome.Glyph icon){
        component.setGraphic(getFontAwesome().create(icon).color(dangerColor));
    }


    public void addIcon(MenuItem component, FontAwesome.Glyph icon){
        if (icon!=null){
            component.setGraphic(getFontAwesome().create(icon));
        }
    }

    public Glyph createIcon(FontAwesome.Glyph icon){
        return getFontAwesome().create(icon);
    }

    private GlyphFont getFontAwesome(){
        if (fontAwesome==null){
            try (InputStream inputStream = UniformDesign.class.getResourceAsStream("/de/factoryfx/javafx/icon/fontawesome-webfont4_3.ttf")) {
                GlyphFont font_awesome = new FontAwesome(inputStream);
                GlyphFontRegistry.register(font_awesome);
                fontAwesome = GlyphFontRegistry.font("FontAwesome");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return fontAwesome;
    }

    public Locale getLocale(){
        return locale;
    }

    public Locale get(){
        return locale;
    }

    public String getLabelText(Attribute<?> attribute){
        return attribute.metadata.labelText.getPreferred(locale);
    }

    public String getText(LanguageText languageText){
        return languageText.getPreferred(locale);
    }

    public String getText(I18nAttribute attribute){
        return attribute.get().getPreferred(locale);
    }

    public VBox createListEditSummary(SimpleObjectProperty<? extends List> boundTo, VBox detailView) {
        VBox root = new VBox();
        detailView.setBorder(new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
        VBox.setMargin(detailView,new Insets(3,0,0,0));

        Label icon = new Label();
        addIcon(icon,FontAwesome.Glyph.LIST);

        ToggleButton expandButton=new ToggleButton();
        expandButton.setOnAction(event -> {
            if (expandButton.isSelected()){
                root.getChildren().add(detailView);
            } else {
                root.getChildren().remove(detailView);
            }
        });
        addIcon(expandButton, FontAwesome.Glyph.EXPAND);

        HBox summary=new HBox(3);
        summary.setAlignment(Pos.CENTER_LEFT);
        Label label = new Label();
        InvalidationListener listener = observable -> {
            if (boundTo.get() == null) {
                label.setText("<empty>");
            } else {
                label.setText("Items: "+boundTo.get().size());
            }
        };
        boundTo.addListener(listener);
        listener.invalidated(null);
        summary.getChildren().addAll(icon,label,expandButton);
        root.getChildren().addAll(summary);
        return root;
    }


//
//    private static void clearButtonClasses(Button button) {
//        button.getStyleClass().remove("dangerButton");
//        button.getStyleClass().remove("warningButton");
//        button.getStyleClass().remove("successButton");
//        button.getStyleClass().remove("primaryButton");
//    }
//
//    public static void setBackGroundColor(Button button, Color color) {
//        button.setStyle("-fx-base: " + toCssColor(color) + ";");
//    }
//
//    public static void setBackGroundColor(TableView<?> tableView, Color color) {
//        tableView.setStyle("-fx-background-color: " + toCssColor(color) + ";");
//    }
//
//    public static void setBackGroundColor(Pane pane, Color color) {
//        pane.setStyle("-fx-background-color: " + toCssColor(color) + ";");
//    }
//
//    public static void setDangerButton(Button button) {
//        clearButtonClasses(button);
//        button.getStyleClass().add("dangerButton");
//    }
//
//    public static void setFontColor(Label label, Color color) {
//        label.setTextFill(color);
//    }
//
//    public static void setFontSize(Label label, int size) {
//        label.setStyle("-fx-font-size: " + size + ";");
//    }
//
//
//    public static String toCssColor(Color color) {
//        return "rgba(" +
//                Math.round(255 * color.getRed()) + "," +
//                Math.round(255 * color.getGreen()) + "," +
//                Math.round(255 * color.getBlue()) + "," +
//                color.getOpacity() +
//                ")";
//    }
}
