import { HttpUtility } from "./HttpUtility";
import { DomUtility } from "./DomUtility";
export class AttributeEditorFactoryViewAttribute {
    constructor(attributeAccessor, inputId, factoryEditor, waitAnimation) {
        this.attributeAccessor = attributeAccessor;
        this.inputId = inputId;
        this.factoryEditor = factoryEditor;
        this.waitAnimation = waitAnimation;
    }
    create() {
        let content = document.createElement("div");
        let loadButton = document.createElement("button");
        loadButton.type = "button";
        loadButton.textContent = "view";
        loadButton.className = "btn btn-primary";
        loadButton.onclick = (e) => {
            let root = this.factoryEditor.getCurrentData().getRoot();
            let request = {
                factoryId: this.factoryEditor.getCurrentData().getId(),
                attributeVariableName: this.attributeAccessor.getAttributeName(),
                root: root.mapToJsonFromRoot(),
            };
            HttpUtility.post("resolveViewRequest", request, this.waitAnimation, (response) => {
                DomUtility.clear(content);
                content.appendChild(this.createFactoryItem(this.factoryEditor.getCurrentData().getRoot().getChildFromRoot(response.factoryId)));
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
            this.factoryEditor.edit(value);
        };
        editButton.className = "btn btn-outline-secondary";
        inputGroupAppend.append(editButton);
        inputGroup.appendChild(input);
        inputGroup.appendChild(inputGroupAppend);
        inputGroup.style.paddingTop = "3px";
        return inputGroup;
    }
}
