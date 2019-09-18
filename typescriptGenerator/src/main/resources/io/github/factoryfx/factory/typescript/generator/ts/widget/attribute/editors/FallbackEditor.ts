import {AttributeAccessor} from "../../../AttributeAccessor";
import {AttributeEditorWidget} from "../AttributeEditorWidget";

export class FallbackEditor extends AttributeEditorWidget{

    constructor(protected attributeAccessor: AttributeAccessor<any>, protected inputId: string) {
        super(attributeAccessor,inputId);
    }

    protected render(): HTMLElement{
        let div: HTMLElement= document.createElement("div");
        div.textContent='not editable: '+this.attributeAccessor.getAttributeMetadata().getType();

        return div;
    }

}