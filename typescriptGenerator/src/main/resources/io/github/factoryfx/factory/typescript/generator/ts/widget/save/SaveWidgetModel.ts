import {WidgetModel} from "../../base/WidgetModel";
import {SaveWidget} from "./SaveWidget";
import {BooleanValue} from "../../base/BooleanValue";
import {Data} from "../../Data";
import {HttpClient} from "../../HttpClient";
import {ViewModel} from "../view/ViewModel";

export class SaveWidgetModel extends WidgetModel<SaveWidget> {
    public readonly visible: BooleanValue = new BooleanValue();

    constructor(public baseVersionId: string , public rootFactory: Data, public httpClient: HttpClient){
        super()
    }

    protected createWidget(): SaveWidget {
        return new SaveWidget(this);
    }

    viewModel?: ViewModel;
    setViewModel(viewModel: ViewModel) {
        this.viewModel=viewModel;
    }

}