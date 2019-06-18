import { AttributeAccessor } from "./AttributeAccessor";
import { Data } from "./Data";

export class AttributeIterationGroup<P extends Data> {
    attributes: AttributeAccessor<any>[];

    constructor(attributes: AttributeAccessor<any>[]) {
        this.attributes=attributes;
    }
}