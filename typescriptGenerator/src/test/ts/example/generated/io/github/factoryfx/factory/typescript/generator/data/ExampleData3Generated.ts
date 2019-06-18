//generated code don't edit manually
import { Data } from "../../../../../../../../util/Data";
import { AttributeAccessor } from "../../../../../../../../util/AttributeAccessor";
import { StaticAttributeValueAccessor } from "../../../../../../../../util/StaticAttributeValueAccessor";
import { DataCreator } from "../../../../../../../../util/DataCreator";
import { DynamicDataDictionary } from "../../../../../../../../util/DynamicDataDictionary";
import { AttributeType } from "../../../../../../../../util/AttributeType";
import { AttributeMetadata } from "../../../../../../../../util/AttributeMetadata";

export abstract class ExampleData3Generated  extends Data {

    public attribute: string = null;
    public static readonly attributeMetadata: AttributeMetadata<string>= new AttributeMetadata<string>('','',AttributeType.StringAttribute,false,[]);

    public attributeAccessor(): AttributeAccessor<string>{
        return new AttributeAccessor<string>(ExampleData3Generated.attributeMetadata,new StaticAttributeValueAccessor<string>(this,"attribute"),"attribute");
    }

    protected mapValuesFromJson(json: any, idToDataMap: any, dataCreator: DataCreator, dynamicDataDictionary: DynamicDataDictionary){
        this.attribute=json.attribute.v;
    }

    protected mapValuesToJson(idToDataMap: any, result: any){
        result.attribute=this.mapAttributeValueToJson(this.attribute);
    }

    protected collectChildrenFlat(): Data[]{
        let result: Array<Data>=[];
        return result;
    }

    public listAttributeAccessor(): AttributeAccessor<any>[]{
        let result: AttributeAccessor<any>[]=[];
        result.push(this.attributeAccessor());
        return result;
    }

}