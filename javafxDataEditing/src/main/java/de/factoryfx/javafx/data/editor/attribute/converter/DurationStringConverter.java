package de.factoryfx.javafx.data.editor.attribute.converter;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.util.StringConverter;

public class DurationStringConverter extends StringConverter<Duration> {
    @Override
    public String toString(Duration duration) {
        if (duration==null){
            return "";
        }
        long seconds = duration.getSeconds();
        long absSeconds = Math.abs(seconds);
        String positive = String.format(
            "%dh:%02dm:%02ds",
            absSeconds / 3600,
            (absSeconds % 3600) / 60,
            absSeconds % 60);
        return seconds < 0 ? "-" + positive : positive;
    }

    @Override
    public Duration fromString(String string) {
        Matcher m = Pattern.compile("(\\d+)h:(\\d{2})m:(\\d{2})s").matcher(string);
        if(!m.matches()) return null;
        return Duration.ofSeconds(Long.parseLong(m.group(1))*3600+ Long.parseLong(m.group(2)) * 60 + Long.parseLong(m.group(3)));
    }

}
