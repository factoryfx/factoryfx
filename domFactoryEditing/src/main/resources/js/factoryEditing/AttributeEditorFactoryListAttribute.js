import { HttpUtility } from "./HttpUtility";
import { DomUtility } from "./DomUtility";
export class AttributeEditorFactoryListAttribute {
    constructor(attributeAccessor, inputId, factoryEditor, waitAnimation) {
        this.attributeAccessor = attributeAccessor;
        this.inputId = inputId;
        this.factoryEditor = factoryEditor;
        this.waitAnimation = waitAnimation;
        this.div = document.createElement("div");
    }
    create() {
        this.update();
        return this.div;
    }
    update() {
        DomUtility.clear(this.div);
        let values = this.attributeAccessor.getValue();
        if (!values) {
            values = [];
        }
        let ul = document.createElement("ul");
        ul.setAttribute("class", "list-group");
        for (let value of values) {
            let li = document.createElement("li");
            li.setAttribute("class", "list-group-item");
            li.appendChild(this.createListItem(value));
            ul.appendChild(li);
        }
        ul.style.marginBottom = "16px";
        ul.hidden = values.length === 0;
        let newButton = document.createElement("button");
        newButton.type = "button";
        newButton.textContent = "add";
        newButton.onclick = (e) => {
            let createRequestBody = {
                "factoryId": this.factoryEditor.getCurrentData().getId(),
                "attributeVariableName": this.attributeAccessor.getAttributeName(),
                "root": this.factoryEditor.getCurrentData().getRoot().mapToJsonFromRoot()
            };
            HttpUtility.post("createNewFactory", createRequestBody, this.waitAnimation, (response) => {
                this.attributeAccessor.getValue().push(this.factoryEditor.getCurrentData().createNewChildFactory(response));
                this.update();
                this.factoryEditor.updateTree();
            });
        };
        newButton.className = "btn btn-primary";
        this.div.appendChild(ul);
        this.div.appendChild(newButton);
    }
    createListItem(value) {
        let inputGroup = document.createElement("div");
        inputGroup.setAttribute("class", "input-group");
        let input = document.createElement("input");
        input.id = this.inputId.toString();
        input.className = "form-control";
        input.readOnly = true;
        input.ondblclick = (e) => {
            this.factoryEditor.edit(value);
        };
        if (value) {
            input.value = value.getDisplayText();
        }
        else {
            input.value = '';
        }
        let inputGroupAppend = document.createElement("div");
        inputGroupAppend.className = "input-group-append";
        let removeButton = document.createElement("button");
        removeButton.type = "button";
        removeButton.textContent = "remove";
        removeButton.onclick = (e) => {
            let array = this.attributeAccessor.getValue();
            array.splice(array.indexOf(value), 1);
            this.update();
            this.factoryEditor.updateTree();
        };
        removeButton.className = "btn btn-outline-danger";
        let editButton = document.createElement("button");
        editButton.textContent = "edit";
        editButton.onclick = (e) => {
            this.factoryEditor.edit(value);
        };
        if (!value) {
            editButton.disabled = true;
        }
        editButton.className = "btn btn-outline-secondary";
        inputGroupAppend.appendChild(removeButton);
        inputGroupAppend.appendChild(editButton);
        inputGroup.appendChild(input);
        inputGroup.appendChild(inputGroupAppend);
        return inputGroup;
    }
}
