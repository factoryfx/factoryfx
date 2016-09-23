package de.factoryfx.data.merge;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

//just the infotext used in gui
public class MergeResultEntryInfo{
    public final String previousValueDisplayText;
    public final String newValueValueDisplayText;
    public final String fieldDisplayText;
    public final String parentDisplayText;

    @JsonCreator
    public MergeResultEntryInfo(
            @JsonProperty("previousValueDisplayText") String previousValueDisplayText,
            @JsonProperty("newValueValueDisplayText") String newValueValueDisplayText,
            @JsonProperty("fieldDisplayText")String fieldDisplayText,
            @JsonProperty("parentDisplayText") String parentDisplayText) {
        this.previousValueDisplayText = previousValueDisplayText;
        this.newValueValueDisplayText = newValueValueDisplayText;
        this.fieldDisplayText = fieldDisplayText;
        this.parentDisplayText = parentDisplayText;
    }
}
