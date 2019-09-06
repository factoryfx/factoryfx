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
        // let previous: Data=this.get()!;
        // if (previous){
        //     for (let child of previous.collectChildren()) {
        //         child.removeChangeListener(this.listener);
        //     }
        // }
        // if (value){
        //     for (let child of value.collectChildren()) {
        //         child.addChangeListener(this.listener);
        //     }
        // }
        super.set(value);
    }

}