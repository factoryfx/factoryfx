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
            });
        };
        newButton.className = "btn btn-primary";
        this.div.appendChild(ul);
        this.div.appendChild(newButton);
    }
    createList(values) {
        let ul = document.createElement("ul");
        ul.setAttribute("class", "list-group");
        for (let value of values) {
            let li = document.createElement("li");
            li.setAttribute("class", "list-group-item");
            li.appendChild(this.createListItem(value));
            ul.appendChild(li);
        }
        return ul;
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
        let button = document.createElement("button");
        button.textContent = "edit";
        button.onclick = (e) => {
            this.factoryEditor.edit(value);
        };
        if (!value) {
            button.disabled = true;
        }
        button.className = "btn btn-outline-secondary";
        inputGroupAppend.appendChild(button);
        inputGroup.appendChild(input);
        inputGroup.appendChild(inputGroupAppend);
        return inputGroup;
    }
}
