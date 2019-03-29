package io.github.factoryfx.javafx;

import java.util.Locale;

import io.github.factoryfx.javafx.factory.util.UniformDesign;
import javafx.scene.paint.Color;

public class UniformDesignBuilder {
    public static UniformDesign build(){
        return build(Locale.ENGLISH);
    }

    public static UniformDesign build(Locale locale){
        return new UniformDesign(locale, Color.web("#FF7979"),Color.web("#F0AD4E"),Color.web("#5BC0DE"),Color.web("#5CB85C"),Color.web("#5494CB"),Color.web("#B5B5B5"),false);
    }
}
