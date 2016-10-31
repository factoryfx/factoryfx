package de.factoryfx.data.merge;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.factoryfx.data.util.LanguageText;

//just the infotext used in gui
public class MergeResultEntryInfo{
    public final String previousValueDisplayText;
    public final String newValueValueDisplayText;
    public final LanguageText fieldDisplayText;
    public final String parentDisplayText;

    @JsonCreator
    public MergeResultEntryInfo(
            @JsonProperty("previousValueDisplayText") String previousValueDisplayText,
            @JsonProperty("newValueValueDisplayText") String newValueValueDisplayText,
            @JsonProperty("fieldDisplayText")LanguageText fieldDisplayText,
            @JsonProperty("parentDisplayText") String parentDisplayText) {
        this.previousValueDisplayText = previousValueDisplayText;
        this.newValueValueDisplayText = newValueValueDisplayText;
        this.fieldDisplayText = fieldDisplayText;
        this.parentDisplayText = parentDisplayText;
    }
}
