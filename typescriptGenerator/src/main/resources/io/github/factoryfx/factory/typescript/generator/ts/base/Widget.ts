import {WidgetModel} from "./WidgetModel";

export abstract class Widget {
    protected abstract render(): HTMLElement;
    public bindModel(): any{
        let newRendered = this.render();
        if (this.parent){
            if (this.rendered){
                this.parent.replaceChild(newRendered,this.rendered!);
            } else {
                this.parent.appendChild(newRendered);
            }
        }
        this.rendered=newRendered;
    }

    private parent?: HTMLElement;
    private rendered?: HTMLElement;
    append(parent: HTMLElement){
        this.parent=parent;
        this.rendered = this.render();
        this.parent.appendChild(this.rendered);
    }

    // bindModel(model: M){
    //     let focused: Element|null= document.activeElement;
    //     let newRendered = this.render();
    //     if (this.parent){
    //         this.parent!.replaceChild(newRendered,this.rendered!);
    //     }
    //     this.rendered=newRendered;
    //
    //     if (focused instanceof HTMLElement && focused!==document.body){
    //         this.refocus(focused);
    //     }
    // }
}