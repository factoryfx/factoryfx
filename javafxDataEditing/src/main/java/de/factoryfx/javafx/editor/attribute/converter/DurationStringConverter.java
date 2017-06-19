package de.factoryfx.javafx.editor.attribute.converter;

import java.time.Duration;

import javafx.util.StringConverter;

/**
 * Created by mhavlik on 16.06.17.
 */
public class DurationStringConverter extends StringConverter<Duration> {
    @Override
    public String toString(Duration object) {
        if (object==null){
            return "";
        }
        return object.toString();
    }

    @Override
    public Duration fromString(String string) {
        return Duration.parse(string);
    }

}
