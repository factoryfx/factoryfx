import {Data} from "./Data";
import {FactoryEditor} from "./FactoryEditor";

export class NavItem {

    constructor(private text: string, private factory: Data, private factoryEditor: FactoryEditor){

    }

    create(): HTMLAnchorElement {
        let navItem: HTMLAnchorElement = document.createElement("a");
        navItem.className="nav-item nav-link";
        navItem.textContent=this.text;
        navItem.onclick=(e)=>{
            this.factoryEditor.edit(this.factory);
            e.preventDefault();
        };
        navItem.href="#";
        return navItem;
    }
}