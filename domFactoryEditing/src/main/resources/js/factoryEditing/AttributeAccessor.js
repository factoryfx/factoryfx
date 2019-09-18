export class AttributeAccessor {
    constructor(attributeMetadata, attributeValues, attributeName) {
        this.attributeMetadata = attributeMetadata;
        this.attributeValues = attributeValues;
        this.attributeName = attributeName;
        this.listeners = [];
    }
    getValue() {
        return this.attributeValues[this.attributeName];
    }
    setValue(value) {
        this.attributeValues[this.attributeName] = value;
        for (let listener of this.listeners) {
            listener();
        }
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
    addChangeListener(listener) {
        if (!this.listeners.includes(listener)) {
            this.listeners.push(listener);
        }
    }
    removeChangeListener(listener) {
        this.listeners.splice(this.listeners.indexOf(listener), 1);
    }
}
