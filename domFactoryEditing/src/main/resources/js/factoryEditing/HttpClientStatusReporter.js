export class HttpClientStatusReporter {
    setRootModel(rootModel) {
        this.rootModel = rootModel;
    }
    showWaitAnimation() {
        if (!this.rootModel) {
            return;
        }
        this.timer = setTimeout(() => {
            this.rootModel.showWaitAnimation();
        }, 1000);
    }
    hideWaitAnimation() {
        if (this.timer) {
            clearTimeout(this.timer);
        }
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
