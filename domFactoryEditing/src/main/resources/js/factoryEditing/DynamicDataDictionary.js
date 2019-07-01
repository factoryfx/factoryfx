import { AttributeMetadata } from "./AttributeMetadata";
import { DynamicData } from "./DynamicData";
import { AttributeMetadataAndAttributeName } from "./AttributeMetadataAndAttributeName";
export class DynamicDataDictionary {
    createAttributeMetadataArray(javaClazz) {
        let result = [];
        Object.entries(this.data.classNameToItem[javaClazz].attributeNameToItem).forEach(([attributeName, value]) => {
            let dynamicDataDictionaryAttributeItem = value;
            result.push(new AttributeMetadataAndAttributeName(new AttributeMetadata(dynamicDataDictionaryAttributeItem.en, dynamicDataDictionaryAttributeItem.de, dynamicDataDictionaryAttributeItem.type, dynamicDataDictionaryAttributeItem.nullable, dynamicDataDictionaryAttributeItem.possibleEnumValues), attributeName));
        });
        return result;
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
