//generated code don't edit manually
import { DomUtility } from "../../../DomUtility";
import { AttributeEditorWidget } from "../AttributeEditorWidget";
export class FactoryListAttributeEditor extends AttributeEditorWidget {
    constructor(attributeAccessor, inputId, factoryEditorModel, httpClient) {
        super(attributeAccessor, inputId);
        this.attributeAccessor = attributeAccessor;
        this.inputId = inputId;
        this.factoryEditorModel = factoryEditorModel;
        this.httpClient = httpClient;
        this.div = document.createElement("div");
    }
    renderAttribute() {
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
            this.httpClient.createNewFactory(this.factoryEditorModel.getFactory().getId(), this.attributeAccessor.getAttributeName(), this.factoryEditorModel.getFactory().getRoot(), (response) => {
                this.attributeAccessor.getValue().push(this.factoryEditorModel.getFactory().createNewChildFactory(response));
                this.update();
                // this.factoryEditor.updateTree();
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
            this.factoryEditorModel.edit(value);
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
            // this.factoryEditor.updateTree();
        };
        removeButton.className = "btn btn-outline-danger";
        let editButton = document.createElement("button");
        editButton.textContent = "edit";
        editButton.onclick = (e) => {
            // this.factoryEditor.edit(value);
            this.factoryEditorModel.edit(value);
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
