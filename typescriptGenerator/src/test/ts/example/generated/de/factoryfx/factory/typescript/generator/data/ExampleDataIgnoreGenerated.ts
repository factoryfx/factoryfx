//generated code don't edit manually
import { AttributeAccessor } from "../../../../../../../util/AttributeAccessor";
import { DataCreator } from "../../../../../../../util/DataCreator";
import { AttributeType } from "../../../../../../../util/AttributeType";
import { Data } from "../../../../../../../util/Data";
import { AttributeMetadata } from "../../../../../../../util/AttributeMetadata";

export abstract class ExampleDataIgnoreGenerated  extends Data {

    public stringAttribute: string;
    public static readonly stringAttributeMetadata: AttributeMetadata<string>= new AttributeMetadata<string>('','',AttributeType.StringAttribute);

    public stringAttributeAccessor(): AttributeAccessor<string,ExampleDataIgnoreGenerated>{
        return new AttributeAccessor<string,ExampleDataIgnoreGenerated>(ExampleDataIgnoreGenerated.stringAttributeMetadata,this,"stringAttribute");
    }

    protected mapValuesFromJson(json: any, idToDataMap: any, dataCreator: DataCreator){
        this.stringAttribute=json.stringAttribute.v;
    }

    protected mapValuesToJson(idToDataMap: any, result: any){
        result.stringAttribute=this.mapAttributeValueToJson(this.stringAttribute);
    }

    protected collectChildrenRecursiveIntern(idToDataMap: any){
        
    }

    protected listAttributeAccessor(): AttributeAccessor<any,ExampleDataIgnoreGenerated>[]{
        let result: AttributeAccessor<any,ExampleDataIgnoreGenerated>[]=[];
        result.push(this.stringAttributeAccessor());
        return result;
    }

}