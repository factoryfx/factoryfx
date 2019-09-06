import {WidgetModel} from "../../base/WidgetModel";
import {Root} from "./Root";
import {WaitAnimationModel} from "../waitannimation/WaitAnimationModel";
import {ViewModel} from "../view/ViewModel";
import {ErrorReporterModel} from "../errorreporter/ErrorReporterModel";


export class RootModel extends WidgetModel<Root> {

    constructor(private readonly view: ViewModel, private readonly waitAnimationModel: WaitAnimationModel, private readonly errorReporterModel: ErrorReporterModel ){
        super();
    }


    protected createWidget(): Root {
        return new Root(this.view.getWidget(),this.waitAnimationModel.getWidget(),this.errorReporterModel.getWidget());
    }

    showWaitAnimation(){
        this.waitAnimationModel.visible.set(true);
        this.view.visible.set(false);
    }

    hideWaitAnimation(){
        this.waitAnimationModel.visible.set(false);
        this.view.visible.set(true);
    }

    reportError(error: string){
        this.waitAnimationModel.visible.set(false);
        this.view.visible.set(false);

        this.errorReporterModel.visible.set(true);
        this.errorReporterModel.errorText.set(error);
    }

}