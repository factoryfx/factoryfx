import { AttributeAccessor } from "./AttributeAccessor";
import { Data } from "./Data";
import { StaticAttributeValueAccessor } from "./StaticAttributeValueAccessor";
import { AttributeType } from "./AttributeType";
export class DynamicData extends Data {
    collectChildrenFlat() {
        return undefined;
    }
    getDisplayText() {
        let splits = this.javaClass.split(".");
        let displayText = splits[splits.length - 1];
        return displayText
            // insert a space before all caps
            .replace(/([A-Z])/g, ' $1');
    }
    listAttributeAccessor() {
        return this.attributeAccessors;
    }
    mapValuesFromJson(json, idToDataMap, dataCreator, dynamicDataDictionary) {
        this.attributeAccessors = [];
        this.attributeValues = {};
        for (let property in json) {
            if (json.hasOwnProperty(property)) {
                if (property !== '@class' && property !== 'treeBuilderName' && property !== 'id') {
                    this.attributeValues[property] = property['v'];
                    let attributeMetadata = dynamicDataDictionary.createAttributeMetadata(this.javaClass, property);
                    let attributeType = attributeMetadata.getType();
                    if (Array.isArray(json[property])) {
                        if (attributeType === AttributeType.FactoryListAttribute || attributeType === AttributeType.FactoryPolymorphicListAttribute) {
                            this.attributeValues[property] = dynamicDataDictionary.createDataList(json[property], idToDataMap, this);
                        }
                        else {
                            this.attributeValues[property] = json[property];
                        }
                    }
                    else {
                        if (attributeType === AttributeType.FactoryAttribute || attributeType === AttributeType.FactoryPolymorphicAttribute) {
                            this.attributeValues[property] = dynamicDataDictionary.createData(json[property].v, idToDataMap, this);
                        }
                        else {
                            this.attributeValues[property] = this.getValue(json[property].v, attributeType);
                        }
                    }
                    this.attributeAccessors.push(new AttributeAccessor(attributeMetadata, new StaticAttributeValueAccessor(this.attributeValues, property), property));
                }
            }
        }
    }
    getValue(jsonValue, attributeType) {
        if (jsonValue === null || jsonValue === undefined) {
            return null;
        }
        if (attributeType === AttributeType.LocalDateTimeAttribute) {
            return this.mapLocalDateTimeFromJson(jsonValue);
        }
        if (attributeType === AttributeType.LocalDateAttribute) {
            return this.mapLocalDateFromJson(jsonValue);
        }
        if (attributeType === AttributeType.InstantAttribute) {
            return this.mapInstantFromJson(jsonValue);
        }
        return jsonValue;
    }
    writeValue(value, attributeType) {
        if (value === null || value === undefined) {
            return null;
        }
        if (attributeType === AttributeType.LocalDateTimeAttribute) {
            return this.mapLocalDateTimeToJson(value);
        }
        if (attributeType === AttributeType.LocalDateAttribute) {
            return this.mapLocalDateToJson(value);
        }
        if (attributeType === AttributeType.InstantAttribute) {
            return this.mapInstantToJson(value);
        }
        return value;
    }
    mapValuesToJson(idToDataMap, result) {
        for (let attributeAccessor of this.attributeAccessors) {
            let type = attributeAccessor.getAttributeMetadata().getType();
            if (type === AttributeType.FactoryAttribute || type === AttributeType.FactoryPolymorphicAttribute) {
                result[attributeAccessor.getAttributeName()] = this.mapAttributeDataToJson(idToDataMap, attributeAccessor.getValue());
                continue;
            }
            if (type === AttributeType.FactoryListAttribute || type === AttributeType.FactoryPolymorphicListAttribute) {
                result[attributeAccessor.getAttributeName()] = this.mapAttributeDataListToJson(idToDataMap, attributeAccessor.getValue());
                continue;
            }
            if (this.isValueListAttribute(type)) {
                result[attributeAccessor.getAttributeName()] = attributeAccessor.getValue();
                continue;
            }
            result[attributeAccessor.getAttributeName()] = this.mapAttributeValueToJson(this.writeValue(attributeAccessor.getValue(), type));
        }
    }
    isValueListAttribute(type) {
        return type === AttributeType.URIListAttribute ||
            type === AttributeType.EnumListAttribute ||
            type === AttributeType.LongListAttribute ||
            type === AttributeType.CharListAttribute ||
            type === AttributeType.IntegerListAttribute ||
            type === AttributeType.DoubleListAttribute ||
            type === AttributeType.ShortListAttribute ||
            type === AttributeType.FloatListAttribute ||
            type === AttributeType.StringListAttribute ||
            type === AttributeType.ByteListAttribute;
    }
}
