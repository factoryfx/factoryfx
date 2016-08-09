package de.factoryfx.development.factory;

import java.util.HashMap;
import java.util.Locale;
import java.util.Optional;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.attribute.AttributeMetadata;
import de.factoryfx.factory.attribute.util.ByteArrayAttribute;
import de.factoryfx.factory.attribute.util.I18nAttribute;
import de.factoryfx.factory.attribute.util.ObjectValueAttribute;
import de.factoryfx.user.UserManagement;

public class WebGuiLayoutFactory extends FactoryBase<WebGuiLayout, WebGuiLayoutFactory> {
    public final ObjectValueAttribute<UserManagement> userManagement = new ObjectValueAttribute<>(new AttributeMetadata().labelText("userManagement"));

    public final ByteArrayAttribute logoSmall = new ByteArrayAttribute(new AttributeMetadata().labelText("logoSmall"));
    public final ByteArrayAttribute logoLarge = new ByteArrayAttribute(new AttributeMetadata().labelText("logoLarge"));


    public final I18nAttribute title = new I18nAttribute(new AttributeMetadata().en("title")).en("Example");

    public final I18nAttribute editorTitle = new I18nAttribute(new AttributeMetadata().en("editorTitle")).en("Factory editor");

    public final I18nAttribute initialisationTitle = new I18nAttribute(new AttributeMetadata().en("editorTitle")).en("Initialisation");
    public final I18nAttribute editingTitle = new I18nAttribute(new AttributeMetadata().en("editingTitle")).en("Edit");
    public final I18nAttribute changeTitle = new I18nAttribute(new AttributeMetadata().en("changeTitle")).en("Staged changes");
    public final I18nAttribute deploymentTitle = new I18nAttribute(new AttributeMetadata().en("deploymentTitle")).en("Deployment");

    public final I18nAttribute editButton = new I18nAttribute(new AttributeMetadata().en("editButton")).en("continue editing");
    public final I18nAttribute saveButton = new I18nAttribute(new AttributeMetadata().en("saveButton")).en("stage changes");
    public final I18nAttribute resetButton= new I18nAttribute(new AttributeMetadata().en("resetButton")).en("revert changes");

    public final I18nAttribute deployedChangesTitle = new I18nAttribute(new AttributeMetadata().en("deployedChangesTitle")).en("Changes");
    public final I18nAttribute deployedValidationsTitle = new I18nAttribute(new AttributeMetadata().en("deployedValidationsTitle")).en("Validation error");
    public final I18nAttribute deployButton = new I18nAttribute(new AttributeMetadata().en("deployButton")).en("Deploy");
    public final I18nAttribute deployMessage = new I18nAttribute(new AttributeMetadata().en("deployMessage")).en("Changes successfully deployed");

    public final I18nAttribute dataItemTableColumn = new I18nAttribute(new AttributeMetadata().en("dataItemTableColumn")).en("Factory");
    public final I18nAttribute fieldTableColumn = new I18nAttribute(new AttributeMetadata().en("fieldTableColumn")).en("Attribute");
    public final I18nAttribute previousValueTableColumn = new I18nAttribute(new AttributeMetadata().en("previousValueTableColumn")).en("Previous value");
    public final I18nAttribute newValueTableColumn = new I18nAttribute(new AttributeMetadata().en("newValueTableColumn")).en("New value");


    public final I18nAttribute validationErrorTableColumn = new I18nAttribute(new AttributeMetadata().en("validationErrorTableColumn")).en("Error");
    public final I18nAttribute validationAttributeTableColumn = new I18nAttribute(new AttributeMetadata().en("validationAttributeTableColumn")).en("Attribute");
    public final I18nAttribute validationFactoryTableColumn = new I18nAttribute(new AttributeMetadata().en("validationFactoryTableColumn")).en("Factory");

    @Override
    protected WebGuiLayout createImp(Optional<WebGuiLayout> previousLiveObject) {
        HashMap<String,String> messages=new HashMap<>();
        this.visitAttributesFlat((attributeVariableName, attribute) -> {
            if (attribute instanceof I18nAttribute) {
                messages.put(attributeVariableName,((I18nAttribute) attribute).get().getPreferred(Locale.ENGLISH));
            }
        });

        return new WebGuiLayout(messages, logoSmall.get(), logoLarge.get(), userManagement.get().authorisationRequired());
    }
}
