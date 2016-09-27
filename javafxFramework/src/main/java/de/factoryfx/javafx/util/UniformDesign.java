package de.factoryfx.javafx.util;

import java.io.IOException;
import java.io.InputStream;

import javafx.scene.control.Labeled;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

public class UniformDesign {

    private static final GlyphFont FONT_AWESOME;
    static {
        try (InputStream inputStream = UniformDesign.class.getResourceAsStream("/de/factoryfx/javafx/icon/fontawesome-webfont4_3.ttf")) {
            GlyphFont font_awesome = new FontAwesome(inputStream);
            GlyphFontRegistry.register(font_awesome);
            FONT_AWESOME = GlyphFontRegistry.font("FontAwesome");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final Color dangerColor;
    private final Color warningColor;
    private final Color infoColor;
    private final Color successColor;
    private final Color primaryColor;
    private final Color borderColor;

    public UniformDesign(Color dangerColor, Color warningColor, Color infoColor, Color successColor, Color primaryColor, Color borderColor) {
        this.dangerColor = dangerColor;
        this.warningColor = warningColor;
        this.infoColor = infoColor;
        this.successColor = successColor;
        this.primaryColor = primaryColor;
        this.borderColor = borderColor;
    }

    public UniformDesign() {
        this(Color.web("#FF7979"),Color.web("#F0AD4E"),Color.web("#5BC0DE"),Color.web("#5CB85C"),Color.web("#5494CB"),Color.web("#B5B5B5"));
    }

    public void addIcon(Labeled component, FontAwesome.Glyph icon){
        component.setGraphic(UniformDesign.FONT_AWESOME.create(icon));
    }

    public void addDangerIcon(Labeled component, FontAwesome.Glyph icon){
        component.setGraphic(UniformDesign.FONT_AWESOME.create(icon).color(dangerColor));
    }


    public void addIcon(MenuItem component, FontAwesome.Glyph icon){
        component.setGraphic(UniformDesign.FONT_AWESOME.create(icon));
    }

    public Glyph createIcon(FontAwesome.Glyph icon){
        return UniformDesign.FONT_AWESOME.create(icon);
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
//    public static void setPrimaryButton(Button button) {
//        clearButtonClasses(button);
//        button.getStyleClass().add("primaryButton");
//    }
//
//    public static void setSuccessButton(Button button) {
//        clearButtonClasses(button);
//        button.getStyleClass().add("successButton");
//    }
//
//    public static void setWarningsButton(Button button) {
//        clearButtonClasses(button);
//        button.getStyleClass().add("warningButton");
//    }
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
