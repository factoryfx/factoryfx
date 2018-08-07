//generated code don't edit manually
import ExampleData2 from "./ExampleData2";
import DataCreator from "./DataCreator";
import Data from "./Data";

export default class ExampleData  extends Data {

    public attribute: string;
    public ref: ExampleData2;
    public refList: ExampleData2[];

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


}