//generated code don't edit manually
import { Data } from "../../../../../../../../util/Data";
import { AttributeAccessor } from "../../../../../../../../util/AttributeAccessor";
import { StaticAttributeValueAccessor } from "../../../../../../../../util/StaticAttributeValueAccessor";
import { DataCreator } from "../../../../../../../../util/DataCreator";
import { DynamicDataDictionary } from "../../../../../../../../util/DynamicDataDictionary";
import { AttributeType } from "../../../../../../../../util/AttributeType";
import { AttributeMetadata } from "../../../../../../../../util/AttributeMetadata";

export abstract class ExampleDataIgnoreGenerated  extends Data {

    public stringAttribute: string = null;
    public static readonly stringAttributeMetadata: AttributeMetadata<string>= new AttributeMetadata<string>('','',AttributeType.StringAttribute,false,[]);

    public stringAttributeAccessor(): AttributeAccessor<string>{
        return new AttributeAccessor<string>(ExampleDataIgnoreGenerated.stringAttributeMetadata,new StaticAttributeValueAccessor<string>(this,"stringAttribute"),"stringAttribute");
    }

    protected mapValuesFromJson(json: any, idToDataMap: any, dataCreator: DataCreator, dynamicDataDictionary: DynamicDataDictionary){
        this.stringAttribute=json.stringAttribute.v;
    }

    protected mapValuesToJson(idToDataMap: any, result: any){
        result.stringAttribute=this.mapAttributeValueToJson(this.stringAttribute);
    }

    protected collectChildrenFlat(): Data[]{
        let result: Array<Data>=[];
        return result;
    }

    public listAttributeAccessor(): AttributeAccessor<any>[]{
        let result: AttributeAccessor<any>[]=[];
        result.push(this.stringAttributeAccessor());
        return result;
    }

}