import {NavItem} from "./NavItem";
import {Navbar} from "./Navbar";
import {FactoryEditor} from "./FactoryEditor";
import {Data} from "./Data";
import {View} from "./View";
import {WaitAnimation} from "./WaitAnimation";
import {SaveWidget} from "./SaveWidget";

export class GuiConfiguration {
    constructor(private guiConfigurationJson: any, private factoryEditor: FactoryEditor, private root: Data,  private baseVersionId, private view: View, private waitAnimation: WaitAnimation){

    }

    public createNavbar(): Navbar{
        let navItems: NavItem[]=[];
        for (let navItemsJson of this.guiConfigurationJson.navBarItems){
            navItems.push(new NavItem(this.root.getChildFromRoot(navItemsJson.factoryId),this.factoryEditor));
        }
        return new Navbar(this.guiConfigurationJson.projectName,navItems,this.factoryEditor,this.view,new SaveWidget( this.root,this.baseVersionId,this.view,this.waitAnimation));
    }
}