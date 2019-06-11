//intellij run parameter:  -r ts-node/register
//user interface: qunit, must be set in run config

import { expect } from 'chai';
import 'mocha';
// import {ExampleData} from "./example/config/ExampleData";
import {ExampleData} from "./example/config/io/github/factoryfx/factory/typescript/generator/data/ExampleData";
import {ExampleData2} from "./example/config/io/github/factoryfx/factory/typescript/generator/data/ExampleData2";
import {ExampleDataAll} from "./example/config/io/github/factoryfx/factory/typescript/generator/data/ExampleDataAll";
import {ExampleDataIgnore} from "./example/config/io/github/factoryfx/factory/typescript/generator/data/ExampleDataIgnore";
import {ExampleFactory} from "./example/config/io/github/factoryfx/factory/typescript/generator/data/ExampleFactory";
import {DataCreator} from "./example/util/DataCreator";
import {ExampleEnum} from "./example/generated/io/github/factoryfx/factory/typescript/generator/data/ExampleEnum";
import {AttributeType} from "./example/util/AttributeType";
import {ExampleData3} from "./example/config/io/github/factoryfx/factory/typescript/generator/data/ExampleData3";
import {Data} from "./example/util/Data";


suite('DataTest');// set user interface to QUnit in runconfig

let exampleDataJson=
    `
        {
          "@class" : "io.github.factoryfx.factory.typescript.generator.data.ExampleData",
          "id" : "84dda4a0-a522-d46f-b8a2-4a1eaedc0384",
          "attribute" : {
            "v" : "123"
          },
          "ref" : {
            "v" : {
              "@class" : "io.github.factoryfx.factory.typescript.generator.data.ExampleData2",
              "id" : "8cabb2a7-4a60-6970-c5f6-aaa6220d3cac",
              "attribute" : { },
              "ref" : { }
            }
          },
          "refList" : [ {
            "@class" : "io.github.factoryfx.factory.typescript.generator.data.ExampleData2",
            "id" : "fd260522-8167-946d-fd4e-ba4ade1868bb",
            "attribute" : { },
            "ref" : { }
          }, "8cabb2a7-4a60-6970-c5f6-aaa6220d3cac" ]
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

    expect(data.ref.getId()).to.equal("8cabb2a7-4a60-6970-c5f6-aaa6220d3cac");
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
          "@class" : "io.github.factoryfx.factory.typescript.generator.data.ExampleData",
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

test('getDisplayText', () => {
    let data: ExampleData = new ExampleData();

    expect(data.getDisplayText()).to.equal("Example Data");
});

test('collectChildren', () => {
    let data: ExampleData = new ExampleData();
    data.ref=new ExampleData2();
    data.refList=[new ExampleData2(),new ExampleData2()]

    expect(data.collectChildren().length).to.equal(4);
});

let exampleDataAll=
    `
        {
          "@class" : "io.github.factoryfx.factory.typescript.generator.data.ExampleDataAll",
          "id" : "87097278-cd36-9247-34a9-c342febab4eb",
          "byteArrayAttribute" : {
            "v" : "Cuw="
          },
          "i18nAttribute" : {
            "v" : {
              "texts" : {
                "de" : "textde",
                "en" : "texten"
              }
            }
          },
          "encryptedStringAttribute" : {
            "v" : {
              "encryptedString" : "Sel5YnHBjWyOR1W5cjXtkw=="
            }
          },
          "doubleAttribute" : {
            "v" : 0.5
          },
          "byteAttribute" : {
            "v" : 10
          },
          "booleanAttribute" : {
            "v" : true
          },
          "localDateAttribute" : {
            "v" : "2018-12-04"
          },
          "enumAttribute" : {
            "v" : "VALUE1"
          },
          "charAttribute" : {
            "v" : "a"
          },
          "longAttribute" : {
            "v" : 9
          },
          "stringAttribute" : {
            "v" : "text"
          },
          "integerAttribute" : {
            "v" : 8
          },
          "localDateTimeAttribute" : {
            "v" : "2018-12-04T17:25:58.7759195"
          },
          "localeAttribute" : {
            "v" : "en"
          },
          "durationAttribute" : {
            "v" : "PT4S"
          },
          "fileContentAttribute" : {
            "v" : "Cuw="
          },
          "localTimeAttribute" : {
            "v" : "17:25:58.7759195"
          },
          "shortAttribute" : {
            "v" : 3
          },
          "passwordAttribute" : {
            "v" : {
              "encryptedString" : "/t7ccPKYps7m22YuhoOS+Q=="
            }
          },
          "uriAttribute" : {
            "v" : "http://google.de"
          },
          "bigDecimalAttribute" : {
            "v" : 3
          },
          "floatAttribute" : {
            "v" : 0.6
          },
          "stringListAttribute" : [ "ab", "cd" ],
          "enumListAttribute" : [ "VALUE1", "VALUE2" ],
          "instantAttribute" : {
            "v" : "2018-12-12T10:24:55.026232600Z"
          },
          "bigIntegerAttribute" : {
            "v" : 56756372572547253765427654376257643527656775656757576
          }
        }
    `;

function assertJsonEquals(exampleDataAllJson: any , data: ExampleDataAll){
    let expected = JSON.stringify(exampleDataAllJson, null, 2);
    let toJson = data.mapToJsonFromRoot();
    toJson.instantAttribute.v="2018-12-12T10:24:55.026232600Z";//ignore for equals cause js don't support nanoseconds date
    toJson.localDateTimeAttribute.v="2018-12-04T17:25:58.7759195";//ignore for equals cause js don't support nanoseconds date
    let actual = JSON.stringify(toJson, null, 2);
    expect(actual).to.equal(expected);
}

function assertExampleDataAllEquals(data: ExampleDataAll){
    assertJsonEquals(JSON.parse(exampleDataAll),data);
}


test('test_all_attributes', () => {
    let data: ExampleDataAll = new ExampleDataAll();
    data.mapFromJsonFromRoot(JSON.parse(exampleDataAll),new DataCreator());
    assertExampleDataAllEquals(data);
});

test('test_all_attributes_type', () => {
    let data: ExampleDataAll = new ExampleDataAll();
    data.mapFromJsonFromRoot(JSON.parse(exampleDataAll),new DataCreator());
    expect(data.localDateAttribute instanceof Date).to.equal(true);
    expect(data.enumAttribute).to.equal(ExampleEnum.VALUE1);
    expect(data.enumAttribute===ExampleEnum.VALUE1).to.equal(true);
    expect(data.stringListAttribute[0]).to.equal("ab");
    expect(data.enumListAttribute[0]).to.equal(ExampleEnum.VALUE1);
    expect(data.bigIntegerAttribute).to.equal(56756372572547253765427654376257643527656775656757576);
});

test('test_null_enum', () => {
    let data: ExampleDataAll = new ExampleDataAll();
    let json = JSON.parse(exampleDataAll);
    json.enumAttribute={};

    data.mapFromJsonFromRoot(json,new DataCreator());
    expect(data.enumAttribute).to.equal(undefined);

    assertJsonEquals(json,data);
});

test('test_null_zero', () => {
    let data: ExampleDataAll = new ExampleDataAll();
    let json = JSON.parse(exampleDataAll);
    json.integerAttribute.v=0;

    data.mapFromJsonFromRoot(json,new DataCreator());
    expect(data.integerAttribute).to.equal(0);

    assertJsonEquals(json,data);
});

let exampleDataIgnore=
    `
        {
            "@class" : "io.github.factoryfx.factory.typescript.generator.data.ExampleDataIgnore",
            "id" : "ec9b0af6-74c3-590d-9d6d-53299c9af6ee",
            "stringAttribute" : { }
        }
    `;

test('test_ignore_attributes', () => {
    let data: ExampleDataIgnore= new ExampleDataIgnore();
    data.mapFromJsonFromRoot(JSON.parse(exampleDataIgnore),new DataCreator());

    let expected = JSON.stringify(JSON.parse(exampleDataIgnore), null, 2);
    let actual = JSON.stringify(data.mapToJsonFromRoot(), null, 2);
    expect(expected).to.equal(actual);
});


let factoryExample = `
{
  "@class" : "io.github.factoryfx.factory.typescript.generator.data.ExampleFactory",
  "id" : "09faee1a-125f-2b1b-d90d-ed3f0d41fca0",
  "attribute" : {
    "v" : "123"
  },
  "ref" : {
    "v" : {
      "@class" : "io.github.factoryfx.factory.typescript.generator.data.ExampleFactory",
      "id" : "4565f9bd-7783-8f4a-3df1-ac190fbe42c8",
      "attribute" : { },
      "ref" : { },
      "refList" : [ ]
    }
  },
  "refList" : [ {
    "@class" : "io.github.factoryfx.factory.typescript.generator.data.ExampleFactory",
    "id" : "cee0b7e7-0a9f-6361-f0af-02a7d860b738",
    "attribute" : { },
    "ref" : { },
    "refList" : [ ]
  } ]
}
`
test('test_factories', () => {
    let data: ExampleFactory = new ExampleFactory();
    data.mapFromJsonFromRoot(JSON.parse(factoryExample),new DataCreator());
    let expected = JSON.stringify(JSON.parse(factoryExample), null, 2);
    let actual = JSON.stringify(data.mapToJsonFromRoot(), null, 2);
    expect(actual).to.equal(expected);
});

test('test_attribute_type', () => {
    let data: ExampleDataAll = new ExampleDataAll();
    data.mapFromJsonFromRoot(JSON.parse(exampleDataAll),new DataCreator());
    expect(data.stringAttributeAccessor().attributeMetadata.getType()).to.equal(AttributeType.StringAttribute);
    expect(data.integerAttributeAccessor().attributeMetadata.getType()).to.equal(AttributeType.IntegerAttribute);
});

test('test_addBackReferences', () => {
    let data: ExampleData = new ExampleData();
    let exampleData2 = new ExampleData2();
    data.ref= exampleData2;
    let exampleData3 = new ExampleData3();
    data.ref.ref= exampleData3;

    data.addBackReferences();

    let path: Array<Data> = exampleData3.getPath();
    expect(data.getParent()).to.equal(undefined);
    expect(exampleData2.getParent()).to.equal(data);
    expect(exampleData3.getParent()).to.equal(exampleData2);


});

test('test_path', () => {
    let data: ExampleData = new ExampleData();
    let exampleData2 = new ExampleData2();
    data.ref= exampleData2;
    let exampleData3 = new ExampleData3();
    data.ref.ref= exampleData3;

    data.addBackReferences();

    let path: Array<Data> = exampleData3.getPath();
    expect(path[0]).to.equal(data);
    expect(path[1]).to.equal(exampleData2);
    expect(path[2]).to.equal(exampleData3);


});

test('test_label', () => {
    let data: ExampleData = new ExampleData();
    expect(data.attributeAccessor().getLabelText('en')).to.equal('labelEn\"\'\\');
    expect(data.refAccessor().getLabelText('en')).to.equal('ref');


});
