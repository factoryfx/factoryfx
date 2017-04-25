package de.factoryfx.data.merge;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.AttributeJsonWrapper;

//just the infotext used in gui
public class AttributeDiffInfo {
    public final AttributeJsonWrapper previousValueDisplayText;
    public final Optional<AttributeJsonWrapper> newValueValueDisplayText;
    public final String parentDisplayText;
    public final String parentId;

    @JsonCreator
    public AttributeDiffInfo(
            @JsonProperty("previousValueDisplayText") AttributeJsonWrapper previousValueDisplayText,
            @JsonProperty("newValueValueDisplayText") AttributeJsonWrapper newValueValueDisplayText,
            @JsonProperty("parentDisplayText") String parentDisplayText,
            @JsonProperty("parentId") String parentId) {
        this.previousValueDisplayText = previousValueDisplayText;
        this.newValueValueDisplayText = Optional.ofNullable(newValueValueDisplayText);
        this.parentDisplayText = parentDisplayText;
        this.parentId = parentId;
    }

    public AttributeDiffInfo(Data parent, Attribute<?> attribute, Attribute<?> newAttributeDisplayText) {
        //created here cause attribute ist updated later
        this(new AttributeJsonWrapper(attribute,""), new AttributeJsonWrapper(newAttributeDisplayText,""), parent.internal().getDisplayText(),parent.getId());
    }

    public AttributeDiffInfo(Data parent, Attribute<?> attribute) {
        //created here cause attribute ist updated later
        this(new AttributeJsonWrapper(attribute,""), null, parent.internal().getDisplayText(),parent.getId());
    }

}
