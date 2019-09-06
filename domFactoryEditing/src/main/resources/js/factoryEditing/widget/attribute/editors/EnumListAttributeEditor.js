import { AttributeEditorWidget } from "../AttributeEditorWidget";
export class EnumListAttributeEditor extends AttributeEditorWidget {
    constructor(attributeAccessor, inputId) {
        super(attributeAccessor, inputId);
        this.attributeAccessor = attributeAccessor;
        this.inputId = inputId;
    }
    renderAttribute() {
        let select = document.createElement("select");
        select.id = this.inputId.toString();
        select.className = "form-control";
        select.multiple = true;
        let values = this.attributeAccessor.getValue();
        let onchangeEvent = (e) => {
            let collection = select.selectedOptions;
            let selectedValues = [];
            for (let i = 0; i < collection.length; i++) {
                selectedValues.push(collection[i].value);
            }
            this.attributeAccessor.setValue(selectedValues);
        };
        select.onchange = onchangeEvent;
        for (let possibleValue of this.attributeAccessor.getAttributeMetadata().getPossibleEnumValues()) {
            let option = document.createElement("option");
            option.value = possibleValue;
            if (values.includes(possibleValue)) {
                option.selected = true;
            }
            option.onmousedown = (e) => {
                e.preventDefault();
                option.selected = !option.selected;
                this.updateText(option, possibleValue);
                onchangeEvent(e);
                return false;
            };
            this.updateText(option, possibleValue);
            select.options.add(option);
        }
        return select;
    }
    updateText(option, possibleValue) {
        if (option.selected) {
            option.textContent = "\u2611 " + possibleValue;
        }
        else {
            option.textContent = "\u2610 " + possibleValue;
        }
    }
}
