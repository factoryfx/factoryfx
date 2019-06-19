export class AttributeEditorEnumAttribute {
    constructor(attributeAccessor, inputId) {
        this.attributeAccessor = attributeAccessor;
        this.inputId = inputId;
    }
    create() {
        let select = document.createElement("select");
        select.id = this.inputId.toString();
        select.className = "form-control";
        let value = this.attributeAccessor.getValue();
        select.value = value;
        select.oninput = (e) => {
            this.attributeAccessor.setValue(select.value);
        };
        for (let possibleValue of this.attributeAccessor.getAttributeMetadata().getPossibleEnumValues()) {
            let option = document.createElement("option");
            option.textContent = possibleValue;
            if (value === possibleValue) {
                option.selected = true;
            }
            select.options.add(option);
        }
        let option = document.createElement("option");
        option.value = null;
        option.textContent = 'empty';
        if (value === null || value === undefined) {
            option.selected = true;
        }
        select.options.add(option);
        return select;
    }
}
