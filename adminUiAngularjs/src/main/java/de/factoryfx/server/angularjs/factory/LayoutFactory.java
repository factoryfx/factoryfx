package de.factoryfx.server.angularjs.factory;

import java.util.HashMap;
import java.util.Locale;

import de.factoryfx.data.attribute.types.ByteArrayAttribute;
import de.factoryfx.data.attribute.types.I18nAttribute;
import de.factoryfx.data.attribute.types.ObjectValueAttribute;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.user.UserManagement;

public class LayoutFactory extends SimpleFactoryBase<Layout,Void> {
    public final ObjectValueAttribute<UserManagement> userManagement = new ObjectValueAttribute<UserManagement>().labelText("userManagement");

    public final ByteArrayAttribute logoSmall = new ByteArrayAttribute().labelText("logoSmall");
    public final ByteArrayAttribute logoLarge = new ByteArrayAttribute().labelText("logoLarge");


    public final I18nAttribute title = new I18nAttribute().en("title").en("Example");

    public final I18nAttribute editorTitle = new I18nAttribute().en("editorTitle").en("Editor");
    public final I18nAttribute dashboardTitle = new I18nAttribute().en("Dashboard").en("Dashboard");
    public final I18nAttribute historyTitle = new I18nAttribute().en("History").en("History");
    public final I18nAttribute viewsTitle = new I18nAttribute().en("View").en("View");

    public final I18nAttribute initialisationTitle = new I18nAttribute().en("editorTitle").en("Initialisation");
    public final I18nAttribute editingTitle = new I18nAttribute().en("editingTitle").en("Edit");
    public final I18nAttribute changeTitle = new I18nAttribute().en("changeTitle").en("Staged changes");
    public final I18nAttribute deploymentTitle = new I18nAttribute().en("deploymentTitle").en("Deployment");

    public final I18nAttribute editButton = new I18nAttribute().en("editButton").en("continue editing");
    public final I18nAttribute saveButton = new I18nAttribute().en("saveButton").en("stage changes");
    public final I18nAttribute resetButton= new I18nAttribute().en("resetButton").en("revert changes");

    public final I18nAttribute deployedChangesTitle = new I18nAttribute().en("deployedChangesTitle").en("Changes");
    public final I18nAttribute deployedValidationsTitle = new I18nAttribute().en("deployedValidationsTitle").en("Validation error");
    public final I18nAttribute deployButton = new I18nAttribute().en("deployButton").en("Deploy");
    public final I18nAttribute deployRestButton = new I18nAttribute().en("deployButton").en("Reset");
    public final I18nAttribute deployMessage = new I18nAttribute().en("deployMessage").en("Changes successfully deployed");

    public final I18nAttribute dataItemTableColumn = new I18nAttribute().en("dataItemTableColumn").en("Factory");
    public final I18nAttribute fieldTableColumn = new I18nAttribute().en("fieldTableColumn").en("Attribute");
    public final I18nAttribute previousValueTableColumn = new I18nAttribute().en("previousValueTableColumn").en("Previous value");
    public final I18nAttribute newValueTableColumn = new I18nAttribute().en("newValueTableColumn").en("New value");


    public final I18nAttribute validationErrorTableColumn = new I18nAttribute().en("validationErrorTableColumn").en("Error");
    public final I18nAttribute validationAttributeTableColumn = new I18nAttribute().en("validationAttributeTableColumn").en("Attribute");
    public final I18nAttribute validationFactoryTableColumn = new I18nAttribute().en("validationFactoryTableColumn").en("Factory");

    @Override
    public Layout createImpl() {
        HashMap<String,String> messages=new HashMap<>();
        internal().visitAttributesFlat((attributeVariableName, attribute) -> {
            if (attribute instanceof I18nAttribute) {
                messages.put(attributeVariableName,((I18nAttribute) attribute).get().internal_getPreferred(Locale.ENGLISH));
            }
        });

        return new Layout(messages, logoSmall.get(), logoLarge.get(), userManagement.get().authorisationRequired());
    }
}
