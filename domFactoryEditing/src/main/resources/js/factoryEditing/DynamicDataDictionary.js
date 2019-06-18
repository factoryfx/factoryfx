import { AttributeMetadata } from "./AttributeMetadata";
import { DynamicData } from "./DynamicData";
export class DynamicDataDictionary {
    createAttributeMetadata(javaClazz, property) {
        let dynamicDataDictionaryAttributeItem = this.data.classNameToItem[javaClazz].attributeNameToItem[property];
        return new AttributeMetadata(dynamicDataDictionaryAttributeItem.en, dynamicDataDictionaryAttributeItem.de, dynamicDataDictionaryAttributeItem.type, dynamicDataDictionaryAttributeItem.nullable, dynamicDataDictionaryAttributeItem.possibleEnumValues);
    }
    mapFromJson(json) {
        this.data = json;
    }
    createData(json, idToDataMap, parent) {
        if (!json)
            return null;
        let clazz = json['@class'];
        if (typeof json === 'string') {
            return idToDataMap[json];
        }
        let result = new DynamicData();
        result.mapFromJson(json, idToDataMap, null, this);
        result.setParent(parent);
        return result;
    }
    createDataList(json, idToDataMap, parent) {
        let result = [];
        for (let entry of json) {
            result.push(this.createData(entry, idToDataMap, parent));
        }
        return result;
    }
}
