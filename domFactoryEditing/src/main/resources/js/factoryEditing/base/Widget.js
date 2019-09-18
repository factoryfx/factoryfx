export class Widget {
    bindModel() {
        this.reRender(this.render());
    }
    append(parent) {
        this.parent = parent;
        this.bindModel();
    }
    reRender(newRendered) {
        if (this.rendered && this.parent.contains(this.rendered)) {
            this.parent.replaceChild(newRendered, this.rendered);
        }
        else {
            this.parent.appendChild(newRendered);
        }
        this.rendered = newRendered;
    }
    renderOnce() {
        if (this.renderedToParent != this.parent) {
            this.renderedToParent = this.parent;
            this.reRender(this.render());
        }
    }
}
