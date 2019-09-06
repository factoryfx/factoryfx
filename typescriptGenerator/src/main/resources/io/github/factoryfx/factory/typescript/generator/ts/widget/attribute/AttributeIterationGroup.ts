import {Data} from "../../Data";
import {AttributeAccessor} from "../../AttributeAccessor";

export class AttributeIterationGroup<P extends Data> {
    attributes: AttributeAccessor<any>[];

    constructor(attributes: AttributeAccessor<any>[]) {
        this.attributes=attributes;
    }
}