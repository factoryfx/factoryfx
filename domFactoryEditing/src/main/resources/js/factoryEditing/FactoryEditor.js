export class FactoryEditor {
    constructor(parentElement, attributeEditorCreator) {
        this.parentElement = parentElement;
        this.attributeEditorCreator = attributeEditorCreator;
    }
    createBreadCrumb(data) {
        let nav = document.createElement("nav");
        nav.setAttribute("aria-label", "breadcrumb");
        let ol = document.createElement("ol");
        ol.setAttribute("class", "breadcrumb");
        let counter = 0;
        let path = data.getPath();
        for (let pathElement of path) {
            let li = document.createElement("li");
            if (counter == path.length - 1) {
                li.setAttribute("class", "breadcrumb-item active");
                li.textContent = pathElement.getDisplayText();
            }
            else {
                let a = document.createElement("a");
                a.setAttribute("href", "#");
                a.textContent = pathElement.getDisplayText();
                a.onclick = (e) => {
                    e.preventDefault();
                    this.edit(pathElement);
                    return false;
                };
                li.setAttribute("class", "breadcrumb-item");
                li.appendChild(a);
            }
            ol.appendChild(li);
            counter++;
        }
        nav.appendChild(ol);
        return nav;
    }
    clear() {
        while (this.parentElement.firstElementChild) {
            this.parentElement.firstElementChild.remove();
        }
    }
    setOnFactoryChange(event) {
        this.factoryChangeEvent = event;
    }
    edit(data) {
        this.currentData = data;
        this.clear();
        this.parentElement.appendChild(this.createBreadCrumb(data));
        //You can't construct DOM elements using normal constructors because you're supposed to go through document.createElement
        let form = document.createElement("form"); //new HTMLFormElement();
        let counter = 0;
        for (let listAttributeAccessorElement of data.listAttributeAccessor()) {
            let formGroup = document.createElement("div");
            formGroup.className = "form-group row";
            formGroup.style.padding = "0rem 1rem";
            let label = document.createElement("label");
            label.htmlFor = counter.toString();
            label.className = "col-sm-2 col-form-label";
            label.textContent = listAttributeAccessorElement.getLabelText("en");
            let div = document.createElement("div");
            div.className = "col-sm-10";
            div.appendChild(this.attributeEditorCreator.create(listAttributeAccessorElement, counter.toString(), this).create());
            formGroup.appendChild(label);
            formGroup.appendChild(div);
            form.appendChild(formGroup);
            form.appendChild(document.createElement("hr"));
            counter++;
        }
        this.parentElement.appendChild(form);
        if (this.factoryChangeEvent) {
            this.factoryChangeEvent.onChange(data);
        }
    }
    back() {
        let path = this.currentData.getPath();
        if (path.length >= 2) {
            this.edit(path[path.length - 2]);
        }
    }
}
