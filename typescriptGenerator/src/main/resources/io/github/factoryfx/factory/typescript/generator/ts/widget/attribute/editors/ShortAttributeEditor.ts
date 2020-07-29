import {AttributeAccessor} from "../../../AttributeAccessor";
import {NumberBaseAttributeEditor} from "./NumberBaseAttributeEditor";

export class ShortAttributeEditor extends NumberBaseAttributeEditor {

    constructor(protected attributeAccessor: AttributeAccessor<any>, protected inputId: string) {
        super(attributeAccessor, inputId);
    }

    protected additionalInputSetup(): any {
        this.input.max = "32767";
        this.input.min = "-32768";
    }

}

