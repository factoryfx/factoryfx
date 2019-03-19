package io.github.factoryfx.javafx.data.editor.attribute.visualisation;

import io.github.factoryfx.data.attribute.types.FileContentAttribute;
import io.github.factoryfx.data.util.LanguageText;
import io.github.factoryfx.javafx.data.editor.attribute.ValidationDecoration;
import io.github.factoryfx.javafx.data.editor.attribute.ValueAttributeVisualisation;
import io.github.factoryfx.javafx.data.util.UniformDesign;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.function.Function;

public class FileContentAttributeVisualisation extends ValueAttributeVisualisation<byte[], FileContentAttribute> {

    private final FileContentAttribute bytes;
    private final UniformDesign uniformDesign;

    private final static Function<String, LanguageText> notEmptyTextCreator = fileName -> {
        if (fileName != null) {
            return new LanguageText().en("Value set (" + fileName + ")").de("Wert gesetzt (" + fileName + ")");
        } else {
            return new LanguageText().en("Value set").de("Wert gesetzt");
        }
    };
    private final static LanguageText EMPTY = new LanguageText().en("Value not set").de("Kein Wert gesetzt");
    private StringProperty openedFileName;

    public FileContentAttributeVisualisation(FileContentAttribute bytes, ValidationDecoration validationDecoration, UniformDesign uniformDesign) {
        super(bytes, validationDecoration);
        this.bytes = bytes;
        this.uniformDesign = uniformDesign;
        this.openedFileName = new SimpleStringProperty();
    }

    private String labelText() {
        return uniformDesign.getText((bytes == null || bytes.get() == null || bytes.get().length == 0) ? EMPTY : notEmptyTextCreator.apply(openedFileName.get()));
    }

    @Override
    public Node createValueVisualisation() {
        HBox hBox = new HBox(3);
        Label status = new Label(labelText());

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
                openedFileName.set(file.getName());
            }
        });

        final Button clear = new Button("Clear");
        clear.setOnAction(e -> {
            bytes.set(null);
            openedFileName.set(null);
        });

        openedFileName.addListener((obs, oldValue, newValue) -> status.setText(labelText()));

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
