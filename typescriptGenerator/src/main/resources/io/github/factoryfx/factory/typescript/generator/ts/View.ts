import {Widget} from "./Widget";
import {DomUtility} from "./DomUtility";
import {Navbar} from "./Navbar";

export class View implements Widget{
    private readonly contentDiv: HTMLDivElement;
    private readonly navbarDiv: HTMLDivElement;
    private readonly viewDiv: HTMLDivElement;

    constructor(){
        this.contentDiv = document.createElement("div");
        this.viewDiv = document.createElement("div");
        this.navbarDiv = document.createElement("div");
        this.viewDiv.appendChild(this.navbarDiv);
        this.viewDiv.appendChild(this.contentDiv);
    }

    public setNavbar(navbar: Navbar){
        DomUtility.clear(this.navbarDiv);
        this.navbarDiv.appendChild(navbar.create());
    }

    show(widget: Widget){
        DomUtility.clear(this.contentDiv);
        this.contentDiv.appendChild(widget.create());
    }

    create(): HTMLElement {
        return this.viewDiv;
    }

}