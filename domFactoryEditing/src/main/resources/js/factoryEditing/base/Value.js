export class Value {
    get() {
        return this.value;
    }
    set(value) {
        this.value = value;
        if (this.parent) {
            this.parent.update();
        }
    }
    getChildren() {
        return [];
    }
    finalize(parent) {
        this.parent = parent;
    }
}
