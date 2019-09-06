export class Widget {
    bindModel() {
        let newRendered = this.render();
        if (this.parent) {
            if (this.rendered) {
                this.parent.replaceChild(newRendered, this.rendered);
            }
            else {
                this.parent.appendChild(newRendered);
            }
        }
        this.rendered = newRendered;
    }
    append(parent) {
        this.parent = parent;
        this.rendered = this.render();
        this.parent.appendChild(this.rendered);
    }
}
