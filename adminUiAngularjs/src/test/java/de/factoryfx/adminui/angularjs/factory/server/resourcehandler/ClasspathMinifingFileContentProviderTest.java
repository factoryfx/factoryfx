package de.factoryfx.adminui.angularjs.factory.server.resourcehandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;

public class ClasspathMinifingFileContentProviderTest {

    @Test
    public void test_all_src_files_exists(){
        ClasspathMinifingFileContentProvider classpathMinifingFileContentProvider=new ClasspathMinifingFileContentProvider();
        try (FileInputStream indexHtml=new FileInputStream("src/main/resources/webapp/index.html")){
            Document doc = Jsoup.parse(indexHtml, "UTF8", "/");
            List<String> paths = classpathMinifingFileContentProvider.combineIndexHtml(doc);
            for (String path: paths){
                File file = new File("src/main/resources/webapp/" + path);
                Assert.assertTrue(path, file.exists() && file.getCanonicalPath().endsWith(file.getName()));
            }
        } catch  (IOException e) {
            throw new RuntimeException(e);
        }
    }

}