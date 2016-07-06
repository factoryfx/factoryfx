package de.factoryfx.factory.attribute;

import java.util.Locale;

import de.factoryfx.factory.util.LanguageText;

public class AttributeMetadata {
    public LanguageText labelText=new LanguageText();
    public String permisson;

    public AttributeMetadata() {

    }

    public AttributeMetadata labelText(String labelText){
        this.labelText.put(Locale.ENGLISH,labelText);
        return this;
    }

    public AttributeMetadata labelText(String labelText, Locale locale){
        this.labelText.put(locale,labelText);
        return this;
    }

    public AttributeMetadata de(String labelText){
        this.labelText.put(Locale.GERMAN,labelText);
        return this;
    }

    public AttributeMetadata en(String labelText){
        this.labelText.put(Locale.ENGLISH,labelText);
        return this;
    }

    public AttributeMetadata permission(String permission){
        this.permisson= permission;
        return this;
    }

}
