package de.factoryfx.development.factory;

import java.util.Map;

import de.factoryfx.factory.util.VoidLiveObject;

public class WebGuiLayout extends VoidLiveObject {

    public final Map<String,String> messages;

    public final Byte[] logoLarge;
    public final Byte[] logoSmall;
    public final boolean authorisationRequired;

    public WebGuiLayout(Map<String,String> messages, Byte[] logoLarge, Byte[] logoSmall, boolean authorisationRequired) {
        this.logoLarge = logoLarge;
        this.logoSmall = logoSmall;
        this.authorisationRequired = authorisationRequired;
        this.messages =messages;
    }
//    public WebGuiLayout(de.factoryfx.development.factory.WebGuiFactory webGuiFactory, Locale locale, UserManagement userManagement){
//        title= webGuiFactory.title.getPreferred(locale);
//        logoLarge= webGuiFactory.logoLarge.get();
//        logoSmall= webGuiFactory.logoSmall.get();
//        factoryEditorModel = new WebGuiFactoryEditorModel(webGuiFactory.editorModel.get(),locale);
//        authorisationRequired = userManagement.authorisationRequired();
//    }

}
