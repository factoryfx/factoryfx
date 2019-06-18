//generated code don't edit manually
import {DataCreator} from "./DataCreator";
import {AttributeAccessor} from "./AttributeAccessor";
import {Data} from "./Data";
import {StaticAttributeValueAccessor} from "./StaticAttributeValueAccessor";
import {AttributeMetadata} from "./AttributeMetadata";
import {AttributeType} from "./AttributeType";
import {DynamicDataDictionary} from "./DynamicDataDictionary";

export class DynamicData extends Data {
    attributeAccessors: AttributeAccessor<any>[];
    attributeValues: any;

    protected collectChildrenFlat(): Array<Data> {
        return undefined;
    }

    getDisplayText(): string {
        let splits = this.javaClass.split(".");
        let displayText = splits[splits.length-1];
        return displayText
        // insert a space before all caps
            .replace(/([A-Z])/g, ' $1');
    }

    listAttributeAccessor(): AttributeAccessor<any>[] {
        return this.attributeAccessors;
    }

    protected mapValuesFromJson(json: any, idToDataMap: any, dataCreator: DataCreator, dynamicDataDictionary: DynamicDataDictionary) {
        this.attributeAccessors=[];
        this.attributeValues={};

        for (let property in json) {
            if (json.hasOwnProperty(property)) {
                if (property!=='@class' && property!=='treeBuilderName' && property!=='id') {
                    this.attributeValues[property]=property['v'];
                    let attributeMetadata: AttributeMetadata<any> = dynamicDataDictionary.createAttributeMetadata(this.javaClass,property);
                    let attributeType: AttributeType = attributeMetadata.getType();
                    if (Array.isArray(json[property])){
                        if (attributeType===AttributeType.FactoryListAttribute || attributeType===AttributeType.FactoryPolymorphicListAttribute){
                            this.attributeValues[property]=dynamicDataDictionary.createDataList(json[property],idToDataMap,this);
                        } else {
                            this.attributeValues[property]=json[property];
                        }
                    } else {
                        if (attributeType===AttributeType.FactoryAttribute || attributeType===AttributeType.FactoryPolymorphicAttribute){
                            this.attributeValues[property]=dynamicDataDictionary.createData(json[property].v,idToDataMap,this);
                        } else {
                            this.attributeValues[property]=this.getValue(json[property].v,attributeType);
                        }
                    }
                    this.attributeAccessors.push(new AttributeAccessor<any>(attributeMetadata,new StaticAttributeValueAccessor(this.attributeValues,property),property));

                }
            }
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
}
