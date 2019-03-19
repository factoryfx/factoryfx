package io.github.factoryfx.javafx.data.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import com.google.common.base.Strings;
import io.github.factoryfx.data.attribute.Attribute;
import io.github.factoryfx.data.attribute.types.I18nAttribute;
import io.github.factoryfx.data.util.LanguageText;
import javafx.scene.control.Button;
import javafx.scene.control.Labeled;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
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


    //usually its bad usability to annoy user with confirmation dialogs
    private final boolean askBeforeDelete;

    public UniformDesign(Locale locale, Color dangerColor, Color warningColor, Color infoColor, Color successColor, Color primaryColor, Color borderColor, boolean askBeforeDelete) {
        this.locale = locale;
        this.dangerColor = dangerColor;
        this.warningColor = warningColor;
        this.infoColor = infoColor;
        this.successColor = successColor;
        this.primaryColor = primaryColor;
        this.borderColor = borderColor;
        this.askBeforeDelete = askBeforeDelete;
    }

    public void addIcon(MenuItem component, FontAwesome.Glyph icon) {
        if (icon != null) {
            component.setGraphic(getFontAwesome().create(icon).color(Color.BLACK));
        }
    }

    public void addIcon(Tab component, FontAwesome.Glyph icon) {
        if (icon != null) {
            component.setGraphic(getFontAwesome().create(icon));
        }
    }

    public void addIcon(Labeled component, FontAwesome.Glyph icon) {
        if (icon != null) {
            component.setGraphic(getFontAwesome().create(icon));
        }
    }

    public void addDangerIcon(Labeled component, FontAwesome.Glyph icon) {
        component.setGraphic(getFontAwesome().create(icon).color(dangerColor));
    }

    public Glyph createIcon(FontAwesome.Glyph icon) {
        return getFontAwesome().create(icon);
    }

    public Glyph createIconSuccess(FontAwesome.Glyph icon) {
        return getFontAwesome().create(icon).color(successColor);
    }

    public Glyph createIconDanger(FontAwesome.Glyph icon) {
        return getFontAwesome().create(icon).color(dangerColor);
    }

    private GlyphFont getFontAwesome() {
        if (fontAwesome == null) {
            try (InputStream inputStream = UniformDesign.class.getResourceAsStream("/io/github/factoryfx/javafx/icon/fontawesome-webfont4_3.ttf")) {
                GlyphFont font_awesome = new FontAwesome(inputStream);
                GlyphFontRegistry.register(font_awesome);
                fontAwesome = GlyphFontRegistry.font("FontAwesome");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return fontAwesome;
    }

    public Locale getLocale() {
        return locale;
    }

    public Locale get() {
        return locale;
    }

    public String getLabelText(Attribute<?,?> attribute) {
        return attribute.internal_getPreferredLabelText(locale);
    }

    public String getLabelText(Attribute<?,?> attribute, String attributeVariableName) {
        String labelText = attribute.internal_getPreferredLabelText(locale);
        if (Strings.isNullOrEmpty(labelText)){
            labelText=attributeVariableName;
        }
        return labelText;
    }

    public String getTooltipText(Attribute<?,?> attribute) {
        return attribute.internal_getPreferredTooltipText(locale);
    }

    public String getText(LanguageText languageText) {
        return languageText.internal_getPreferred(locale);
    }

    public String getText(I18nAttribute attribute) {
        return attribute.get().internal_getPreferred(locale);
    }

    public void setBackGroundColor(Button button, Color color) {
        button.setStyle("-fx-base: " + toCssColor(color) + ";");
    }

    public void setDangerButton(Button button) {
        button.getStyleClass().add("dangerButton");
    }

    public void setWarningButton(Button button) {
        button.getStyleClass().add("warningButton");
    }

    public void setInfoButton(Button button) {
        button.getStyleClass().add("infoButton");
    }

    public void setSuccessButton(Button button) {
        button.getStyleClass().add("successButton");
    }

    public void setPrimaryButton(Button button) {
        button.getStyleClass().add("primaryButton");
    }

    private String toCssColor(Color color) {
        return "rgba(" + Math.round(255 * color.getRed()) + "," + Math.round(255 * color.getGreen()) + "," + Math.round(255 * color.getBlue()) + "," + color.getOpacity() + ")";
    }

    public boolean isAskBeforeDelete() {
        return askBeforeDelete;
    }

}
