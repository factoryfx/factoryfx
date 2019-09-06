import {AttributeAccessor} from "../../AttributeAccessor";
import {Widget} from "../../base/Widget";

export abstract class AttributeEditorWidget extends Widget {

    constructor(protected attributeAccessor: AttributeAccessor<any>, protected inputId: string) {
        super()
    }

    createLabel(): HTMLLabelElement{
        let label: HTMLLabelElement = document.createElement("label");
        label.htmlFor=this.inputId;
        label.textContent=this.attributeAccessor.getAttributeMetadata().getLabelText("en");
        label.className = "col-xl-2 col-form-label";
        label.style.textOverflow="clip";
        label.style.overflow="hidden";
        return label;
    }

    render(): HTMLElement{
        let result = this.renderAttribute();
        this.bindAttribute();
        return result;
    }



    bindModel(){
        this.bindAttribute();
    }

    protected abstract renderAttribute(): HTMLElement;

    protected bindAttribute(): any{

    }

}