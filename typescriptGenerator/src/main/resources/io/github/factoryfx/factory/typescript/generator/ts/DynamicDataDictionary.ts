import {Data} from "./Data";
import {AttributeMetadata} from "./AttributeMetadata";
import {AttributeType} from "./AttributeType";
import {DynamicData} from "./DynamicData";
import {AttributeMetadataAndAttributeName} from "./AttributeMetadataAndAttributeName";

export class DynamicDataDictionary{
    data: any;

    public createAttributeMetadataArray(javaClazz: string): AttributeMetadataAndAttributeName[] {
        let result: AttributeMetadataAndAttributeName[] = [];

        let attributeNameToItem = this.data.classNameToItem[javaClazz].attributeNameToItem;
        for (var attributeName in attributeNameToItem) {
            let dynamicDataDictionaryAttributeItem = attributeNameToItem[attributeName];
            if (attributeNameToItem.hasOwnProperty(attributeName)) {
                result.push(new AttributeMetadataAndAttributeName(
                    new AttributeMetadata<any>(
                        dynamicDataDictionaryAttributeItem.en,
                        dynamicDataDictionaryAttributeItem.de,
                        dynamicDataDictionaryAttributeItem.type,
                        dynamicDataDictionaryAttributeItem.nullable,
                        dynamicDataDictionaryAttributeItem.possibleEnumValues
                    ),attributeName)
                );
            }
        }
        return result;

    }

    public mapFromJson(json: any): void{
        this.data=json;
    }

    public createData(json: any, idToDataMap: any, parent: Data): Data{
        let clazz=json['@class'];
        if (typeof json === 'string'){
            return idToDataMap[json];
        }

        let result: DynamicData= new DynamicData();
        result.mapFromJson(json,idToDataMap,null,this);
        result.setParent(parent);
        return result;
    }

    public createDataList(json: any, idToDataMap: any, parent: Data): Data[]{
        let result: Data[]=[];
        for (let entry of json) {
            let newData = this.createData(entry,idToDataMap,parent);
            if (newData){
                result.push(newData);
            }
        }
        return result;
    }
}