//generated code don't edit manually
import { Data } from "../../../../../../../../util/Data";
import { AttributeAccessor } from "../../../../../../../../util/AttributeAccessor";
import { StaticAttributeValueAccessor } from "../../../../../../../../util/StaticAttributeValueAccessor";
import { ExampleData2 } from "../../../../../../../../config/io/github/factoryfx/factory/typescript/generator/data/ExampleData2";
import { DataCreator } from "../../../../../../../../util/DataCreator";
import { DynamicDataDictionary } from "../../../../../../../../util/DynamicDataDictionary";
import { AttributeType } from "../../../../../../../../util/AttributeType";
import { AttributeMetadata } from "../../../../../../../../util/AttributeMetadata";

export abstract class ExampleDataGenerated  extends Data {

    public attribute: string = null;
    public ref: ExampleData2 = null;
    public refList: ExampleData2[] = null;
    public static readonly attributeMetadata: AttributeMetadata<string>= new AttributeMetadata<string>('labelEn\"\'\\','labelDe',AttributeType.StringAttribute,false,[]);
    public static readonly refMetadata: AttributeMetadata<ExampleData2>= new AttributeMetadata<ExampleData2>('','',AttributeType.FactoryAttribute,true,[]);
    public static readonly refListMetadata: AttributeMetadata<ExampleData2[]>= new AttributeMetadata<ExampleData2[]>('','',AttributeType.FactoryListAttribute,true,[]);

    public attributeAccessor(): AttributeAccessor<string>{
        return new AttributeAccessor<string>(ExampleDataGenerated.attributeMetadata,new StaticAttributeValueAccessor<string>(this,"attribute"),"attribute");
    }

    public refAccessor(): AttributeAccessor<ExampleData2>{
        return new AttributeAccessor<ExampleData2>(ExampleDataGenerated.refMetadata,new StaticAttributeValueAccessor<ExampleData2>(this,"ref"),"ref");
    }

    public refListAccessor(): AttributeAccessor<ExampleData2[]>{
        return new AttributeAccessor<ExampleData2[]>(ExampleDataGenerated.refListMetadata,new StaticAttributeValueAccessor<ExampleData2[]>(this,"refList"),"refList");
    }

    protected mapValuesFromJson(json: any, idToDataMap: any, dataCreator: DataCreator, dynamicDataDictionary: DynamicDataDictionary){
        this.attribute=json.attribute.v;
        this.ref=<ExampleData2>dataCreator.createData(json.ref.v,idToDataMap,this);
        this.refList=<ExampleData2[]>dataCreator.createDataList(json.refList,idToDataMap,this);
    }

    protected mapValuesToJson(idToDataMap: any, result: any){
        result.attribute=this.mapAttributeValueToJson(this.attribute);
        result.ref=this.mapAttributeDataToJson(idToDataMap,this.ref);
        result.refList=this.mapAttributeDataListToJson(idToDataMap,this.refList);
    }

    protected collectChildrenFlat(): Data[]{
        let result: Array<Data>=[];
        if (this.ref) result.push(this.ref);
        if (this.refList) for (let child of this.refList) {result.push(child)};
        return result;
    }

    public listAttributeAccessor(): AttributeAccessor<any>[]{
        let result: AttributeAccessor<any>[]=[];
        result.push(this.attributeAccessor());
        result.push(this.refAccessor());
        result.push(this.refListAccessor());
        return result;
    }

}