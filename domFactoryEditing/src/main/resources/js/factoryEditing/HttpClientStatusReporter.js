export class HttpClientStatusReporter {
    setRootModel(rootModel) {
        this.rootModel = rootModel;
    }
    showWaitAnimation() {
        if (!this.rootModel) {
            return;
        }
        this.rootModel.showWaitAnimation();
    }
    hideWaitAnimation() {
        if (!this.rootModel) {
            return;
        }
        this.rootModel.hideWaitAnimation();
    }
    reportError(error) {
        if (!this.rootModel) {
            console.log(error);
            return;
        }
        this.rootModel.reportError(error);
    }
}
