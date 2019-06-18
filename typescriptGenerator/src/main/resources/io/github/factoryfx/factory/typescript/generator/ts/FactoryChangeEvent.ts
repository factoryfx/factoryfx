import {Data} from "./Data";

export interface FactoryChangeEvent {
    onChange(newData: Data);
}