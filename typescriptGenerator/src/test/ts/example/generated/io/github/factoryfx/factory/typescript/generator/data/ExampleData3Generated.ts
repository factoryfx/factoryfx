//generated code don't edit manually
import { Data } from "../../../../../../../../util/Data";
import { AttributeAccessor } from "../../../../../../../../util/AttributeAccessor";
import { DataCreator } from "../../../../../../../../util/DataCreator";
import { AttributeType } from "../../../../../../../../util/AttributeType";
import { AttributeMetadata } from "../../../../../../../../util/AttributeMetadata";

export abstract class ExampleData3Generated  extends Data {

    public attribute: string = null;
    public static readonly attributeMetadata: AttributeMetadata<string>= new AttributeMetadata<string>('','',AttributeType.StringAttribute);

    public attributeAccessor(): AttributeAccessor<string,ExampleData3Generated>{
        return new AttributeAccessor<string,ExampleData3Generated>(ExampleData3Generated.attributeMetadata,this,"attribute");
    }

    protected mapValuesFromJson(json: any, idToDataMap: any, dataCreator: DataCreator){
        this.attribute=json.attribute.v;
    }

    protected mapValuesToJson(idToDataMap: any, result: any){
        result.attribute=this.mapAttributeValueToJson(this.attribute);
    }

    protected collectChildrenFlat(): Data[]{
        let result: Array<Data>=[];
        return result;
    }

    public listAttributeAccessor(): AttributeAccessor<any,ExampleData3Generated>[]{
        let result: AttributeAccessor<any,ExampleData3Generated>[]=[];
        result.push(this.attributeAccessor());
        return result;
    }

}