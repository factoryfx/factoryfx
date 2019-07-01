package io.github.factoryfx.dom.rest;

public class DomGuiMetadata {
    public final DynamicDataDictionary dynamicDataDictionary;
    public final GuiConfiguration guiConfiguration;


    public DomGuiMetadata(DynamicDataDictionary dynamicDataDictionary, GuiConfiguration guiConfiguration) {
        this.dynamicDataDictionary = dynamicDataDictionary;
        this.guiConfiguration = guiConfiguration;
    }
}
