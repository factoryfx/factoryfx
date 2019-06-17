//generated code don't edit manually
import { Data } from "./Data";
import { ExampleData } from "../config/io/github/factoryfx/factory/typescript/generator/data/ExampleData";
import { ExampleData2 } from "../config/io/github/factoryfx/factory/typescript/generator/data/ExampleData2";
import { ExampleFactory } from "../config/io/github/factoryfx/factory/typescript/generator/data/ExampleFactory";
import { ExampleData3 } from "../config/io/github/factoryfx/factory/typescript/generator/data/ExampleData3";
import { ExampleDataIgnore } from "../config/io/github/factoryfx/factory/typescript/generator/data/ExampleDataIgnore";
import { ExampleDataAll } from "../config/io/github/factoryfx/factory/typescript/generator/data/ExampleDataAll";

export class DataCreator  {


    public createData(json: any, idToDataMap: any, parent: Data): Data{
        if (!json) return null;
        let clazz=json['@class'];
        if (typeof json === 'string'){
            return idToDataMap[json];
        }
        if (clazz==='io.github.factoryfx.factory.typescript.generator.data.ExampleData'){
            let result: ExampleData= new ExampleData();
            result.mapFromJson(json,idToDataMap,this);
            result.setParent(parent);
            return result;
        }
        if (clazz==='io.github.factoryfx.factory.typescript.generator.data.ExampleData2'){
            let result: ExampleData2= new ExampleData2();
            result.mapFromJson(json,idToDataMap,this);
            result.setParent(parent);
            return result;
        }
        if (clazz==='io.github.factoryfx.factory.typescript.generator.data.ExampleData3'){
            let result: ExampleData3= new ExampleData3();
            result.mapFromJson(json,idToDataMap,this);
            result.setParent(parent);
            return result;
        }
        if (clazz==='io.github.factoryfx.factory.typescript.generator.data.ExampleDataAll'){
            let result: ExampleDataAll= new ExampleDataAll();
            result.mapFromJson(json,idToDataMap,this);
            result.setParent(parent);
            return result;
        }
        if (clazz==='io.github.factoryfx.factory.typescript.generator.data.ExampleDataIgnore'){
            let result: ExampleDataIgnore= new ExampleDataIgnore();
            result.mapFromJson(json,idToDataMap,this);
            result.setParent(parent);
            return result;
        }
        if (clazz==='io.github.factoryfx.factory.typescript.generator.data.ExampleFactory'){
            let result: ExampleFactory= new ExampleFactory();
            result.mapFromJson(json,idToDataMap,this);
            result.setParent(parent);
            return result;
        }
        return null;
    }

    public createDataList(json: any, idToDataMap: any, parent: Data): Data[]{
        let result: Data[]=[];
        for (let entry of json) {
            result.push(this.createData(entry,idToDataMap,parent));
        }
        return result;
    }

}