export class StaticAttributeValueAccessor {
    constructor(attributeParent, attributeName) {
        this.attributeParent = attributeParent;
        this.attributeName = attributeName;
        this.attributeParent = attributeParent;
        this.attributeName = attributeName;
    }
    getValue() {
        return this.attributeParent[this.attributeName];
    }
    setValue(value) {
        this.attributeParent[this.attributeName] = value;
    }
}
