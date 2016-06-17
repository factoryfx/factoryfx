package de.factoryfx.guimodel;

import java.util.ArrayList;
import java.util.List;

import de.factoryfx.factory.util.LanguageText;

public class GuiModel {
    public final List<RuntimeQueryView> runtimeQueryViews=new ArrayList<>();
    public LanguageText title=new LanguageText();
    public byte[] logoLarge;
    public byte[] logoSmall;

}
