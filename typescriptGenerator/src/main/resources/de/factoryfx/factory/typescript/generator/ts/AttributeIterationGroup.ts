import AttributeAccessor from "./AttributeAccessor";
import Data from "./Data";

export default class AttributeIterationGroup<P extends Data> {
    attributes: AttributeAccessor<any,P>[];

    constructor(attributes: AttributeAccessor<any,P>[]) {
        this.attributes=attributes;
    }
}