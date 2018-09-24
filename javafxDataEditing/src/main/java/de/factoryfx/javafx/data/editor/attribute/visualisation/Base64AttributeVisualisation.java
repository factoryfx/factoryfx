package de.factoryfx.javafx.data.editor.attribute.visualisation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import de.factoryfx.javafx.data.editor.attribute.ValidationDecoration;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;

import de.factoryfx.data.attribute.types.Base64Attribute;
import de.factoryfx.data.util.LanguageText;
import de.factoryfx.javafx.data.editor.attribute.ValueAttributeVisualisation;
import de.factoryfx.javafx.data.util.UniformDesign;

public class Base64AttributeVisualisation extends ValueAttributeVisualisation<String,Base64Attribute> {

    private final Base64Attribute bytes;
    private final UniformDesign uniformDesign;
    private final static LanguageText EMPTY = new LanguageText().en("Value not set").de("Kein Wert gesetzt");
    private final static LanguageText NOT_EMPTY = new LanguageText().en("Value set").de("Wert gesetzt");

    public Base64AttributeVisualisation(Base64Attribute bytes, ValidationDecoration validationDecoration, UniformDesign uniformDesign) {
        super(bytes, validationDecoration);
        this.bytes = bytes;
        this.uniformDesign = uniformDesign;
    }

    private String labelText() {
        return uniformDesign.getText((bytes == null || bytes.get() == null || bytes.getBytes().length == 0) ? EMPTY : NOT_EMPTY);
    }

    @Override
    public Node createValueVisualisation() {
        HBox hBox = new HBox(3);
        Label status = new Label(labelText());
        observableAttributeValue.addListener(observable -> status.setText(labelText()));

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        if (bytes.internal_getFileExtension() != null) {
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Files", "*." + bytes.internal_getFileExtension()));
        }

        final Button openButton = new Button("Open Resource File...");
        openButton.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(openButton.getScene().getWindow());
            if (file != null) {
                openFile(file);
            }
        });

        final Button clear = new Button("Clear");
        clear.setOnAction(e -> bytes.set((String) null));

        hBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(status, Priority.SOMETIMES);
        hBox.getChildren().addAll(openButton, clear, status);
        hBox.disableProperty().bind(readOnly);
        return hBox;
    }

    private void openFile(File file) {
        try {
            bytes.set(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
