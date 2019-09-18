import {AttributeAccessor} from "../../../AttributeAccessor";
import {NumberBaseAttributeEditor} from "./NumberBaseAttributeEditor";

export class BigDecimalAttributeEditor extends NumberBaseAttributeEditor{

    constructor(protected attributeAccessor: AttributeAccessor<any>, protected inputId: string) {
        super(attributeAccessor,inputId);
    }

    protected additionalInputSetup(): any {
        this.input.type="text";
    }

    public bindModel(): any {
        this.renderOnce();
        this.input.value=this.attributeAccessor.getValue();
        this.input.oninput=(e) => {
            if (/^[+-]?(?=.)(?: \d+,)*\d*(?:\.\d+)?$/.test(this.input.value)) {
                this.attributeAccessor.setValue(this.input.value);
                this.input.setCustomValidity("");
            } else {
                this.input.setCustomValidity("Not a Number");
            }
        };
        this.input.required=!this.attributeAccessor.getAttributeMetadata().nullable();
    }

}