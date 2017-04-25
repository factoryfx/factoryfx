package de.factoryfx.server.angularjs.model;

import java.util.Locale;

import de.factoryfx.data.attribute.AttributeJsonWrapper;
import de.factoryfx.data.merge.AttributeDiffInfo;

public class WebGuiMergeDiffInfo {
    public final String previousValueDisplayText;
    public final String newValueValueDisplayText;
    public final String fieldDisplayText;
    public final String parentDisplayText;


    public WebGuiMergeDiffInfo(AttributeDiffInfo info, Locale locale){
        previousValueDisplayText=info.previousValueDisplayText.getDisplayText();
        newValueValueDisplayText=info.newValueValueDisplayText.map(AttributeJsonWrapper::getDisplayText).orElse("removed");
        fieldDisplayText=info.previousValueDisplayText.createAttribute().metadata.labelText.internal_getPreferred(locale);
        parentDisplayText=info.previousValueDisplayText.getDisplayText();
    }
}
