package de.factoryfx.data.attribute;

import java.util.Locale;

import de.factoryfx.data.util.LanguageText;

public class AttributeMetadata {
    public final LanguageText labelText=new LanguageText();
    public final LanguageText addonText=new LanguageText();;
    public String permission;

    public AttributeMetadata() {

    }
    public AttributeMetadata addonText(String addonText){
        this.addonText.internal_put(Locale.ENGLISH,addonText);
        return this;
    }


    public AttributeMetadata labelText(String labelText){
        this.labelText.internal_put(Locale.ENGLISH,labelText);
        return this;
    }

    public AttributeMetadata labelText(String labelText, Locale locale){
        this.labelText.internal_put(locale,labelText);
        return this;
    }


    public AttributeMetadata de(String labelText){
        this.labelText.internal_put(Locale.GERMAN,labelText);
        return this;
    }

    public AttributeMetadata en(String labelText){
        this.labelText.internal_put(Locale.ENGLISH,labelText);
        return this;
    }

    public AttributeMetadata permission(String permission){
        this.permission = permission;
        return this;
    }

}
