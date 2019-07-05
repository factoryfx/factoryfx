export class Navbar {
    constructor(projectName, navItems, factoryEditor, view, saveWidget) {
        this.projectName = projectName;
        this.navItems = navItems;
        this.factoryEditor = factoryEditor;
        this.view = view;
        this.saveWidget = saveWidget;
        this.factoryEditor.addOnFactoryChange(newData => {
            this.update();
        });
    }
    create() {
        let nav = document.createElement("nav");
        nav.className = "navbar navbar-expand-lg navbar-dark bg-dark";
        let navbarBrand = document.createElement("a");
        navbarBrand.className = "navbar-brand";
        navbarBrand.href = "#";
        navbarBrand.textContent = this.projectName;
        // let collapse: HTMLElement = document.createElement("div");
        // collapse.className="collapse navbar-collapse";
        let navbarNav = document.createElement("div");
        navbarNav.className = "navbar-nav mr-auto";
        for (let navItem of this.navItems) {
            navbarNav.appendChild(navItem.create());
        }
        let form = document.createElement("form");
        form.className = "form-inline";
        let saveButton = document.createElement("button");
        saveButton.type = "button";
        saveButton.textContent = "Save";
        saveButton.className = "btn btn-outline-success";
        saveButton.onclick = (e) => {
            if (this.factoryEditor.validate()) {
                this.view.show(this.saveWidget);
            }
        };
        form.appendChild(saveButton);
        nav.appendChild(navbarBrand);
        nav.appendChild(navbarNav);
        nav.appendChild(form);
        return nav;
    }
    update() {
        for (let navItem of this.navItems) {
            navItem.update();
        }
    }
}
