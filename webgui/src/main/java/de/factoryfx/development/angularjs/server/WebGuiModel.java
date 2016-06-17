package de.factoryfx.development.angularjs.server;

import java.util.Locale;

import de.factoryfx.guimodel.GuiModel;

public class WebGuiModel {

    public String title;
    public byte[] logoLarge;
    public byte[] logoSmall;

    public WebGuiModel(GuiModel guiModel, Locale locale){
        title=guiModel.title.getPreferred(locale);
        logoLarge=guiModel.logoLarge;
        logoSmall=guiModel.logoSmall;
    }
}
