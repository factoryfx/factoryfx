package de.factoryfx.data;

import java.util.Optional;

public interface SearchTextMatchable {

    default boolean matchSearchText(String newValue) {
        return Optional.ofNullable(toString()).map(s->s.contains(newValue)).orElse(null);
    }
}
