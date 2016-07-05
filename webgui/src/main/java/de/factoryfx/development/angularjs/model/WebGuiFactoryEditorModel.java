package de.factoryfx.development.angularjs.model;

import java.util.Locale;

import de.factoryfx.guimodel.FactoryEditorModel;

public class WebGuiFactoryEditorModel {
    public final String editorTitle;
    public final String editButton;
    public final String saveButton;
    public final String resetButton;

    public final String deploymentTitle;
    public final String initialisationTitle;
    public final String editingTitle;

    public final String deployedChangesTitle;
    public final String deployedValidationsTitle;
    public final String deployButton;

    public final String dataItemTableColumn;
    public final String previousValueTableColumn;
    public final String newValueTableColumn;

    public final String validationErrorTableColumn;
    public final String validationAttributeTableColumn;
    public final String validationFactoryTableColumn;
    public final String deployMessage;

    public WebGuiFactoryEditorModel(FactoryEditorModel factoryEditorModel, Locale locale){
        this.editorTitle = factoryEditorModel.editorTitle.getPreferred(locale);
        this.editButton = factoryEditorModel.editButton.getPreferred(locale);
        this.saveButton = factoryEditorModel.saveButton.getPreferred(locale);
        this.resetButton = factoryEditorModel.resetButton.getPreferred(locale);

        this.deploymentTitle = factoryEditorModel.deploymentTitle.getPreferred(locale);
        this.initialisationTitle = factoryEditorModel.initialisationTitle.getPreferred(locale);
        this.editingTitle = factoryEditorModel.editingTitle.getPreferred(locale);


        this.deployedChangesTitle = factoryEditorModel.deployedChangesTitle.getPreferred(locale);
        this.deployButton = factoryEditorModel.deployButton.getPreferred(locale);
        this.dataItemTableColumn = factoryEditorModel.dataItemTableColumn.getPreferred(locale);
        this.previousValueTableColumn = factoryEditorModel.previousValueTableColumn.getPreferred(locale);
        this.newValueTableColumn = factoryEditorModel.newValueTableColumn.getPreferred(locale);

        this.validationErrorTableColumn = factoryEditorModel.validationErrorTableColumn.getPreferred(locale);
        this.validationAttributeTableColumn = factoryEditorModel.validationAttributeTableColumn.getPreferred(locale);
        this.validationFactoryTableColumn = factoryEditorModel.validationFactoryTableColumn.getPreferred(locale);
        this.deployedValidationsTitle = factoryEditorModel.deployedValidationsTitle.getPreferred(locale);
        this.deployMessage = factoryEditorModel.deployMessage.getPreferred(locale);

    }
}
