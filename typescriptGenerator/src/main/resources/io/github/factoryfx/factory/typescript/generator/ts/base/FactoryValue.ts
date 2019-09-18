import {Value} from "./Value";
import {Data} from "../Data";
import {WidgetModel} from "./WidgetModel";

export class  FactoryValue extends Value<Data> {

    private listener: ()=>any;
    constructor() {
        super();
        this.listener=()=> {
            if (this.parent) {
                this.parent.update();
            }
        }
    }

    getChildren(): WidgetModel<any>[] {
        return [];
    }



    set(value?: Data){
        let previous: Data=this.get()!;
        if (previous){
            previous.removeChangeListener(this.listener);

        }
        if (value){
            value.addChangeListener(this.listener);
        }
        super.set(value);
    }

}