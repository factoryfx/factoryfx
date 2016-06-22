package de.factoryfx.development.angularjs.server.model;

import java.util.Locale;

import de.factoryfx.guimodel.GuiModel;

public class WebGuiModel {

    public String title;
    public byte[] logoLarge;
    public byte[] logoSmall;
    public WebGuiFactoryEditorModel factoryEditorModel;

    public WebGuiModel(GuiModel guiModel, Locale locale){
        title=guiModel.title.getPreferred(locale);
        logoLarge=guiModel.logoLarge;
        logoSmall=guiModel.logoSmall;
        factoryEditorModel = new WebGuiFactoryEditorModel(guiModel.factoryEditorModel,locale);
    }
}
