package de.factoryfx.javafx.distribution.launcher.ui;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.google.common.base.Throwables;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.filter.EncodingFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.message.DeflateEncoder;
import org.glassfish.jersey.message.GZipEncoder;

public class UserInterfaceDistributionClientController {

    public static final String GUI_ZIP = "gui.zip";
    @FXML
    private ComboBox<String> serverUrlList;

    @FXML
    private Button startButton;

    @FXML
    private ProgressBar progress;

    @FXML
    private TextField serverUrlInput;

    @FXML
    private VBox rootPane;


    public UserInterfaceDistributionClientController() {

    }

    @FXML
    void initialize() {
        readServerList();

        startButton.setOnAction(event -> {
            startGui();
        });

        startButton.disableProperty().bind(serverUrlInput.textProperty().isEmpty());

        serverUrlList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            serverUrlInput.setText(newValue);
        });
    }

    private void startGui() {
        try {
            String serverUrl=serverUrlInput.getText();

            JacksonFeature jacksonFeature = new JacksonFeature();
            ClientConfig cc = new ClientConfig().register(jacksonFeature);
            Client client = ClientBuilder.newClient(cc);
            client.register(GZipEncoder.class);
            client.register(EncodingFilter.class);
            client.register(DeflateEncoder.class);

            progress.setProgress(-1);

            File guiFolder = new File("./" + serverUrl.hashCode());
            String fileHash = "";
            if (guiFolder.exists()) {
                fileHash = Files.hash(new File(guiFolder, GUI_ZIP), Hashing.md5()).toString();
            }

            JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
            provider.setMapper(new ObjectMapper());
            client.register(provider);
            WebTarget webResource = client.target(serverUrl + "/download/checkVersion?" + "fileHash=" + fileHash);
            Response response = webResource.request(MediaType.TEXT_PLAIN).get();
            boolean needUpdate = Boolean.parseBoolean(response.readEntity(String.class));

            if (needUpdate) {
                rootPane.setDisable(true);
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        WebTarget webResourceDownload = client.target(serverUrl + "/download/");
                        Response responseDownload = webResourceDownload.request("application/zip").get();
                        File tempDownloadFile = responseDownload.readEntity(File.class);
                        File newFile = new File(guiFolder, GUI_ZIP);
                        try {
                            mkdir(guiFolder);
                            Files.move(tempDownloadFile, newFile);
                            unzip(newFile.getAbsolutePath(), newFile.getParent());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        Platform.runLater(()->rootPane.setDisable(false));
                    }
                }.start();

//                deleteFolder(guifolder); TODO required?
            }


//            new ProcessBuilder(guiFolder.getAbsolutePath() + "/gui/bin/UserInterface.bat").start();
            URL distributionServerURL = new URL(serverUrl);
            URL serverURL = new URL(distributionServerURL.getProtocol(),distributionServerURL.getHost(),8100,distributionServerURL.getPath());
            new ProcessBuilder(guiFolder.getAbsolutePath() + "/project.exe",serverURL.toExternalForm()).directory(new File(guiFolder.getAbsolutePath(),"./gui/bin")).inheritIO().start();
//                System.exit(0);

            progress.setProgress(1);
            if (!serverUrlList.getItems().contains(serverUrl)) {
                serverUrlList.getItems().add(serverUrl);
            }
            writeServerList();
        } catch (Exception e){
            Alert alter = new Alert(Alert.AlertType.ERROR);
            alter.setContentText("Error");
            TextArea textArea = new TextArea();
            textArea.setText(Throwables.getStackTraceAsString(e));
            alter.setGraphic(textArea);
            alter.show();
            progress.setProgress(0);
        }
    }

    private void mkdir(File folder) throws IOException {
        if (!folder.exists() && !folder.mkdirs()) {
            throw new IOException("Could not create directory "+ folder.getAbsolutePath());
        }
    }

    public void unzip(String zipFilePath, String destDirectory) throws IOException {
        File destDir = new File(destDirectory);
        mkdir(destDir);
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = zipIn.getNextEntry();
        // iterates over entries in the zip file
        while (entry != null) {
            String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                // if the entry is a file, extracts it
                extractFile(zipIn, filePath);
            } else {
                // if the entry is a directory, make the directory
                File dir = new File(filePath);
                mkdir(dir);
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
    }
    private static final int BUFFER_SIZE = 4096;
    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }


    public static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if(files!=null) { //some JVMs return null for empty dirs
            for(File f: files) {
                if(f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }

    private void readServerList() {
        File file = new File("./serverList.txt");
        if (file.exists()){
            Path path = Paths.get(file.toURI());
            try {
                serverUrlList.getItems().addAll(java.nio.file.Files.readAllLines(path, StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private void writeServerList() {
        Path path = Paths.get(new File("./serverList.txt").toURI());
        try {
            java.nio.file.Files.write(path, serverUrlList.getItems(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
