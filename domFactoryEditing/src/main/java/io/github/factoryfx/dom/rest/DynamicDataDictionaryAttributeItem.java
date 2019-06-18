package io.github.factoryfx.dom.rest;

import java.util.HashMap;
import java.util.List;

public class DynamicDataDictionaryAttributeItem {
    public final String type;
    public final boolean nullable;
    public final String en;
    public final String de;
    public final List<String> possibleEnumValues;

    public DynamicDataDictionaryAttributeItem(String type, boolean nullable, String en, String de, List<String> possibleEnumValues) {
        this.type = type;
        this.nullable = nullable;
        this.en = en;
        this.de = de;
        this.possibleEnumValues = possibleEnumValues;
    }
}
