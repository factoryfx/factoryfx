import { Value } from "./Value";
export class WidgetModel {
    constructor() {
    }
    getValues() {
        let result = [];
        for (const prop in this) {
            if (this.hasOwnProperty(prop)) {
                if (this[prop] instanceof Value) {
                    let tsWorkaround = this[prop];
                    result.push(tsWorkaround);
                }
            }
        }
        return result;
    }
    getWidget() {
        if (!this.widget) {
            for (let value of this.getValues()) {
                value.finalize(this);
            }
            this.widget = this.createWidget();
        }
        return this.widget;
    }
    update() {
        this.widget.bindModel();
    }
}
