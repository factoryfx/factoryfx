import { AttributeEditorWidget } from "../AttributeEditorWidget";
import { BootstrapUtility } from "../../../BootstrapUtility";
import { FactorySelectDialog } from "../../../utility/FactorySelectDialog";
export class FactoryListAttributeEditor extends AttributeEditorWidget {
    constructor(attributeAccessor, inputId, factoryEditorModel, httpClient) {
        super(attributeAccessor, inputId);
        this.attributeAccessor = attributeAccessor;
        this.inputId = inputId;
        this.factoryEditorModel = factoryEditorModel;
        this.httpClient = httpClient;
    }
    render() {
        let div = document.createElement("div");
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
        let newButton = BootstrapUtility.createButtonPrimary();
        newButton.textContent = "add";
        newButton.onclick = (e) => {
            this.httpClient.createNewFactories(this.factoryEditorModel.getFactory().getId(), this.attributeAccessor.getAttributeName(), this.factoryEditorModel.getFactory().getRoot(), (possibleValues) => {
                if (possibleValues.length > 1) {
                    let dialog = new FactorySelectDialog(newButton.parentElement, possibleValues, (selected) => {
                        let items = this.attributeAccessor.getValue();
                        items.push(selected);
                        this.attributeAccessor.setValue(items);
                    });
                    dialog.show();
                }
                else {
                    let items = this.attributeAccessor.getValue();
                    items.push(possibleValues[0]);
                    this.attributeAccessor.setValue(items);
                }
                // this.factoryEditor.updateTree();
            });
        };
        div.appendChild(ul);
        div.appendChild(newButton);
        return div;
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
            let items = this.attributeAccessor.getValue();
            items.splice(items.indexOf(value), 1);
            this.attributeAccessor.setValue(items);
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
