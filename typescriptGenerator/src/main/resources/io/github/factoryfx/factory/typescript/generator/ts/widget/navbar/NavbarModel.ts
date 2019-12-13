import {WidgetModel} from "../../base/WidgetModel";
import {Navbar} from "./Navbar";
import {StringValue} from "../../base/StringValue";
import {FactoryEditorModel} from "../factoryeditor/FactoryEditorModel";
import {NavitemModel} from "../navitem/NavitemModel";
import {ViewModel} from "../view/ViewModel";


export class NavbarModel extends WidgetModel<Navbar> {

    public readonly projectName: StringValue = new StringValue();
    public viewModel?: ViewModel;

    constructor(public navItems: NavitemModel[], public factoryEditorModel: FactoryEditorModel){
        super();
    }


    protected createWidget(): Navbar {
        return new Navbar(this);
    }

    setViewModel(viewModel: ViewModel) {
        this.viewModel=viewModel;
    }
}