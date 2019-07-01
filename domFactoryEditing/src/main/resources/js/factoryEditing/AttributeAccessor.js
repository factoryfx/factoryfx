export class AttributeAccessor {
    constructor(attributeMetadata, attributeValues, attributeName) {
        this.attributeMetadata = attributeMetadata;
        this.attributeValues = attributeValues;
        this.attributeName = attributeName;
    }
    getValue() {
        return this.attributeValues[this.attributeName];
    }
    setValue(value) {
        this.attributeValues[this.attributeName] = value;
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
