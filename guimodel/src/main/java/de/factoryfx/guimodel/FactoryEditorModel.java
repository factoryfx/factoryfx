package de.factoryfx.guimodel;

import de.factoryfx.factory.util.LanguageText;

public class FactoryEditorModel {

    public final LanguageText editorTitle = new LanguageText().en("Factory editor");

    public final LanguageText initialisationTitle = new LanguageText().en("Initialisation");
    public final LanguageText editingTitle = new LanguageText().en("Edit");
    public final LanguageText deploymentTitle = new LanguageText().en("Deployment");

    public final LanguageText editButton = new LanguageText().en("continue editing");
    public final LanguageText saveButton = new LanguageText().en("stage changes");
    public final LanguageText resetButton= new LanguageText().en("revert changes");

    public final LanguageText deployedChangesTitle = new LanguageText().en("Deployed changes");
    public final LanguageText deployedValidationsTitle = new LanguageText().en("Validation error");
    public final LanguageText deployButton = new LanguageText().en("Deploy");
    public final LanguageText deployMessage = new LanguageText().en("Changes successfully deployed");

    public final LanguageText dataItemTableColumn = new LanguageText().en("Data Item");
    public final LanguageText previousValueTableColumn = new LanguageText().en("Previous value");
    public final LanguageText newValueTableColumn = new LanguageText().en("New value");


    public final LanguageText validationErrorTableColumn = new LanguageText().en("Error");
    public final LanguageText validationAttributeTableColumn = new LanguageText().en("Attribute");
    public final LanguageText validationFactoryTableColumn = new LanguageText().en("Factory");
}
