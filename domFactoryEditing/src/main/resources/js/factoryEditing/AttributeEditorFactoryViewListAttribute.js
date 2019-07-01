import { HttpUtility } from "./HttpUtility";
import { DomUtility } from "./DomUtility";
export class AttributeEditorFactoryViewListAttribute {
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
            HttpUtility.post("resolveViewListRequest", request, this.waitAnimation, (response) => {
                DomUtility.clear(content);
                content.appendChild(this.createFactoryList(response));
            });
        };
        content.appendChild(loadButton);
        return content;
    }
    createFactoryList(factoryIds) {
        let root = this.factoryEditor.getCurrentData().getRoot();
        let ul = document.createElement("ul");
        ul.setAttribute("class", "list-group");
        for (let factoryId of factoryIds) {
            let li = document.createElement("li");
            li.setAttribute("class", "list-group-item");
            li.appendChild(this.createFactoryListItem(root.getChildFromRoot(factoryId)));
            ul.appendChild(li);
        }
        ul.style.paddingTop = "3px";
        return ul;
    }
    createFactoryListItem(value) {
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
        return inputGroup;
    }
}
