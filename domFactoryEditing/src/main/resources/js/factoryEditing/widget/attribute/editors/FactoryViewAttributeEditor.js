//generated code don't edit manually
import { DomUtility } from "../../../DomUtility";
import { AttributeEditorWidget } from "../AttributeEditorWidget";
export class FactoryViewAttributeEditor extends AttributeEditorWidget {
    constructor(attributeAccessor, inputId, factoryEditorModel, httpClient) {
        super(attributeAccessor, inputId);
        this.attributeAccessor = attributeAccessor;
        this.inputId = inputId;
        this.factoryEditorModel = factoryEditorModel;
        this.httpClient = httpClient;
    }
    renderAttribute() {
        let content = document.createElement("div");
        let loadButton = document.createElement("button");
        loadButton.type = "button";
        loadButton.textContent = "view";
        loadButton.className = "btn btn-primary";
        loadButton.onclick = (e) => {
            this.httpClient.resolveViewRequest(this.factoryEditorModel.getFactory().getId(), this.attributeAccessor.getAttributeName(), this.factoryEditorModel.getFactory().getRoot(), (response) => {
                DomUtility.clear(content);
                content.appendChild(this.createFactoryItem(this.factoryEditorModel.getFactory().getRoot().getChildFromRoot(response.factoryId)));
            });
        };
        content.appendChild(loadButton);
        return content;
    }
    createFactoryItem(value) {
        let inputGroup = document.createElement("div");
        inputGroup.setAttribute("class", "input-group");
        let input = document.createElement("input");
        input.id = this.inputId.toString();
        input.className = "form-control";
        input.readOnly = true;
        input.value = value.getDisplayText();
        let inputGroupAppend = document.createElement("div");
        inputGroupAppend.className = "input-group-append";
        let editButton = document.createElement("button");
        editButton.type = "button";
        editButton.textContent = "edit";
        editButton.onclick = (e) => {
            this.factoryEditorModel.edit(value);
        };
        editButton.className = "btn btn-outline-secondary";
        inputGroupAppend.append(editButton);
        inputGroup.appendChild(input);
        inputGroup.appendChild(inputGroupAppend);
        inputGroup.style.paddingTop = "3px";
        return inputGroup;
    }
}
