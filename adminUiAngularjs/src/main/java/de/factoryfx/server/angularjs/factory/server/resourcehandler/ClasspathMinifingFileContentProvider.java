package de.factoryfx.server.angularjs.factory.server.resourcehandler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.google.common.base.Joiner;
import com.google.common.io.ByteStreams;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClasspathMinifingFileContentProvider implements FileContentProvider {

    private static final Logger logger = LoggerFactory.getLogger(ClasspathMinifingFileContentProvider.class);

    private HashMap<String,byte[]> resourceCache= new HashMap<>();
    private HashMap<String,JavaScriptFile> javaScriptFiles= new HashMap<>();



    List<String> combineIndexHtml(Document doc){
        Elements scripts = doc.select("script");
        List<String> allJsFiles = new ArrayList<>();
        for (Element element : scripts) {
            allJsFiles.add(element.attr("src"));
            element.remove();
        }
        doc.getElementsByTag("head").prepend("<script src=\"combined.js\"></script>");
        resourceCache.put("combined.js", combineFiles(allJsFiles));
        return allJsFiles;
    }


    public ClasspathMinifingFileContentProvider(){
        logger.info("start javascript minification");

        for (String resourcePath: getWebAppResources()){
            try (InputStream inputstream=getClass().getResourceAsStream(resourcePath)){
                resourceCache.put(resourcePath.replace("/webapp/", ""), ByteStreams.toByteArray(inputstream));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        for (Map.Entry<String,byte[]> entry: resourceCache.entrySet()){
            String path = entry.getKey();
            if (path.endsWith(".js") && !path.endsWith("min.js") ){
                javaScriptFiles.put(path,new JavaScriptFile(entry.getValue(),resourceCache.get(path.replaceAll(".js$",".min.js")),path));
            }
        }



        try (InputStream inputStream=getClass().getResourceAsStream("/webapp/index.html")) {
            Document doc = Jsoup.parse(inputStream, "UTF8", "/");
            replaceIndexHtmlPlaceholder(doc);
            {
                logger.info("combine javascript files");
                List<String> allJsFiles = combineIndexHtml(doc);
                resourceCache.put("combined.js", combineFiles(allJsFiles));
            }

            {
//                css combine buggy cause relative paths in css file like fonts
            }

            resourceCache.put("index.html", doc.html().getBytes(StandardCharsets.UTF_8));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    //guava classpath scanner don't work on buildserver reason unknown
    //
    private List<String> getWebAppResources() {
        try {
            final ArrayList<String> result = new ArrayList<>();
            final String path = "webapp/";
            final File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());

            if (jarFile.isFile()) {  // Run with JAR file
                final JarFile jar;
                jar = new JarFile(jarFile);
                final Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    final String name = entries.nextElement().getName();
                    if (name.startsWith(path) && !name.endsWith("/")) {
                        result.add("/"+name);
                    }
                }
                jar.close();
            } else { // Run with IDE
                final URL url = ClasspathMinifingFileContentProvider.class.getResource("/" + path);
                final Path base = Paths.get(url.toURI());
                Files.walkFileTree(base, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        result.add("/"+path+base.relativize(file).toString().replace("\\","/"));
                        return super.visitFile(file, attrs);
                    }
                });
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

//
//        ArrayList<String> result = new ArrayList<>();
//        try {
//            for (ClassPath.ResourceInfo resourceInfo: ClassPath.from(getClass().getClassLoader()).getResources()) {
//    //                System.err.println("qqqq:"+resourceInfo.getResourceName());
//                if (!resourceInfo.getResourceName().endsWith("class") && resourceInfo.getResourceName().startsWith("de/scoopgmbh/xtc/xmonweb/webapp/")) {
//                    String resourceName = "/" + resourceInfo.getResourceName();
//                    result.add(resourceName);
//                }
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        return result;
    }

    public byte[] combineFiles(List<String> allFiles) {
        ByteArrayOutputStream result = new ByteArrayOutputStream( );
        for (String file: allFiles){
            try {
                if (getFile(file)==null){
                    Joiner.MapJoiner mapJoiner = Joiner.on("\n").withKeyValueSeparator("=");
                    throw new IllegalStateException("missing file:"+file+"\navaible files:\n"+mapJoiner.join(javaScriptFiles));
                }
                result.write(getFile(file));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return result.toByteArray();
    }

    @Override
    public boolean containsFile(String file) {
        return resourceCache.containsKey(file);
    }

    @Override
    public byte[] getFile(String file) {
        if (javaScriptFiles.containsKey(file)){
            return javaScriptFiles.get(file).contentMinified;
        }

        return resourceCache.get(file);
    }
}
