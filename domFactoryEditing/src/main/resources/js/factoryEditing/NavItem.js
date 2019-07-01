export class NavItem {
    constructor(text, factory, factoryEditor) {
        this.text = text;
        this.factory = factory;
        this.factoryEditor = factoryEditor;
    }
    create() {
        let navItem = document.createElement("a");
        navItem.className = "nav-item nav-link";
        navItem.textContent = this.text;
        navItem.onclick = (e) => {
            this.factoryEditor.edit(this.factory);
            e.preventDefault();
        };
        navItem.href = "#";
        return navItem;
    }
}
