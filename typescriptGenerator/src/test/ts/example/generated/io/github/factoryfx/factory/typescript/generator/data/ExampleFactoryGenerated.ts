//generated code don't edit manually
import { Data } from "../../../../../../../../util/Data";
import { AttributeAccessor } from "../../../../../../../../util/AttributeAccessor";
import { StaticAttributeValueAccessor } from "../../../../../../../../util/StaticAttributeValueAccessor";
import { ExampleFactory } from "../../../../../../../../config/io/github/factoryfx/factory/typescript/generator/data/ExampleFactory";
import { DataCreator } from "../../../../../../../../util/DataCreator";
import { DynamicDataDictionary } from "../../../../../../../../util/DynamicDataDictionary";
import { AttributeType } from "../../../../../../../../util/AttributeType";
import { AttributeMetadata } from "../../../../../../../../util/AttributeMetadata";

export abstract class ExampleFactoryGenerated  extends Data {

    public attribute: string = null;
    public ref: ExampleFactory = null;
    public refList: ExampleFactory[] = null;
    public static readonly attributeMetadata: AttributeMetadata<string>= new AttributeMetadata<string>('labelEn\"\'\\','labelDe',AttributeType.StringAttribute,false,[]);
    public static readonly refMetadata: AttributeMetadata<ExampleFactory>= new AttributeMetadata<ExampleFactory>('','',AttributeType.FactoryAttribute,false,[]);
    public static readonly refListMetadata: AttributeMetadata<ExampleFactory[]>= new AttributeMetadata<ExampleFactory[]>('','',AttributeType.FactoryListAttribute,true,[]);

    public attributeAccessor(): AttributeAccessor<string>{
        return new AttributeAccessor<string>(ExampleFactoryGenerated.attributeMetadata,new StaticAttributeValueAccessor<string>(this,"attribute"),"attribute");
    }

    public refAccessor(): AttributeAccessor<ExampleFactory>{
        return new AttributeAccessor<ExampleFactory>(ExampleFactoryGenerated.refMetadata,new StaticAttributeValueAccessor<ExampleFactory>(this,"ref"),"ref");
    }

    public refListAccessor(): AttributeAccessor<ExampleFactory[]>{
        return new AttributeAccessor<ExampleFactory[]>(ExampleFactoryGenerated.refListMetadata,new StaticAttributeValueAccessor<ExampleFactory[]>(this,"refList"),"refList");
    }

    protected mapValuesFromJson(json: any, idToDataMap: any, dataCreator: DataCreator, dynamicDataDictionary: DynamicDataDictionary){
        this.attribute=json.attribute.v;
        this.ref=<ExampleFactory>dataCreator.createData(json.ref.v,idToDataMap,this);
        this.refList=<ExampleFactory[]>dataCreator.createDataList(json.refList,idToDataMap,this);
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