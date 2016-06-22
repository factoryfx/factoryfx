package de.factoryfx.development.angularjs.server.model;

import java.util.Locale;

import de.factoryfx.guimodel.FactoryEditorModel;

public class WebGuiFactoryEditorModel {
    public final String editorTitle;
    public final String editButton;
    public final String saveButton;

    public final String deploymentTitle;
    public final String deployedChangesTitle;
    public final String deployButton;
    public final String dataItemTableColumn;
    public final String previousValueTableColumn;
    public final String newValueTableColumn;

    public WebGuiFactoryEditorModel(FactoryEditorModel factoryEditorModel, Locale locale){
        this.editorTitle = factoryEditorModel.editorTitle.getPreferred(locale);
        this.editButton = factoryEditorModel.editButton.getPreferred(locale);
        this.saveButton = factoryEditorModel.saveButton.getPreferred(locale);

        this.deploymentTitle = factoryEditorModel.deploymentTitle.getPreferred(locale);
        this.deployedChangesTitle = factoryEditorModel.deployedChangesTitle.getPreferred(locale);
        this.deployButton = factoryEditorModel.deployButton.getPreferred(locale);
        this.dataItemTableColumn = factoryEditorModel.dataItemTableColumn.getPreferred(locale);
        this.previousValueTableColumn = factoryEditorModel.previousValueTableColumn.getPreferred(locale);
        this.newValueTableColumn = factoryEditorModel.newValueTableColumn.getPreferred(locale);
    }
}
