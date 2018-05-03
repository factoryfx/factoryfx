package de.factoryfx.javafx;

import java.util.Locale;

import de.factoryfx.javafx.data.util.UniformDesign;
import javafx.scene.paint.Color;

public class UniformDesignBuilder {
    public static UniformDesign build(){
        return new UniformDesign(Locale.ENGLISH, Color.web("#FF7979"),Color.web("#F0AD4E"),Color.web("#5BC0DE"),Color.web("#5CB85C"),Color.web("#5494CB"),Color.web("#B5B5B5"),false);
    }
}
