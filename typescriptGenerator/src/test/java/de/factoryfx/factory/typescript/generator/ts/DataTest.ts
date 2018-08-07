import Data from '../../../../../../../../main/resources/de/factoryfx/factory/typescript/generator/ts/Data';

// import 'mocha';
// import { expect } from 'chai';
//
// // const expect = chai.expect;
//
// describe('Hello function', () => {
//     it('should return hello world', () => {
//         let data = new Data();
//         expect('Hello World!').to.equal('Hello World!');
//     });
// });

// // new Data();
// describe('My math library', () => {
//
//     it('should be able to add things correctly' , () => {
//         expect(add(3,4)).to.equal(7);
//     });
//
// });

// import 'mocha';

// describe('my test', () => {
//     it('does something', () => {
//         // your test
//     });
// });


import { expect } from 'chai';
import 'mocha';
import ExampleData from "./example/ExampleData";
import DataCreator from "./example/DataCreator";

suite('DataTest');

let exampleDataJson=
    `
        {
          "@class" : "de.factoryfx.factory.typescript.generator.data.ExampleData",
          "id" : "1bf502f7-163a-d84c-10c4-41d6486e63a1",
          "attribute" : {
            "v" : "123"
          },
          "ref" : {
            "v" : {
              "@class" : "de.factoryfx.factory.typescript.generator.data.ExampleData2",
              "id" : "8738e21d-8eaa-c3e4-26fe-e9ed0d89c4b2",
              "attribute" : { }
            }
          },
          "refList" : [ {
            "@class" : "de.factoryfx.factory.typescript.generator.data.ExampleData2",
            "id" : "346434f9-86a2-8576-3f32-cbb12ef70828",
            "attribute" : { }
          }, "8738e21d-8eaa-c3e4-26fe-e9ed0d89c4b2" ]
        }
    `;

test('mapValuesFromJson_value', ()=>{
    let data: ExampleData = new ExampleData();

    data.mapFromJsonFromRoot(JSON.parse(exampleDataJson),new DataCreator());

    expect(data.attribute).to.equal("123");
});

test('mapValuesFromJson_ref', ()=>{
    let data: ExampleData = new ExampleData();

    data.mapFromJsonFromRoot(JSON.parse(exampleDataJson),new DataCreator());

    expect(data.ref.attribute).to.equal(undefined);
});

test('mapValuesFromJson_ref_id', ()=>{
    let data: ExampleData = new ExampleData();

    data.mapFromJsonFromRoot(JSON.parse(exampleDataJson),new DataCreator());

    expect(data.ref.id).to.equal("8738e21d-8eaa-c3e4-26fe-e9ed0d89c4b2");
});

test('mapValuesFromJson_ref_id', ()=>{
    let data: ExampleData = new ExampleData();

    data.mapFromJsonFromRoot(JSON.parse(exampleDataJson),new DataCreator());

    expect(data.ref.id).to.equal("8738e21d-8eaa-c3e4-26fe-e9ed0d89c4b2");
});

test('mapValuesFromJson_list', ()=>{
    let data: ExampleData = new ExampleData();

    data.mapFromJsonFromRoot(JSON.parse(exampleDataJson),new DataCreator());

    expect(data.refList[0].attribute).to.equal(undefined);
});

test('mapValuesFromJson_list_JsonIdentityInfo', ()=>{
    let data: ExampleData = new ExampleData();

    data.mapFromJsonFromRoot(JSON.parse(exampleDataJson),new DataCreator());

    expect(data.refList[1]).to.equal(data.ref);
});

let exampleDataJsonEmpty=
    `
        {
          "@class" : "de.factoryfx.factory.typescript.generator.data.ExampleData",
          "id" : "d7ad0a40-3216-7d29-c589-cee305e40f34",
          "attribute" : {
            "v" : "123"
          },
          "ref" : { },
          "refList" : [ ]
        }
    `;

test('mapValuesFromJson_ref_empty', ()=>{
    let data: ExampleData = new ExampleData();

    data.mapFromJsonFromRoot(JSON.parse(exampleDataJsonEmpty),new DataCreator());

    expect(data.ref).to.equal(null);

    expect(Array.isArray(data.refList)).to.equal(true);
    expect(data.refList.length).to.equal(0);
});


test('mapValuesToJson', ()=>{
    let data: ExampleData = new ExampleData();
    data.mapFromJsonFromRoot(JSON.parse(exampleDataJson),new DataCreator());
    let json:any = data.mapToJsonFromRoot();

    let stringifyExampleDataJson = JSON.stringify(JSON.parse(exampleDataJson), null, 2);
    let stringifyResult = JSON.stringify(json, null, 2);
    expect(stringifyResult).to.equal(stringifyExampleDataJson);
});

test('mapValuesToJson_empty', ()=>{
    let data: ExampleData = new ExampleData();
    data.mapFromJsonFromRoot(JSON.parse(exampleDataJsonEmpty),new DataCreator());
    let json:any = data.mapToJsonFromRoot();

    let stringifyExampleDataJsonEmpty = JSON.stringify(JSON.parse(exampleDataJsonEmpty), null, 2);
    let stringifyResult = JSON.stringify(json, null, 2);
    expect(stringifyResult).to.equal(stringifyExampleDataJsonEmpty);
});

