//generated code don't edit manually
import { Data } from "../../../../../../../../util/Data";
import { AttributeAccessor } from "../../../../../../../../util/AttributeAccessor";
import { ExampleData2 } from "../../../../../../../../config/io/github/factoryfx/factory/typescript/generator/data/ExampleData2";
import { DataCreator } from "../../../../../../../../util/DataCreator";
import { AttributeType } from "../../../../../../../../util/AttributeType";
import { AttributeMetadata } from "../../../../../../../../util/AttributeMetadata";

export abstract class ExampleDataGenerated  extends Data {

    public attribute: string;
    public ref: ExampleData2;
    public refList: ExampleData2[];
    public static readonly attributeMetadata: AttributeMetadata<string>= new AttributeMetadata<string>('labelEn\"\'\\','labelDe',AttributeType.StringAttribute);
    public static readonly refMetadata: AttributeMetadata<ExampleData2>= new AttributeMetadata<ExampleData2>('','',AttributeType.DataReferenceAttribute);
    public static readonly refListMetadata: AttributeMetadata<ExampleData2[]>= new AttributeMetadata<ExampleData2[]>('','',AttributeType.DataReferenceListAttribute);

    public attributeAccessor(): AttributeAccessor<string,ExampleDataGenerated>{
        return new AttributeAccessor<string,ExampleDataGenerated>(ExampleDataGenerated.attributeMetadata,this,"attribute");
    }

    public refAccessor(): AttributeAccessor<ExampleData2,ExampleDataGenerated>{
        return new AttributeAccessor<ExampleData2,ExampleDataGenerated>(ExampleDataGenerated.refMetadata,this,"ref");
    }

    public refListAccessor(): AttributeAccessor<ExampleData2[],ExampleDataGenerated>{
        return new AttributeAccessor<ExampleData2[],ExampleDataGenerated>(ExampleDataGenerated.refListMetadata,this,"refList");
    }

    protected mapValuesFromJson(json: any, idToDataMap: any, dataCreator: DataCreator){
        this.attribute=json.attribute.v;
        this.ref=<ExampleData2>dataCreator.createData(json.ref.v,idToDataMap);
        this.refList=<ExampleData2[]>dataCreator.createDataList(json.refList,idToDataMap);
    }

    protected mapValuesToJson(idToDataMap: any, result: any){
        result.attribute=this.mapAttributeValueToJson(this.attribute);
        result.ref=this.mapAttributeDataToJson(idToDataMap,this.ref);
        result.refList=this.mapAttributeDataListToJson(idToDataMap,this.refList);
    }

    protected collectChildrenRecursiveIntern(idToDataMap: any){
        this.collectDataChildren(this.ref,idToDataMap);
        this.collectDataArrayChildren(this.refList,idToDataMap);
    }

    protected listAttributeAccessor(): AttributeAccessor<any,ExampleDataGenerated>[]{
        let result: AttributeAccessor<any,ExampleDataGenerated>[]=[];
        result.push(this.attributeAccessor());
        result.push(this.refAccessor());
        result.push(this.refListAccessor());
        return result;
    }

}