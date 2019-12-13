import {WidgetModel} from "../../base/WidgetModel";
import {BooleanValue} from "../../base/BooleanValue";
import {HttpClient} from "../../HttpClient";
import {ViewModel} from "../view/ViewModel";
import {HistoryWidget} from "./HistoryWidget";

export class HistoryWidgetModel extends WidgetModel<HistoryWidget> {
    public readonly visible: BooleanValue = new BooleanValue();

    constructor(public httpClient: HttpClient){
        super()
    }

    protected createWidget(): HistoryWidget {
        return new HistoryWidget(this);
    }

    viewModel?: ViewModel;
    setViewModel(viewModel: ViewModel) {
        this.viewModel=viewModel;
    }

}