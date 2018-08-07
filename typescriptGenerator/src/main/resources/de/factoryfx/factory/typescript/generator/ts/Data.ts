import DataCreator from "./DataCreator";

export default abstract class Data  {
    id: string;
    javaClass: string;

    //DataCreator passed as parameter cause cyclic dependency (ts compiles fine but error at runtime)
    public mapFromJsonFromRoot(json: any, dataCreator: DataCreator) {
        this.mapFromJson(json,{},dataCreator);
    }

    public mapFromJson(json: any, idToDataMap: any, dataCreator: DataCreator) {
        this.id = json.id;
        this.javaClass = json['@class'];
        this.mapValuesFromJson(json, idToDataMap,dataCreator);
        idToDataMap[this.id]=this;
    }

    public mapToJsonFromRoot(): any {
        return this.mapToJson({});
    }

    public mapToJson(idToDataMap: any): any{
        if (idToDataMap[this.id]){
            return this.id;
        }
        idToDataMap[this.id]=this;
        let result: any = {};
        result['@class'] = this.javaClass;
        result.id = this.id;
        this.mapValuesToJson(idToDataMap,result);
        return result;
    }

    protected abstract mapValuesFromJson(json: any, idToDataMap: any, dataCreator: DataCreator);//hook for generated code
    protected abstract mapValuesToJson(idToDataMap: any, result: any);//hook for generated code

    protected mapAttributeValueToJson(idToDataMap: any, value: any): any {
        if (value) {
            return {
                v: value
            };
        }
        return {}
    }

    protected mapAttributeDataToJson(idToDataMap: any, data: Data): any {
        if (data) {
            return {
                v: data.mapToJson(idToDataMap)
            };
        }
        return {}
    }

    protected mapAttributeDataListToJson(idToDataMap: any, dataList: Data[]): any {
        let result = [];
        if (dataList) {
            for (let entry of dataList) {
                result.push(entry.mapToJson(idToDataMap));
            }
        }
        return result;
    }

}
