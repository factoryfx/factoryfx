//generated code don't edit manually
import AttributeMetadata from "../data/AttributeMetadata";
import AttributeAccessor from "../data/AttributeAccessor";
import ExampleData2 from "../config/ExampleData2";
import Data from "../data/Data";
import DataCreator from "../data/DataCreator";

export default abstract class ExampleDataGenerated  extends Data {

    public attribute: string;
    public ref: ExampleData2;
    public refList: ExampleData2[];
    public static readonly attributeMetadata: AttributeMetadata<string>= new AttributeMetadata<string>('labelEn\"\'\\','labelDe');
    public static readonly refMetadata: AttributeMetadata<ExampleData2>= new AttributeMetadata<ExampleData2>('','');
    public static readonly refListMetadata: AttributeMetadata<ExampleData2[]>= new AttributeMetadata<ExampleData2[]>('','');
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
        result.attribute=this.mapAttributeValueToJson(idToDataMap,this.attribute);
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