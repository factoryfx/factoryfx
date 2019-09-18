import {AttributeAccessor} from "../../../AttributeAccessor";
import {AttributeEditorWidget} from "../AttributeEditorWidget";

export class BooleanAttributeEditor extends AttributeEditorWidget{

    constructor(protected attributeAccessor: AttributeAccessor<any>, protected inputId: string) {
        super(attributeAccessor,inputId);
    }

    protected render(): HTMLElement{
        let div: HTMLDivElement= document.createElement("div");
        div.className="form-check";

        let input: HTMLInputElement= document.createElement("input");
        input.id=this.inputId.toString();
        input.className="form-check-input position-static";
        input.type="checkbox";

        input.checked=this.attributeAccessor.getValue();
        input.oninput= (e) => {
            this.attributeAccessor.setValue(input.checked);
        };

        div.appendChild(input);
        return div;
    }

}