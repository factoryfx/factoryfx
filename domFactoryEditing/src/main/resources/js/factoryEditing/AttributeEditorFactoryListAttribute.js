export class AttributeEditorFactoryListAttribute {
    constructor(attributeAccessor, inputId, factoryEditor) {
        this.attributeAccessor = attributeAccessor;
        this.inputId = inputId;
        this.factoryEditor = factoryEditor;
    }
    create() {
        let values = this.attributeAccessor.getValue();
        if (!values) {
            values = [];
        }
        let div = document.createElement("div");
        let ul = document.createElement("ul");
        ul.setAttribute("class", "list-group");
        for (let value of values) {
            let li = document.createElement("li");
            li.setAttribute("class", "list-group-item");
            li.appendChild(this.createListItem(value));
            ul.appendChild(li);
        }
        div.appendChild(ul);
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
