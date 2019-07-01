
export class DomUtility {
    public static clear(target: HTMLElement) {
        while (target.firstElementChild) {
            target.firstElementChild.remove();
        }
    }
}