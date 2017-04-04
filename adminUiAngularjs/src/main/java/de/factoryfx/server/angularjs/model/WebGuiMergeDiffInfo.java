package de.factoryfx.server.angularjs.model;

import java.util.Locale;

import de.factoryfx.data.merge.AttributeDiffInfo;

public class WebGuiMergeDiffInfo {
    public final String previousValueDisplayText;
    public final String newValueValueDisplayText;
    public final String fieldDisplayText;
    public final String parentDisplayText;


    public WebGuiMergeDiffInfo(AttributeDiffInfo info, Locale locale){
        previousValueDisplayText=info.previousValueDisplayText;
        newValueValueDisplayText=info.newValueValueDisplayText;
        fieldDisplayText=info.fieldDisplayText.internal_getPreferred(locale);
        parentDisplayText=info.previousValueDisplayText;
    }
}
