import {Data} from "./Data";
import {ValidationError} from "./ValidationError";
import {AttributeEditorCreator} from "./AttributeEditorCreator";
import {FactoryChangeEvent} from "./FactoryChangeEvent";


export class FactoryEditor {
    constructor(private parentElement: HTMLElement, private attributeEditorCreator: AttributeEditorCreator) {


    }

    createBreadCrumb(data: Data): HTMLElement {
        let nav: HTMLElement = document.createElement("nav");
        nav.setAttribute("aria-label", "breadcrumb");

        let ol: HTMLElement = document.createElement("ol");
        ol.setAttribute("class", "breadcrumb");

        let counter: number=0;
        let path = data.getPath();
        for (let pathElement of path) {
            let li: HTMLElement = document.createElement("li");
            if (counter==path.length-1){
                li.setAttribute("class", "breadcrumb-item active");
                li.textContent=pathElement.getDisplayText();
            } else {
                let a: HTMLElement = document.createElement("a");
                a.setAttribute("href", "#");
                a.textContent=pathElement.getDisplayText();
                a.onclick= (e) => {
                    e.preventDefault();
                    this.edit(pathElement);
                    return false;
                };


                li.setAttribute("class", "breadcrumb-item");
                li.appendChild(a)
            }
            ol.appendChild(li);
            counter++;
        }

        nav.appendChild(ol);
        return nav;
    }

    private clear() {
        while (this.parentElement.firstElementChild) {
            this.parentElement.firstElementChild.remove();
        }
    }

    factoryChangeEvent: FactoryChangeEvent;
    public setOnFactoryChange(event: FactoryChangeEvent){
        this.factoryChangeEvent=event;
    }

    currentData: Data;
    edit(data: Data) {
        this.currentData=data;
        this.clear();
        this.parentElement.appendChild(this.createBreadCrumb(data));


        //You can't construct DOM elements using normal constructors because you're supposed to go through document.createElement

        let form: HTMLFormElement= document.createElement("form"); //new HTMLFormElement();

        let counter: number=0;
        for (let listAttributeAccessorElement of data.listAttributeAccessor()) {

            let formGroup: HTMLElement= document.createElement("div");
            formGroup.className="form-group row";
            formGroup.style.padding="0rem 1rem";

            let label: HTMLLabelElement = document.createElement("label");
            label.htmlFor=counter.toString();
            label.className="col-sm-2 col-form-label";
            label.textContent=listAttributeAccessorElement.getLabelText("en");

            let div: HTMLDivElement = document.createElement("div");
            div.className="col-sm-10";
            div.appendChild(this.attributeEditorCreator.create(listAttributeAccessorElement,counter.toString(),this).create());

            formGroup.appendChild(label);
            formGroup.appendChild(div);
            form.appendChild(formGroup);

            form.appendChild(document.createElement("hr"));
            counter++;
        }
        this.parentElement.appendChild(form);

        if (this.factoryChangeEvent){
            this.factoryChangeEvent.onChange(data);
        }
    }

     back(){
         let path: Array<Data> = this.currentData.getPath();
         if (path.length>=2){
             this.edit(path[path.length-2]);
         }
     }
}