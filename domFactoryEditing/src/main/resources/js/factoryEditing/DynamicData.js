import { AttributeAccessor } from "./AttributeAccessor";
import { Data } from "./Data";
import { AttributeType } from "./AttributeType";
export class DynamicData extends Data {
    collectChildrenFlat() {
        let result = [];
        let attributeMetadataArray = this.dynamicDataDictionary.createAttributeMetadataArray(this.javaClass);
        for (let attributeMetadata of attributeMetadataArray) {
            let attributeType = attributeMetadata.attributeMetadata.getType();
            if (attributeType === AttributeType.FactoryListAttribute || attributeType === AttributeType.FactoryPolymorphicListAttribute) {
                for (let data of this.attributeValues[attributeMetadata.attributeName]) {
                    result.push(data);
                }
            }
            if (attributeType === AttributeType.FactoryAttribute || attributeType === AttributeType.FactoryPolymorphicAttribute) {
                if (this.attributeValues[attributeMetadata.attributeName]) {
                    result.push(this.attributeValues[attributeMetadata.attributeName]);
                }
            }
        }
        return result;
    }
    getDisplayText() {
        let splits = this.javaClass.split(".");
        let displayText = splits[splits.length - 1];
        if (displayText.endsWith("Factory")) {
            displayText = displayText.slice(0, -7);
        }
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
        this.dynamicDataDictionary = dynamicDataDictionary;
        let attributeMetadataArray = dynamicDataDictionary.createAttributeMetadataArray(this.javaClass);
        for (let attributeMetadata of attributeMetadataArray) {
            this.attributeAccessors.push(new AttributeAccessor(attributeMetadata.attributeMetadata, this.attributeValues, attributeMetadata.attributeName));
            let attributeType = attributeMetadata.attributeMetadata.getType();
            if (attributeType === AttributeType.FactoryAttribute || attributeType === AttributeType.FactoryPolymorphicAttribute) {
                let jsonChildValue = json[attributeMetadata.attributeName].v;
                if (jsonChildValue) {
                    this.attributeValues[attributeMetadata.attributeName] = dynamicDataDictionary.createData(jsonChildValue, idToDataMap, this);
                }
                else {
                    this.attributeValues[attributeMetadata.attributeName] = null;
                }
                continue;
            }
            if (attributeType === AttributeType.FactoryListAttribute || attributeType === AttributeType.FactoryPolymorphicListAttribute) {
                this.attributeValues[attributeMetadata.attributeName] = dynamicDataDictionary.createDataList(json[attributeMetadata.attributeName], idToDataMap, this);
                continue;
            }
            if (attributeType === AttributeType.FactoryViewAttribute || attributeType === AttributeType.FactoryViewListAttribute) {
                continue;
            }
            if (Array.isArray(json[attributeMetadata.attributeName])) {
                this.attributeValues[attributeMetadata.attributeName] = json[attributeMetadata.attributeName];
                continue;
            }
            this.attributeValues[attributeMetadata.attributeName] = this.getValue(json[attributeMetadata.attributeName].v, attributeType);
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
        if (attributeType === AttributeType.LongAttribute) {
            return this.mapLongFromJson(jsonValue);
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
        if (attributeType === AttributeType.LongAttribute) {
            return this.mapLongToJson(value);
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
    createNewChildFactory(json) {
        let idToDataMap = {};
        this.collectChildrenRecursive(idToDataMap);
        return this.dynamicDataDictionary.createData(json, idToDataMap, this);
    }
}
