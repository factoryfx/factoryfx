package io.github.factoryfx.javafx.distribution.launcher.ui;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Set;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javafx.application.Platform;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.client.filter.EncodingFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.glassfish.jersey.message.DeflateEncoder;
import org.glassfish.jersey.message.GZipEncoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.hash.Hashing;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class UserInterfaceDistributionClientController {

    public static final String GUI_ZIP = "gui.zip";
    private final String httpAuthenticationUser;
    private final String httpAuthenticationPassword;
    private final String exeName;
    private final Supplier<Client> clientBuilder;

    /**
     *
     * @param exeName  e.g 'project', optional include '.exe' or '.bat'
     * @param httpAuthenticationUser user
     * @param httpAuthenticationPassword password
     * @param clientBuilder could be used to enable ssl see <a href="https://stackoverflow.com/questions/2145431/https-using-jersey-client">stackoverflow.</a>
     */
    public UserInterfaceDistributionClientController(String exeName, String httpAuthenticationUser, String httpAuthenticationPassword, Supplier<Client> clientBuilder) {
        this.httpAuthenticationUser = httpAuthenticationUser;
        this.httpAuthenticationPassword = httpAuthenticationPassword;
        this.exeName = exeName;
        this.clientBuilder = clientBuilder;
    }

    public UserInterfaceDistributionClientController(String exeName, String httpAuthenticationUser, String httpAuthenticationPassword) {
        this(exeName, httpAuthenticationUser, httpAuthenticationPassword, () -> {
            JacksonFeature jacksonFeature = new JacksonFeature();
            ClientConfig cc = new ClientConfig().register(jacksonFeature);
            return ClientBuilder.newClient(cc);
        });
    }


    @SuppressWarnings("deprecated")
    public void startGui(String url, Runnable onSuccess, Runnable onError) {
        String serverUrl = url.endsWith("/") ? url.substring(0, url.length() - 1) : url;

        Client client = clientBuilder.get();

        client.register(GZipEncoder.class);
        client.register(EncodingFilter.class);
        client.register(DeflateEncoder.class);
        JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
        provider.setMapper(new ObjectMapper());
        client.register(provider);
        if (!Strings.isNullOrEmpty(httpAuthenticationUser) && !Strings.isNullOrEmpty(httpAuthenticationPassword)) {
            client.register(HttpAuthenticationFeature.basic(httpAuthenticationUser, httpAuthenticationPassword));
        }


        File guiFolder = new File("./" + serverUrl.hashCode());
        String fileHash = "";
        if (guiFolder.exists()) {
            try {
                fileHash = com.google.common.io.Files.asByteSource(new File(guiFolder, GUI_ZIP)).hash(Hashing.md5()).toString();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        String checkVersionUri = serverUrl + "/download/checkVersion?fileHash=" + fileHash;
        WebTarget webResource = client.target(checkVersionUri);
        Response response = webResource.request(MediaType.TEXT_PLAIN).get();
        if (response.getStatus() != 200) {
            throw new RuntimeException(response.getStatus() + "\nReceived http status code " + response.getStatus() + "\n" + checkVersionUri + "\n" + response.readEntity(String.class));
        }
        boolean needUpdate = Boolean.parseBoolean(response.readEntity(String.class));


        new Thread("User Interface Download Thread") {
            @Override
            public void run() {
                try {
                    if (needUpdate) {
                        WebTarget webResourceDownload = client.target(serverUrl + "/download/");
                        Response responseDownload = webResourceDownload.request("application/zip").get();

                        File newFile = new File(guiFolder, GUI_ZIP);
                        mkdir(guiFolder);

                        try (InputStream in = responseDownload.readEntity(InputStream.class)) {
                            Files.copy(in, newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        }

                        unzip(newFile.getAbsolutePath(), newFile.getParent());
                    }

                    Set<String> executableCandidates = Set.of(exeName, exeName + ".exe", exeName + ".bat");
                    var exec = new Object() { String s = null;};
                    Files.walkFileTree(guiFolder.toPath(), new SimpleFileVisitor<>() {
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            if(executableCandidates.contains(file.getFileName().toString())){
                                exec.s = file.toAbsolutePath().toString();
                            }
                            return super.visitFile(file, attrs);
                        }
                    });
                    if(exec.s == null) {
                        throw new FileNotFoundException(exeName + " not found.");
                    }

                    URL distributionServerURL = new URL(serverUrl);
                    new ProcessBuilder(exec.s, distributionServerURL.toExternalForm()).directory(new File(guiFolder.getAbsolutePath(), "./")).inheritIO().start();
                    Platform.runLater(onSuccess);
                } catch (IOException e) {
                    Platform.runLater(onError);
                    throw new RuntimeException(e);
                }
            }
        }.start();

    }

    private void mkdir(File folder) throws IOException {
        if (!folder.exists() && !folder.mkdirs()) {
            throw new IOException("Could not create directory " + folder.getAbsolutePath());
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
        int read;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }
}
