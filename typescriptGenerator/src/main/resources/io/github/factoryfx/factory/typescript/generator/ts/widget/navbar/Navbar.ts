import {Widget} from "../../base/Widget";
import {FactoryEditorModel} from "../factoryeditor/FactoryEditorModel";
import {NavbarModel} from "./NavbarModel";
import {BootstrapUtility} from "../../BootstrapUtility";


export class Navbar extends Widget {
    constructor(private model: NavbarModel){
        super();
    }

    render(): HTMLElement {
        let nav: HTMLElement = document.createElement("nav");
        nav.className="navbar navbar-expand-lg navbar-dark bg-dark";
        let navbarBrand: HTMLAnchorElement = document.createElement("a");
        navbarBrand.className="navbar-brand";
        navbarBrand.href="#";
        navbarBrand.textContent=this.model.projectName.get()!;

        // let collapse: HTMLElement = document.createElement("div");
        // collapse.className="collapse navbar-collapse";
        let navbarNav: HTMLElement = document.createElement("div");
        navbarNav.className="navbar-nav mr-auto";

        for (let navItem of this.model.navItems){
            navItem.getWidget().append(navbarNav);
        }

        let form: HTMLFormElement = document.createElement("form");
        form.className="form-inline";

        let historyButton: HTMLButtonElement = BootstrapUtility.createButtonSecondary();
        form.appendChild(historyButton);
        historyButton.textContent="History";
        historyButton.onclick=(e)=>{
            this.model.viewModel!.showHistoryWidget();
        };
        historyButton.style.marginRight="10px";


        let saveButton: HTMLButtonElement = BootstrapUtility.createButtonSuccess();
        saveButton.textContent="Save";
        saveButton.onclick=(e)=>{
            let factoryEditor: FactoryEditorModel = this.model.factoryEditorModel;
            if (factoryEditor!.getWidget()!.validate()){
                this.model.viewModel!.showSaveContent();

                // this.rootNode.view.get().f
                // this.view.show(this.saveWidget)
            }
        };
        form.appendChild(saveButton);

        nav.appendChild(navbarBrand);
        nav.appendChild(navbarNav);
        nav.appendChild(form);
        return nav;
    }
}