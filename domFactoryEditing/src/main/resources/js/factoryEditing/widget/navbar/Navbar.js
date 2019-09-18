//generated code don't edit manually
import { Widget } from "../../base/Widget";
import { BootstrapUtility } from "../../BootstrapUtility";
export class Navbar extends Widget {
    constructor(model) {
        super();
        this.model = model;
    }
    render() {
        let nav = document.createElement("nav");
        nav.className = "navbar navbar-expand-lg navbar-dark bg-dark";
        let navbarBrand = document.createElement("a");
        navbarBrand.className = "navbar-brand";
        navbarBrand.href = "#";
        navbarBrand.textContent = this.model.projectName.get();
        // let collapse: HTMLElement = document.createElement("div");
        // collapse.className="collapse navbar-collapse";
        let navbarNav = document.createElement("div");
        navbarNav.className = "navbar-nav mr-auto";
        for (let navItem of this.model.navItems) {
            navItem.getWidget().append(navbarNav);
        }
        let form = document.createElement("form");
        form.className = "form-inline";
        let historyButton = BootstrapUtility.createButtonSecondary();
        form.appendChild(historyButton);
        historyButton.textContent = "History";
        historyButton.onclick = (e) => {
            this.model.viewModel.showHistoryWidget();
        };
        historyButton.style.marginRight = "10px";
        let saveButton = BootstrapUtility.createButtonSuccess();
        saveButton.textContent = "Save";
        saveButton.onclick = (e) => {
            let factoryEditor = this.model.factoryEditorModel;
            if (factoryEditor.getWidget().validate()) {
                this.model.viewModel.showSaveContent();
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
