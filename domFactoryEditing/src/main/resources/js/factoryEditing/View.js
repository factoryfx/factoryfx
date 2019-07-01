import { DomUtility } from "./DomUtility";
export class View {
    constructor() {
        this.contentDiv = document.createElement("div");
        this.viewDiv = document.createElement("div");
        this.navbarDiv = document.createElement("div");
        this.viewDiv.appendChild(this.navbarDiv);
        this.viewDiv.appendChild(this.contentDiv);
    }
    setNavbar(navbar) {
        DomUtility.clear(this.navbarDiv);
        this.navbarDiv.appendChild(navbar.create());
    }
    show(widget) {
        DomUtility.clear(this.contentDiv);
        this.contentDiv.appendChild(widget.create());
    }
    create() {
        return this.viewDiv;
    }
}
