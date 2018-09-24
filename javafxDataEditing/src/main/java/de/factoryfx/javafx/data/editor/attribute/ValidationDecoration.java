package de.factoryfx.javafx.data.editor.attribute;

import de.factoryfx.data.validation.ValidationError;
import de.factoryfx.javafx.data.util.UniformDesign;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.stage.PopupWindow;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ValidationDecoration {

    private final UniformDesign uniformDesign;
    private Consumer<List<ValidationError>> validationUpdater;
    private List<ValidationError> validationErrors=new ArrayList<>();
    private Tooltip tooltip;

    public ValidationDecoration(UniformDesign uniformDesign) {
        this.uniformDesign = uniformDesign;
    }

    public void update(List<ValidationError> validationErrors){
        this.validationErrors=validationErrors;
        if (validationUpdater!=null){
            validationUpdater.accept(validationErrors);
        }
    }

    public Node wrap(Node node) {

        if (node == null)
            return null;

        validationUpdater = (validationErrors) -> {
            boolean isValid = validationErrors.isEmpty();
            StringBuilder validationErrorText = new StringBuilder();
            int counter = 1;
            for (ValidationError validationError : validationErrors) {
                validationErrorText.append(counter);
                validationErrorText.append(": ");
                validationErrorText.append(validationError.validationDescription(uniformDesign::getText));
                validationErrorText.append("\n");
                counter++;
            }



//            final List<ValidationError> childErrors = validationErrors.stream().filter(e -> !e.isErrorFor(boundAttribute)).collect(Collectors.toList());
//            if (!childErrors.isEmpty()){
//                counter = 1;
//                validationErrorText.append("Error in Reference:\n");
//                for (ValidationError validationError : childErrors) {
//                    validationErrorText.append(counter);
//                    validationErrorText.append(": ");
//                    validationErrorText.append(validationError.validationDescriptionForChild(uniformDesign.getLocale()));
//                    validationErrorText.append("\n");
//                    counter++;
//                }
//            }

            if (tooltip==null){
                tooltip = new Tooltip();
            }

            tooltip.setText(validationErrorText.toString());
            tooltip.setShowDelay(Duration.ZERO);
            tooltip.setAnchorLocation(PopupWindow.AnchorLocation.CONTENT_BOTTOM_LEFT);
            tooltip.getStyleClass().add("errorTooltip");


            if (!isValid && !node.isDisabled()) {
                if (node.getStyleClass().stream().noneMatch(c -> c.equals("error"))) node.getStyleClass().add("error");

                Tooltip.install(node, tooltip);

            } else {
                node.getStyleClass().removeIf(c -> c.equals("error"));
                node.setOnMouseEntered(null);
                node.setOnMouseExited(null);
                Tooltip.uninstall(node, tooltip);
            }
        };
        validationUpdater.accept(validationErrors);
        return node;
    }
}
