//generated code don't edit manually
import { Widget } from "../../base/Widget";
export class Navitem extends Widget {
    constructor(model) {
        super();
        this.model = model;
    }
    render() {
        let navItem = document.createElement("a");
        navItem.className = "nav-item nav-link";
        navItem.textContent = this.model.factory.get().getDisplayText();
        navItem.onclick = (e) => {
            this.model.factoryEditor.edit(this.model.factory.get());
            // this.factoryEditor.edit(this.factory);
            e.preventDefault();
        };
        navItem.href = "#";
        if (this.model.factoryEditor.getFactory() === this.model.factory.get()) {
            navItem.className = "nav-item nav-link active";
        }
        else {
            navItem.className = "nav-item nav-link";
        }
        return navItem;
    }
}
