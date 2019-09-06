import {WidgetModel} from "./WidgetModel";

export class Value<T> {

    private value?: T;

    get(): T|undefined{
        return this.value;
    }

    set(value?: T){
        this.value=value;
        if (this.parent){
            this.parent.update();
        }
    }


    getChildren(): WidgetModel<any>[]{
        return [];
    }

    protected parent?: WidgetModel<any>;
    finalize(parent: WidgetModel<any>){
        this.parent=parent;
    }


}