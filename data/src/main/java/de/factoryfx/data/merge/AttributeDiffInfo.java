package de.factoryfx.data.merge;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.util.LanguageText;

//just the infotext used in gui
public class AttributeDiffInfo {
    public final String previousValueDisplayText;
    public final String newValueValueDisplayText;
    public final LanguageText fieldDisplayText;
    public final String parentDisplayText;

    @JsonCreator
    public AttributeDiffInfo(
            @JsonProperty("previousValueDisplayText") String previousValueDisplayText,
            @JsonProperty("newValueValueDisplayText") String newValueValueDisplayText,
            @JsonProperty("fieldDisplayText")LanguageText fieldDisplayText,
            @JsonProperty("parentDisplayText") String parentDisplayText) {
        this.previousValueDisplayText = previousValueDisplayText;
        this.newValueValueDisplayText = newValueValueDisplayText;
        this.fieldDisplayText = fieldDisplayText;
        this.parentDisplayText = parentDisplayText;
    }

    public AttributeDiffInfo(String parentDisplayText, Attribute<?> attribute, Attribute<?> newAttributeDisplayText) {
        //created here cause attribute ist updated later
        this(attribute.getDisplayText(), newAttributeDisplayText.getDisplayText(), attribute.metadata.labelText, parentDisplayText);
    }

    public AttributeDiffInfo(String parentDisplayText, Attribute<?> attribute) {
        //created here cause attribute ist updated later
        this(attribute.getDisplayText(), "removed", attribute.metadata.labelText, parentDisplayText);
    }

    @Override
    public String toString() {
        return "MergeResultEntryInfo{" + "previousValueDisplayText='" + previousValueDisplayText + '\'' + ", newValueValueDisplayText='" + newValueValueDisplayText + '\'' + ", fieldDisplayText=" + fieldDisplayText + ", parentDisplayText='" + parentDisplayText + '}';
    }
}
