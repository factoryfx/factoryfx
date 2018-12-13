//generated code don't edit manually
import { ExampleFactory } from "../config/de/factoryfx/factory/typescript/generator/data/ExampleFactory";
import { ExampleData } from "../config/de/factoryfx/factory/typescript/generator/data/ExampleData";
import { ExampleData2 } from "../config/de/factoryfx/factory/typescript/generator/data/ExampleData2";
import { ExampleDataIgnore } from "../config/de/factoryfx/factory/typescript/generator/data/ExampleDataIgnore";
import { Data } from "./Data";
import { ExampleDataAll } from "../config/de/factoryfx/factory/typescript/generator/data/ExampleDataAll";

export class DataCreator  {


    public createData(json: any, idToDataMap: any): Data{
        if (!json) return null;
        let clazz=json['@class'];
        if (typeof json === 'string'){
            return idToDataMap[json];
        }
        if (clazz==='de.factoryfx.factory.typescript.generator.data.ExampleData'){
            let result: ExampleData= new ExampleData();
            result.mapFromJson(json,idToDataMap,this);
            return result;
        }
        if (clazz==='de.factoryfx.factory.typescript.generator.data.ExampleData2'){
            let result: ExampleData2= new ExampleData2();
            result.mapFromJson(json,idToDataMap,this);
            return result;
        }
        if (clazz==='de.factoryfx.factory.typescript.generator.data.ExampleDataAll'){
            let result: ExampleDataAll= new ExampleDataAll();
            result.mapFromJson(json,idToDataMap,this);
            return result;
        }
        if (clazz==='de.factoryfx.factory.typescript.generator.data.ExampleDataIgnore'){
            let result: ExampleDataIgnore= new ExampleDataIgnore();
            result.mapFromJson(json,idToDataMap,this);
            return result;
        }
        if (clazz==='de.factoryfx.factory.typescript.generator.data.ExampleFactory'){
            let result: ExampleFactory= new ExampleFactory();
            result.mapFromJson(json,idToDataMap,this);
            return result;
        }
        return null;
    }

    public createDataList(json: any, idToDataMap: any): Data[]{
        let result: Data[]=[];
        for (let entry of json) {
            result.push(this.createData(entry,idToDataMap));
        }
        return result;
    }

}