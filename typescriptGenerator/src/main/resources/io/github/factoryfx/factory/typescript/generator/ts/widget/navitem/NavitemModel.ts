import {WidgetModel} from "../../base/WidgetModel";
import {Navitem} from "./Navitem";
import {FactoryValue} from "../../base/FactoryValue";
import {Data} from "../../Data";
import {FactoryEditorModel} from "../factoryeditor/FactoryEditorModel";


export class NavitemModel extends WidgetModel<Navitem> {

    public readonly factory: FactoryValue = new FactoryValue();


    constructor(public factoryData: Data, public factoryEditor: FactoryEditorModel){
        super();
        this.factory.set(factoryData);
    }

    protected createWidget(): Navitem {
        return new Navitem(this);
    }

}