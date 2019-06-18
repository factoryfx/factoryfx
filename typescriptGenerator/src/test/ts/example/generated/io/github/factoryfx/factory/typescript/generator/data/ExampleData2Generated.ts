//generated code don't edit manually
import { Data } from "../../../../../../../../util/Data";
import { AttributeAccessor } from "../../../../../../../../util/AttributeAccessor";
import { StaticAttributeValueAccessor } from "../../../../../../../../util/StaticAttributeValueAccessor";
import { ExampleData3 } from "../../../../../../../../config/io/github/factoryfx/factory/typescript/generator/data/ExampleData3";
import { DataCreator } from "../../../../../../../../util/DataCreator";
import { DynamicDataDictionary } from "../../../../../../../../util/DynamicDataDictionary";
import { AttributeType } from "../../../../../../../../util/AttributeType";
import { AttributeMetadata } from "../../../../../../../../util/AttributeMetadata";

export abstract class ExampleData2Generated  extends Data {

    public attribute: string = null;
    public ref: ExampleData3 = null;
    public static readonly attributeMetadata: AttributeMetadata<string>= new AttributeMetadata<string>('','',AttributeType.StringAttribute,false,[]);
    public static readonly refMetadata: AttributeMetadata<ExampleData3>= new AttributeMetadata<ExampleData3>('','',AttributeType.FactoryAttribute,false,[]);

    public attributeAccessor(): AttributeAccessor<string>{
        return new AttributeAccessor<string>(ExampleData2Generated.attributeMetadata,new StaticAttributeValueAccessor<string>(this,"attribute"),"attribute");
    }

    public refAccessor(): AttributeAccessor<ExampleData3>{
        return new AttributeAccessor<ExampleData3>(ExampleData2Generated.refMetadata,new StaticAttributeValueAccessor<ExampleData3>(this,"ref"),"ref");
    }

    protected mapValuesFromJson(json: any, idToDataMap: any, dataCreator: DataCreator, dynamicDataDictionary: DynamicDataDictionary){
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

    public listAttributeAccessor(): AttributeAccessor<any>[]{
        let result: AttributeAccessor<any>[]=[];
        result.push(this.attributeAccessor());
        result.push(this.refAccessor());
        return result;
    }

}