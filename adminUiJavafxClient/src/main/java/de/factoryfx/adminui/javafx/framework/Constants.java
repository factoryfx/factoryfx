package de.factoryfx.adminui.javafx.framework;

import java.io.IOException;
import java.io.InputStream;

import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

public class Constants {

    static{
        try (InputStream inputStream= Constants.class.getResourceAsStream("/de/factoryfx/richclient/icon/fontawesome-webfont4_3.ttf")){
            GlyphFont font_awesome =new FontAwesome(inputStream);
            GlyphFontRegistry.register(font_awesome);
            FONT_AWESOME=GlyphFontRegistry.font("FontAwesome");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static final GlyphFont FONT_AWESOME;

}
