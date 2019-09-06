import {Widget} from "./Widget";
import {Value} from "./Value";

export abstract class WidgetModel<W extends Widget> {
    constructor(){

    }

    protected abstract createWidget(): W;

    public widget?: W;

    public getValues(): Array<Value<any>>{
        let result: Array<Value<any>> =[];
        for (const prop in this) {
            if (this.hasOwnProperty(prop)) {
                if (this[prop] instanceof Value){
                    let tsWorkaround: any = this[prop];
                    result.push(<Value<any>>tsWorkaround);
                }
            }
        }
        return result;
    }


    public getWidget(): W {
        if (!this.widget){
            for (let value of this.getValues()) {
                value.finalize(this);
            }

            this.widget=this.createWidget();
        }
        return this.widget;
    }

    update() {
        this.widget!.bindModel();
    }
}