import {WidgetModel} from "../../base/WidgetModel";
import {FactoryValue} from "../../base/FactoryValue";
import {FactoryEditor} from "./FactoryEditor";
import {Data} from "../../Data";
import {BooleanValue} from "../../base/BooleanValue";
import {HttpClient} from "../../HttpClient";


export class FactoryEditorModel extends WidgetModel<FactoryEditor> {
    public readonly visible: BooleanValue = new BooleanValue();


    public readonly factory: FactoryValue = new FactoryValue();

    constructor(private httpClient: HttpClient){
        super();
    }


    public getFactory(): Data{
        return this.factory.get()!;
    }

    public edit(factory: Data){
        this.factory.set(factory);
    }

    protected createWidget(): FactoryEditor {
        return new FactoryEditor(this,this.httpClient);
    }

}