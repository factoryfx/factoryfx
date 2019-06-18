import {Data} from "./Data";
import {AttributeMetadata} from "./AttributeMetadata";
import {AttributeType} from "./AttributeType";
import {DynamicData} from "./DynamicData";

export class DynamicDataDictionary{
    data: any;

    public createAttributeMetadata(javaClazz: string, property: string) {
        let dynamicDataDictionaryAttributeItem: any = this.data.classNameToItem[javaClazz].attributeNameToItem[property];
        return new AttributeMetadata<any>(dynamicDataDictionaryAttributeItem.en,dynamicDataDictionaryAttributeItem.de,dynamicDataDictionaryAttributeItem.type,dynamicDataDictionaryAttributeItem.nullable,dynamicDataDictionaryAttributeItem.possibleEnumValues);
    }

    public mapFromJson(json: any): void{
        this.data=json;
    }

    public createData(json: any, idToDataMap: any, parent: Data): Data{
        if (!json) return null;
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
            result.push(this.createData(entry,idToDataMap,parent));
        }
        return result;
    }
}