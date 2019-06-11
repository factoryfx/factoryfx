//intellij run parameter:  -r ts-node/register
//user interface: qunit, must be set in run config

import { expect } from 'chai';
import 'mocha';
// import {ExampleData} from "./example/config/ExampleData";
import {FactoryEditor} from "./example/util/FactoryEditor";
import {JSDOM} from 'jsdom';
import {Data} from "./example/util/Data";
import {ExampleData} from "./example/config/io/github/factoryfx/factory/typescript/generator/data/ExampleData";
// import { JSDOM } from JSDOM;

suite('FactoryEditorDataTest');// set user interface to QUnit in runconfig


test('test_helloworld', () => {
    const dom = new JSDOM(`<!DOCTYPE html><p>Hello world</p>`);
    let parentElement: HTMLElement=dom.window.document.querySelector("p");

    let factoryEditor: FactoryEditor = new FactoryEditor(parentElement);

    let root: ExampleData= new ExampleData();

    factoryEditor.edit(root);

    console.log(dom.window.document.querySelector("p").textContent);
    // data.mapFromJsonFromRoot(JSON.parse(exampleDataAll),new DataCreator());
    // expect(data.stringAttributeAccessor().attributeMetadata.getType()).to.equal(AttributeType.StringAttribute);
    // expect(data.integerAttributeAccessor().attributeMetadata.getType()).to.equal(AttributeType.IntegerAttribute);


});

