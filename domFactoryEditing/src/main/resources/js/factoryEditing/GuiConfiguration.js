//generated code don't edit manually
import { NavItem } from "./NavItem";
import { Navbar } from "./Navbar";
import { SaveWidget } from "./SaveWidget";
export class GuiConfiguration {
    constructor(guiConfigurationJson, factoryEditor, root, baseVersionId, view, waitAnimation) {
        this.guiConfigurationJson = guiConfigurationJson;
        this.factoryEditor = factoryEditor;
        this.root = root;
        this.baseVersionId = baseVersionId;
        this.view = view;
        this.waitAnimation = waitAnimation;
    }
    createNavbar() {
        let navItems = [];
        for (let navItemsJson of this.guiConfigurationJson.navBarItems) {
            navItems.push(new NavItem(navItemsJson.text, this.root.getChildFromRoot(navItemsJson.factoryId), this.factoryEditor));
        }
        return new Navbar(this.guiConfigurationJson.projectName, navItems, this.factoryEditor, this.view, new SaveWidget(this.root, this.baseVersionId, this.view, this.waitAnimation));
    }
}
