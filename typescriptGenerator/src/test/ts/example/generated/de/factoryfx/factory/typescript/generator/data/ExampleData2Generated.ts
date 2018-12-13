//generated code don't edit manually
import { AttributeType } from "../../../../../../../util/AttributeType";
import { Data } from "../../../../../../../util/Data";
import { AttributeAccessor } from "../../../../../../../util/AttributeAccessor";
import { AttributeMetadata } from "../../../../../../../util/AttributeMetadata";
import { DataCreator } from "../../../../../../../util/DataCreator";

export abstract class ExampleData2Generated  extends Data {

    public attribute: string;
    public static readonly attributeMetadata: AttributeMetadata<string>= new AttributeMetadata<string>('','',AttributeType.StringAttribute);

    public attributeAccessor(): AttributeAccessor<string,ExampleData2Generated>{
        return new AttributeAccessor<string,ExampleData2Generated>(ExampleData2Generated.attributeMetadata,this,"attribute");
    }

    protected mapValuesFromJson(json: any, idToDataMap: any, dataCreator: DataCreator){
        this.attribute=json.attribute.v;
    }

    protected mapValuesToJson(idToDataMap: any, result: any){
        result.attribute=this.mapAttributeValueToJson(this.attribute);
    }

    protected collectChildrenRecursiveIntern(idToDataMap: any){
        
    }

    protected listAttributeAccessor(): AttributeAccessor<any,ExampleData2Generated>[]{
        let result: AttributeAccessor<any,ExampleData2Generated>[]=[];
        result.push(this.attributeAccessor());
        return result;
    }

}