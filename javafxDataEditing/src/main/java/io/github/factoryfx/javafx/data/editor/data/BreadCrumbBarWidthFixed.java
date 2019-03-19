package io.github.factoryfx.javafx.data.editor.data;

//import impl.org.controlsfx.skin.BreadCrumbBarSkin;
import org.controlsfx.control.BreadCrumbBar;

public class BreadCrumbBarWidthFixed<T> extends BreadCrumbBar<T> {
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    protected Skin<?> createDefaultSkin() {
//        return new BreadCrumbBarSkin<>(this) {
//
//            @Override
//            protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
//                double width = 0;
//                for (Node node : getChildren()) {
//                    if (node instanceof BreadCrumbButton) {
//                        width = width - ((BreadCrumbButton) node).getArrowWidth();
//                    }
//                    width += node.prefWidth(-1);
//                }
//                if (!getChildren().isEmpty()){
//                    width = width + ((BreadCrumbButton) getChildren().get(0)).getArrowWidth();
//                }
//                return width;
//            }
//
//        };
//    }
}
