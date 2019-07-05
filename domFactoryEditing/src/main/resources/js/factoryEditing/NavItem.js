export class NavItem {
    constructor(factory, factoryEditor) {
        this.factory = factory;
        this.factoryEditor = factoryEditor;
        this.navItem = document.createElement("a");
    }
    create() {
        this.navItem.className = "nav-item nav-link";
        this.navItem.textContent = this.factory.getDisplayText();
        this.navItem.onclick = (e) => {
            this.factoryEditor.edit(this.factory);
            e.preventDefault();
        };
        this.navItem.href = "#";
        return this.navItem;
    }
    update() {
        if (this.factoryEditor.getCurrentData() === this.factory) {
            this.navItem.className = "nav-item nav-link active";
        }
        else {
            this.navItem.className = "nav-item nav-link";
        }
    }
}
