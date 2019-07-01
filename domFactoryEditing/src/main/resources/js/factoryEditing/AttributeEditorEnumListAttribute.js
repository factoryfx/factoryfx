export class AttributeEditorEnumListAttribute {
    constructor(attributeAccessor, inputId) {
        this.attributeAccessor = attributeAccessor;
        this.inputId = inputId;
    }
    create() {
        let select = document.createElement("select");
        select.id = this.inputId.toString();
        select.className = "form-control";
        select.multiple = true;
        let values = this.attributeAccessor.getValue();
        select.onchange = (e) => {
            let collection = select.selectedOptions;
            let selectedValues = [];
            for (let i = 0; i < collection.length; i++) {
                selectedValues.push(collection[i].value);
            }
            this.attributeAccessor.setValue(selectedValues);
        };
        for (let possibleValue of this.attributeAccessor.getAttributeMetadata().getPossibleEnumValues()) {
            let option = document.createElement("option");
            option.textContent = possibleValue;
            option.value = possibleValue;
            if (values.includes(possibleValue)) {
                option.selected = true;
            }
            select.options.add(option);
        }
        return select;
    }
}
