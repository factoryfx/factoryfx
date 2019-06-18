//generated code don't edit manually
import {Data} from "./Data";
import {ValidationError} from "./ValidationError";
import {AttributeAccessor} from "./AttributeAccessor";
import {AttributeEditor} from "./AttributeEditor";

export class AttributeEditorFallback implements AttributeEditor{

    constructor(private attributeAccessor: AttributeAccessor<any>, private inputId: string) {

    }


    create(): HTMLElement{
        let div: HTMLElement= document.createElement("div");
        div.textContent='not editable: '+this.attributeAccessor.getAttributeMetadata().getType();

        return div;
    }

}