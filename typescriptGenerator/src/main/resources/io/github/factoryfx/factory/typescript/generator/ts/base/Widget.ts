export abstract class Widget {
    protected abstract render(): HTMLElement;
    public bindModel(): any{
         this.reRender(this.render());
    }

    private parent?: HTMLElement;
    private rendered?: HTMLElement;
    append(parent: HTMLElement){
        this.parent=parent;

        this.bindModel()
    }

    protected reRender(newRendered: HTMLElement){
        if (this.rendered && this.parent!.contains(this.rendered)){
            this.parent!.replaceChild(newRendered,this.rendered!);
        } else {
            this.parent!.appendChild(newRendered);
        }
        this.rendered=newRendered;
    }

    private renderedToParent?: HTMLElement;
    protected renderOnce(){
        if (this.renderedToParent!=this.parent){
            this.renderedToParent=this.parent;
            this.reRender(this.render());
        }

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