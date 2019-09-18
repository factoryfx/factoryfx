import { AttributeEditorWidget } from "../AttributeEditorWidget";
import { DomUtility } from "../../../DomUtility";
export class StringListAttributeEditor extends AttributeEditorWidget {
    constructor(attributeAccessor, inputId) {
        super(attributeAccessor, inputId);
        this.attributeAccessor = attributeAccessor;
        this.inputId = inputId;
        this.ul = document.createElement("ul");
        this.ul.style.paddingLeft = "0px";
    }
    render() {
        let div = document.createElement("div");
        this.bindValues();
        let newButton = document.createElement("button");
        newButton.type = "button";
        newButton.textContent = "add";
        newButton.onclick = (e) => {
            let values = this.attributeAccessor.getValue();
            values.push("");
            this.attributeAccessor.setValue(values);
            this.bindValues();
        };
        newButton.className = "btn btn-primary";
        div.appendChild(this.ul);
        div.appendChild(newButton);
        return div;
    }
    bindValues() {
        DomUtility.clear(this.ul);
        let values = this.attributeAccessor.getValue();
        let counter = 0;
        for (let value of values) {
            let li = document.createElement("li");
            li.setAttribute("class", "list-group-item");
            li.appendChild(this.createListItem(value, counter));
            this.ul.appendChild(li);
            counter++;
        }
    }
    createListItem(value, index) {
        let inputGroup = document.createElement("div");
        inputGroup.className = "input-group";
        let input = document.createElement("input");
        input.id = this.inputId.toString();
        input.className = "form-control";
        input.type = "text";
        input.value = value;
        input.oninput = (e) => {
            let values = this.attributeAccessor.getValue();
            values[index] = input.value;
        };
        let inputGroupAppend = document.createElement("div");
        inputGroupAppend.className = "input-group-append";
        let button = document.createElement("button");
        button.type = "button";
        button.textContent = "remove";
        button.onclick = (e) => {
            let values = this.attributeAccessor.getValue();
            values.splice(values.indexOf(value), 1);
            this.attributeAccessor.setValue(values);
            this.bindValues();
        };
        button.className = "btn btn-outline-danger";
        inputGroupAppend.appendChild(button);
        inputGroup.appendChild(input);
        inputGroup.appendChild(inputGroupAppend);
        return inputGroup;
    }
}
