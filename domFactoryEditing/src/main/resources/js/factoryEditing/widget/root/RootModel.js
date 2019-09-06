//generated code don't edit manually
import { WidgetModel } from "../../base/WidgetModel";
import { Root } from "./Root";
export class RootModel extends WidgetModel {
    constructor(view, waitAnimationModel, errorReporterModel) {
        super();
        this.view = view;
        this.waitAnimationModel = waitAnimationModel;
        this.errorReporterModel = errorReporterModel;
    }
    createWidget() {
        return new Root(this.view.getWidget(), this.waitAnimationModel.getWidget(), this.errorReporterModel.getWidget());
    }
    showWaitAnimation() {
        this.waitAnimationModel.visible.set(true);
        this.view.visible.set(false);
    }
    hideWaitAnimation() {
        this.waitAnimationModel.visible.set(false);
        this.view.visible.set(true);
    }
    reportError(error) {
        this.waitAnimationModel.visible.set(false);
        this.view.visible.set(false);
        this.errorReporterModel.visible.set(true);
        this.errorReporterModel.errorText.set(error);
    }
}
