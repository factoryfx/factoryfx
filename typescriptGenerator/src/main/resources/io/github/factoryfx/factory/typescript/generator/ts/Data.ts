import { DataCreator } from "./DataCreator";

export abstract class Data  {
    private id: string;
    private javaClass: string;

    public getId(): string{
        if (!this.id){
            this.id=(Math.floor(Math.random()*1000000000)).toLocaleString();;
        }
        return this.id;
    }

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
    protected abstract collectChildrenRecursiveIntern(idToDataMap: any);//hook for generated code


    protected mapAttributeValueToJson(value: any): any {
        if (value!==null && value!==undefined) {
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

    public abstract getDisplayText();

    private collectChildrenRecursive(idToDataMap: any){
        if (this && !idToDataMap[this.getId()]) {
            idToDataMap[this.getId()] = this;
        } else {
            return
        }
        this.collectChildrenRecursiveIntern(idToDataMap);
    }

    protected collectDataChildren(data: Data,idToDataMap: any) {
        if (data){
            data.collectChildrenRecursive(idToDataMap);
        }
    }

    protected collectDataArrayChildren(dataArray: Data[],idToDataMap: any) {
        if (dataArray) {
            for (let child of dataArray) {
                child.collectChildrenRecursive(idToDataMap);
            }
        }
    }

    public collectChildren(): Data[] {
        let idToDataMap = {};
        this.collectChildrenRecursive(idToDataMap);

        let result = [];
        for(var child in idToDataMap) result.push(idToDataMap[child]);
        //Object["values"](idToDataMap);//Object.values(idToDataMap);
        return result;
    }

    private pad(num: number, size: number): string {
        let s = num+"";
        while (s.length < size) s = "0" + s;
        return s;
    }

    protected mapLocalDateFromJson(json: any): Date{
        return new Date(json);
    }

    protected mapLocalDateToJson(date: Date): any{
        let day = date.getDate();
        let monthIndex = date.getMonth()+1;
        let year = date.getFullYear();
        return year+"-"+this.pad(monthIndex,2)+"-"+this.pad(day,2);
    }

    protected mapInstantFromJson(json: any): Date{
        let match = /(.*)\.(.*)Z/.exec(json);
        let convertNanoToMilli=match[1]+'.'+this.pad((Math.round(Number(match[2])/100000)),2)+'Z';

        let current:Date = new Date(convertNanoToMilli);
        let utcDate = new Date(current.getTime() + current.getTimezoneOffset() * 60000);
        return utcDate;
    }

    protected mapInstantToJson(date: Date): any{
        let day = date.getDate();
        let monthIndex = date.getMonth()+1;
        let year = date.getFullYear();
        let hour = date.getHours();
        let min = date.getMinutes();
        let sec = date.getSeconds();
        let milliseconds = date.getMilliseconds();
        return year+"-"+this.pad(monthIndex,2)+"-"+this.pad(day,2)+'T'+this.pad(hour,2)+':'+this.pad(min,2)+':'+this.pad(sec,2)+'.'+milliseconds+'Z';
    }

    protected mapLocalDateTimeFromJson(json: any): Date{
        return new Date(json);
    }

    protected mapLocalDateTimeToJson(date: Date): any{
        let day = date.getDate();
        let monthIndex = date.getMonth()+1;
        let year = date.getFullYear();
        let hour = date.getHours();
        let min = date.getMinutes();
        let sec = date.getSeconds();
        let milliseconds = date.getMilliseconds();
        return year+"-"+this.pad(monthIndex,2)+"-"+this.pad(day,2)+'T'+this.pad(hour,2)+':'+this.pad(min,2)+':'+this.pad(sec,2)+'.'+milliseconds;
    }



}
