import {Data} from "./Data";
import {ValidationError} from "./ValidationError";
import {AttributeAccessor} from "./AttributeAccessor";

export interface AttributeEditor {

    create(): HTMLElement;

}