package de.factoryfx.development.angularjs.model;

import java.util.Locale;

import de.factoryfx.guimodel.GuiModel;
import de.factoryfx.user.UserManagement;

public class WebGuiModel {

    public String title;
    public byte[] logoLarge;
    public byte[] logoSmall;
    public WebGuiFactoryEditorModel factoryEditorModel;
    public boolean authorisationRequired;

    public WebGuiModel(GuiModel guiModel, Locale locale, UserManagement userManagement){
        title=guiModel.title.getPreferred(locale);
        logoLarge=guiModel.logoLarge;
        logoSmall=guiModel.logoSmall;
        factoryEditorModel = new WebGuiFactoryEditorModel(guiModel.factoryEditorModel,locale);
        authorisationRequired = userManagement.authorisationRequired();
    }
}
