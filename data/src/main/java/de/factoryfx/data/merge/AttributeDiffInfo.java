package de.factoryfx.data.merge;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.AttributeJsonWrapper;

//just the infotext used in gui
public class AttributeDiffInfo {
    @JsonProperty
    private final AttributeJsonWrapper previousValueDisplayText;
    @JsonProperty
    private final AttributeJsonWrapper newAttribute;
    @JsonProperty
    private final String parentDisplayText;
    @JsonProperty
    private final String parentId;

    @JsonCreator
    public AttributeDiffInfo(
            @JsonProperty("previousValueDisplayText") AttributeJsonWrapper previousValueDisplayText,
            @JsonProperty("newAttribute") AttributeJsonWrapper newAttribute,
            @JsonProperty("parentDisplayText") String parentDisplayText,
            @JsonProperty("parentId") String parentId) {
        this.previousValueDisplayText = previousValueDisplayText;
        this.newAttribute = newAttribute;
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

    @JsonIgnore
    public String getNewAttributeDisplayText(){
        if (isNewAttributePresent()){
            return newAttribute.getDisplayText();
        }
        return "removed";
    }

    @JsonIgnore
    public boolean isNewAttributePresent(){
        return newAttribute!=null;
    }

    public Attribute createNewAttributeDisplayAttribute(){
        return newAttribute.createAttribute();
    }

    @JsonIgnore
    public String getPreviousAttributeDisplayText(){
       return previousValueDisplayText.getDisplayText();
    }

    @JsonIgnore
    public Attribute createPreviousAttribute(){
        return previousValueDisplayText.createAttribute();
    }

    @JsonIgnore
    public boolean isFromFactory(String factoryId){
        return parentId.equals(factoryId);
    }

    @JsonIgnore
    public String parentDisplayText(){
        return parentDisplayText;
    }

}
