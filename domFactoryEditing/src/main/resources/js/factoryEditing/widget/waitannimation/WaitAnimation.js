//generated code don't edit manually
import { Widget } from "../../base/Widget";
import { BootstrapUtility } from "../../BootstrapUtility";
export class WaitAnimation extends Widget {
    constructor(model) {
        super();
        this.model = model;
    }
    render() {
        let modal = document.createElement("div");
        if (!this.model.visible.get()) {
            return modal;
        }
        modal.style.position = "fixed"; /* Stay in place */
        modal.style.zIndex = "2"; /* Sit on top */
        modal.style.left = "0";
        modal.style.top = "0";
        modal.style.right = "0";
        modal.style.bottom = "0";
        modal.style.width = "100%";
        modal.style.height = "100%";
        modal.style.overflow = "auto"; /* Enable scroll if needed */
        modal.style.backgroundColor = "rgba(0,0,0,0.4)";
        modal.onclick = (e) => {
            e.preventDefault();
        };
        modal.append(BootstrapUtility.createProgressBar());
        return modal;
    }
}
