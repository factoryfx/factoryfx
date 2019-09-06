import { ValidationError } from "./ValidationError";
export class AttributeMetadata {
    constructor(en, de, attributeType, isNullable, possibleEnumValues) {
        this.en = en;
        this.de = de;
        this.attributeType = attributeType;
        this.isNullable = isNullable;
        this.possibleEnumValues = possibleEnumValues;
        this.attributeType = attributeType;
    }
    getLabelText(locale) {
        if (locale === 'de') {
            return this.de;
        }
        return this.en;
    }
    validation(validationFunction) {
        this.validationFunction = validationFunction;
    }
    validate(value) {
        if (!this.isNullable && !value) {
            let workaround = value;
            if (!value && workaround !== 0) {
                return new ValidationError("Required", "Pflichtfeld");
            }
        }
        if (!this.validationFunction) {
            return null;
        }
        return this.validationFunction(value);
    }
    nullable() {
        return this.isNullable;
    }
    getType() {
        return this.attributeType;
    }
    getPossibleEnumValues() {
        return this.possibleEnumValues;
    }
}
