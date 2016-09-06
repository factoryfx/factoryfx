package de.factoryfx.adminui.angularjs.factory;

import java.util.Map;

import de.factoryfx.factory.util.VoidLiveObject;

public class Layout extends VoidLiveObject {

    public final Map<String,String> messages;

    public final byte[] logoLarge;
    public final byte[] logoSmall;
    public final boolean authorisationRequired;

    public Layout(Map<String,String> messages, byte[] logoSmall, byte[] logoLarge, boolean authorisationRequired) {
        this.logoLarge = logoLarge;
        this.logoSmall = logoSmall;
        this.authorisationRequired = authorisationRequired;
        this.messages =messages;
    }
//    public Layout(de.factoryfx.development.factory.WebGuiFactory webGuiFactory, Locale locale, UserManagement userManagement){
//        title= webGuiFactory.title.getPreferred(locale);
//        logoLarge= webGuiFactory.logoLarge.get();
//        logoSmall= webGuiFactory.logoSmall.get();
//        factoryEditorModel = new WebGuiFactoryEditorModel(webGuiFactory.editorModel.get(),locale);
//        authorisationRequired = userManagement.authorisationRequired();
//    }

}
