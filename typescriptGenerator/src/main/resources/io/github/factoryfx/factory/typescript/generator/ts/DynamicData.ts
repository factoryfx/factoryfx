//generated code don't edit manually
import {DataCreator} from "./DataCreator";
import {AttributeAccessor} from "./AttributeAccessor";
import {Data} from "./Data";
import {AttributeMetadata} from "./AttributeMetadata";
import {AttributeType} from "./AttributeType";
import {DynamicDataDictionary} from "./DynamicDataDictionary";
import {AttributeMetadataAndAttributeName} from "./AttributeMetadataAndAttributeName";

export class DynamicData extends Data {
    attributeAccessors: AttributeAccessor<any>[];
    attributeValues: any;

    protected collectChildrenFlat(): Array<Data> {
        let result: Array<Data>=[];

        let attributeMetadataArray:  AttributeMetadataAndAttributeName[] = this.dynamicDataDictionary.createAttributeMetadataArray(this.javaClass);
        for (let attributeMetadata of attributeMetadataArray){
            let attributeType: AttributeType = attributeMetadata.attributeMetadata.getType();
            if (attributeType===AttributeType.FactoryListAttribute || attributeType===AttributeType.FactoryPolymorphicListAttribute){
                for (let data of this.attributeValues[attributeMetadata.attributeName]) {
                    result.push(data);
                }
            }
            if (attributeType===AttributeType.FactoryAttribute || attributeType===AttributeType.FactoryPolymorphicAttribute){
                if (this.attributeValues[attributeMetadata.attributeName]){
                    result.push(this.attributeValues[attributeMetadata.attributeName]);
                }
            }
        }
        return result;
    }

    getDisplayText(): string {
        let splits = this.javaClass.split(".");
        let displayText = splits[splits.length-1];
        if (displayText.endsWith("Factory")){
            displayText=displayText.slice(0, -7);
        }
        return displayText
        // insert a space before all caps
            .replace(/([A-Z])/g, ' $1');
    }

    listAttributeAccessor(): AttributeAccessor<any>[] {
        return this.attributeAccessors;
    }

    dynamicDataDictionary: DynamicDataDictionary;
    protected mapValuesFromJson(json: any, idToDataMap: any, dataCreator: DataCreator, dynamicDataDictionary: DynamicDataDictionary) {
        this.attributeAccessors=[];
        this.attributeValues={};
        this.dynamicDataDictionary=dynamicDataDictionary;


        let attributeMetadataArray:  AttributeMetadataAndAttributeName[] = dynamicDataDictionary.createAttributeMetadataArray(this.javaClass);
        for (let attributeMetadata of attributeMetadataArray){
            this.attributeAccessors.push(new AttributeAccessor<any>(attributeMetadata.attributeMetadata,this.attributeValues,attributeMetadata.attributeName));
            let attributeType: AttributeType = attributeMetadata.attributeMetadata.getType();
            if (attributeType===AttributeType.FactoryAttribute || attributeType===AttributeType.FactoryPolymorphicAttribute){
                this.attributeValues[attributeMetadata.attributeName]=dynamicDataDictionary.createData(json[attributeMetadata.attributeName].v,idToDataMap,this);
                continue;
            }
            if (attributeType===AttributeType.FactoryListAttribute || attributeType===AttributeType.FactoryPolymorphicListAttribute){
                this.attributeValues[attributeMetadata.attributeName]=dynamicDataDictionary.createDataList(json[attributeMetadata.attributeName],idToDataMap,this);
                continue;
            }
            if (attributeType===AttributeType.FactoryViewAttribute || attributeType===AttributeType.FactoryViewListAttribute){
                continue;
            }
            if (Array.isArray(json[attributeMetadata.attributeName])){
                this.attributeValues[attributeMetadata.attributeName]=json[attributeMetadata.attributeName];
                continue;
            }
            this.attributeValues[attributeMetadata.attributeName]=this.getValue(json[attributeMetadata.attributeName].v,attributeType);
        }
    }

    private getValue(jsonValue: any, attributeType: AttributeType): any {
        if (jsonValue===null || jsonValue === undefined){
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

    private writeValue(value: any, attributeType: AttributeType): any {
        if (value===null || value === undefined){
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

    protected mapValuesToJson(idToDataMap: any, result: any) {
        for (let attributeAccessor of this.attributeAccessors) {

            let type: AttributeType= attributeAccessor.getAttributeMetadata().getType();

            if (type===AttributeType.FactoryAttribute || type===AttributeType.FactoryPolymorphicAttribute) {
                result[attributeAccessor.getAttributeName()]=this.mapAttributeDataToJson(idToDataMap, attributeAccessor.getValue());
                continue;
            }
            if (type===AttributeType.FactoryListAttribute || type===AttributeType.FactoryPolymorphicListAttribute) {
                result[attributeAccessor.getAttributeName()]=this.mapAttributeDataListToJson(idToDataMap, attributeAccessor.getValue());
                continue;
            }
            if (this.isValueListAttribute(type)){
                result[attributeAccessor.getAttributeName()]=attributeAccessor.getValue();
                continue;
            }
            result[attributeAccessor.getAttributeName()]=this.mapAttributeValueToJson(this.writeValue(attributeAccessor.getValue(),type));

        }
    }


    private isValueListAttribute(type: AttributeType) {
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

    createNewChildFactory(json: any): Data{
        let idToDataMap = {};
        this.collectChildrenRecursive(idToDataMap);
        return this.dynamicDataDictionary.createData(json,idToDataMap,this);
    }
}
