//generated code don't edit manually
import {Data} from "./Data";
import {ValidationError} from "./ValidationError";
import {AttributeEditorCreator} from "./AttributeEditorCreator";


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

    edit(data: Data) {
        this.clear();
        this.parentElement.appendChild(this.createBreadCrumb(data));


        //You can't construct DOM elements using normal constructors because you're supposed to go through document.createElement

        let form: HTMLFormElement= document.createElement("form"); //new HTMLFormElement();

        let counter: number=0;
        for (let listAttributeAccessorElement of data.listAttributeAccessor()) {

            let formGroup: HTMLElement= document.createElement("div");
            formGroup.setAttribute("class","form-group row");
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
        console.log(new ValidationError("",""))

        // <div class="form-group">
        //     <label for="exampleInputEmail1">Email address</label>
        // <input type="email" class="form-control" id="exampleInputEmail1" aria-describedby="emailHelp" placeholder="Enter email">
        // <small id="emailHelp" class="form-text text-muted">We'll never share your email with anyone else.</small>
        // </div>


        // let listAttributeAccessor = data.listAttributeAccessor();
        // let attributeAccessor: AttributeAccessor<any,ExampleDataGenerated> = attributeAccessor[0];
        // for (let attributeAccessor in listAttributeAccessor){
        //     let attributeAccessor: AttributeAccessor<any,ExampleDataGenerated> = attributeAccessor[0];
        //     attributeAccessorElement
        //     (<AttributeAccessor<attributeAccessor,ExampleDataGenerated>attributeAccessor).attributeName
        // }


        // const form = document.createElement("form");

        // let properties: string[] = Reflect.getMetadata("editableProperties", obj) || [];
        // for (let property of properties) {
        //     const dataType = Reflect.getMetadata("design:type", obj, property) || property;
        //     const displayName = Reflect.getMetadata("displayName", obj, property) || property;
        //
        //     // create the label
        //     const label = document.createElement("label");
        //     label.textContent = displayName;
        //     label.htmlFor = property;
        //     form.appendChild(label);
        //
        //     // Create the input
        //     const input = document.createElement("input");
        //     input.id = property;
        //     if (dataType === String) {
        //         input.type = "text";
        //         input.addEventListener("input", e => obj[property] = input.value);
        //     } else if (dataType === Date) {
        //         input.type = "date";
        //         input.addEventListener("input", e => obj[property] = input.valueAsDate);
        //     } else if (dataType === Number) {
        //         input.type = "number";
        //         input.addEventListener("input", e => obj[property] = input.valueAsNumber);
        //     } else if (dataType === Boolean) {
        //         input.type = "checkbox";
        //         input.addEventListener("input", e => obj[property] = input.checked);
        //     }
        //
        //     form.appendChild(input);
        // }

        // parentElement.appendChild(form);

        // this.parentElement.textContent="bla";
    }
}