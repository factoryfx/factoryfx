export class AttributeAccessor {
    constructor(attributeMetadata, valueAccessor, attributeName) {
        this.attributeMetadata = attributeMetadata;
        this.valueAccessor = valueAccessor;
        this.attributeName = attributeName;
    }
    getValue() {
        return this.valueAccessor.getValue();
    }
    setValue(value) {
        this.valueAccessor.setValue(value);
    }
    getLabelText(locale) {
        let labelText = this.attributeMetadata.getLabelText(locale);
        if (!labelText) {
            labelText = this.attributeName;
        }
        return labelText;
    }
    getAttributeMetadata() {
        return this.attributeMetadata;
    }
    getAttributeName() {
        return this.attributeName;
    }
}
