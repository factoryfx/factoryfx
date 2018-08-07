//generated code don't edit manually
import DataCreator from "./DataCreator";
import Data from "./Data";

export default class ExampleData2  extends Data {

    public attribute: string;

    protected mapValuesFromJson(json: any, idToDataMap: any, dataCreator: DataCreator){
        this.attribute=json.attribute.v;
    }

    protected mapValuesToJson(idToDataMap: any, result: any){
        result.attribute=this.mapAttributeValueToJson(idToDataMap,this.attribute);
    }


}