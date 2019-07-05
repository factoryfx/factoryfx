import {NavItem} from "./NavItem";
import {HttpUtility} from "./HttpUtility";
import {Widget} from "./Widget";
import {FactoryEditor} from "./FactoryEditor";
import {Data} from "./Data";
import {View} from "./View";
import {FactoryUpdateResult} from "./FactoryUpdateResult";
import {WaitAnimation} from "./WaitAnimation";
import {SaveWidget} from "./SaveWidget";

export class Navbar implements Widget{
    constructor(private projectName: string, private navItems: NavItem[], private factoryEditor: FactoryEditor, private view: View, private saveWidget: SaveWidget){
        this.factoryEditor.addOnFactoryChange(newData => {
            this.update();
        });
    }

    create(): HTMLElement{
        let nav: HTMLElement = document.createElement("nav");
        nav.className="navbar navbar-expand-lg navbar-dark bg-dark";
        let navbarBrand: HTMLAnchorElement = document.createElement("a");
        navbarBrand.className="navbar-brand";
        navbarBrand.href="#";
        navbarBrand.textContent=this.projectName;

        // let collapse: HTMLElement = document.createElement("div");
        // collapse.className="collapse navbar-collapse";
        let navbarNav: HTMLElement = document.createElement("div");
        navbarNav.className="navbar-nav mr-auto";

        for (let navItem of this.navItems){
            navbarNav.appendChild(navItem.create());
        }

        let form: HTMLFormElement = document.createElement("form");
        form.className="form-inline";
        let saveButton: HTMLButtonElement = document.createElement("button");
        saveButton.type="button";
        saveButton.textContent="Save";
        saveButton.className="btn btn-outline-success";
        saveButton.onclick=(e)=>{
            if (this.factoryEditor.validate()){
                this.view.show(this.saveWidget)
            }
        };
        form.appendChild(saveButton);

        nav.appendChild(navbarBrand);
        nav.appendChild(navbarNav);
        nav.appendChild(form);
        return nav;
    }


    public update(): void{
        for (let navItem of this.navItems){
            navItem.update();
        }
    }

}