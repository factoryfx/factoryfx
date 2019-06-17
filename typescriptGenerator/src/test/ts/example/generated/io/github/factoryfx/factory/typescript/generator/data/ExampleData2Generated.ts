//generated code don't edit manually
import { Data } from "../../../../../../../../util/Data";
import { AttributeAccessor } from "../../../../../../../../util/AttributeAccessor";
import { ExampleData3 } from "../../../../../../../../config/io/github/factoryfx/factory/typescript/generator/data/ExampleData3";
import { DataCreator } from "../../../../../../../../util/DataCreator";
import { AttributeType } from "../../../../../../../../util/AttributeType";
import { AttributeMetadata } from "../../../../../../../../util/AttributeMetadata";

export abstract class ExampleData2Generated  extends Data {

    public attribute: string = null;
    public ref: ExampleData3 = null;
    public static readonly attributeMetadata: AttributeMetadata<string>= new AttributeMetadata<string>('','',AttributeType.StringAttribute);
    public static readonly refMetadata: AttributeMetadata<ExampleData3>= new AttributeMetadata<ExampleData3>('','',AttributeType.FactoryAttribute);

    public attributeAccessor(): AttributeAccessor<string,ExampleData2Generated>{
        return new AttributeAccessor<string,ExampleData2Generated>(ExampleData2Generated.attributeMetadata,this,"attribute");
    }

    public refAccessor(): AttributeAccessor<ExampleData3,ExampleData2Generated>{
        return new AttributeAccessor<ExampleData3,ExampleData2Generated>(ExampleData2Generated.refMetadata,this,"ref");
    }

    protected mapValuesFromJson(json: any, idToDataMap: any, dataCreator: DataCreator){
        this.attribute=json.attribute.v;
        this.ref=<ExampleData3>dataCreator.createData(json.ref.v,idToDataMap,this);
    }

    protected mapValuesToJson(idToDataMap: any, result: any){
        result.attribute=this.mapAttributeValueToJson(this.attribute);
        result.ref=this.mapAttributeDataToJson(idToDataMap,this.ref);
    }

    protected collectChildrenFlat(): Data[]{
        let result: Array<Data>=[];
        if (this.ref) result.push(this.ref);
        return result;
    }

    public listAttributeAccessor(): AttributeAccessor<any,ExampleData2Generated>[]{
        let result: AttributeAccessor<any,ExampleData2Generated>[]=[];
        result.push(this.attributeAccessor());
        result.push(this.refAccessor());
        return result;
    }

}