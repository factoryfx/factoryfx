import {Data} from "./Data";
import {FactoryEditor} from "./FactoryEditor";

export class NavItem {
    private navItem: HTMLAnchorElement = document.createElement("a");
    constructor(private factory: Data, private factoryEditor: FactoryEditor){

    }

    create(): HTMLAnchorElement {
        this.navItem.className="nav-item nav-link";
        this.navItem.textContent=this.factory.getDisplayText();
        this.navItem.onclick=(e)=>{
            this.factoryEditor.edit(this.factory);
            e.preventDefault();
        };
        this.navItem.href="#";
        return this.navItem;
    }

    public update(): void{
        if (this.factoryEditor.getCurrentData()===this.factory){
            this.navItem.className="nav-item nav-link active";
        } else {
            this.navItem.className="nav-item nav-link";
        }
    }
}