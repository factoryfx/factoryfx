//generated code don't edit manually
import { DataCreator } from "./DataCreator";
import {AttributeAccessor} from "./AttributeAccessor";
import {DynamicDataDictionary} from "./DynamicDataDictionary";

export abstract class Data  {
    private id!: string;
    protected javaClass!: string;
    private parent!: Data;

    public getId(): string{
        if (!this.id){
            this.id=(Math.floor(Math.random()*1000000000)).toLocaleString();;
        }
        return this.id;
    }

    //DataCreator passed as parameter cause cyclic dependency (ts compiles fine but error at runtime)
    public mapFromJsonFromRoot(json: any, dataCreator: DataCreator) {
        this.mapFromJson(json,{},dataCreator,null);
    }

    public mapFromJsonFromRootDynamic(json: any, dynamicDataDictionary: DynamicDataDictionary) {
        this.mapFromJson(json,{},null,dynamicDataDictionary);
    }

    public mapFromJsonFromRootDynamicWidthMap(json: any, idToDataMap: any, dynamicDataDictionary: DynamicDataDictionary) {
        this.mapFromJson(json,idToDataMap,null,dynamicDataDictionary);
    }

    public mapFromJson(json: any, idToDataMap: any, dataCreator: DataCreator | null, dynamicDataDictionary: DynamicDataDictionary  | null) {
        this.id = json.id;
        this.javaClass = json['@class'];
        this.mapValuesFromJson(json, idToDataMap,dataCreator,dynamicDataDictionary);
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

    protected abstract mapValuesFromJson(json: any, idToDataMap: any, dataCreator: DataCreator  | null, dynamicDataDictionary: DynamicDataDictionary  | null): void;//hook for generated code
    protected abstract mapValuesToJson(idToDataMap: any, result: any): void;//hook for generated code
    protected abstract collectChildrenFlat(): Array<Data>;//hook for generated code
    abstract listAttributeAccessor(): AttributeAccessor<any>[];
    public abstract getDisplayText(): string;
    abstract createNewChildFactory(json: any): Data;

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



    protected collectChildrenRecursive(idToDataMap: any){
        if (this && !idToDataMap[this.getId()]) {
            idToDataMap[this.getId()] = this;
        } else {
            return
        }
        for (let child of this.collectChildrenFlat()) {
            child.collectChildrenRecursive(idToDataMap);
        }
    }

    public getChildrenFlat(): Data[] {
        return this.collectChildrenFlat();
    }


    public collectChildren(): Data[] {
        let idToDataMap: any = {};
        this.collectChildrenRecursive(idToDataMap);

        let result = [];
        for(let child in idToDataMap) result.push(idToDataMap[child]);
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
        let match: any = /(.*)\.(.*)Z/.exec(json);
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

    public getPath(): Array<Data>{
        let result: Array<Data>  = [];
        let data: Data=this;
        while (data){
            result.push(data);
            data=data.parent;
        }
        result.reverse();
        return result;
    }

    public getRoot(): Data{
        return this.getPath()[0];
    }

    public setParent(parent: Data){
        this.parent= parent;
    }


    public addBackReferences(){
        let stack: Data[] = [];
        stack.push(this);

        let data: Data;
        do {
            data= stack.pop() as Data;
            if (data){
                for (let child of data.collectChildrenFlat()) {
                    child.setParent(data);
                    stack.push(child);
                }
            }
        } while (data);
    }

    getParent(): Data{
        return this.parent;
    }

    getJavaClass(): string{
        return this.javaClass
    }

    getChildFromRoot(factoryId: any): Data {
        let idToDataMap: any = {};
        this.collectChildrenRecursive(idToDataMap);
        return idToDataMap[factoryId];
    }
}
