package io.github.factoryfx.factory.storage.filesystem;

import io.github.factoryfx.factory.FactoryDepTopDownTest;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.merge.testdata.ExampleDataA;
import io.github.factoryfx.factory.storage.StoredDataMetadata;
import io.github.factoryfx.factory.storage.migration.MigrationManager;
import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadataDictionary;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

public class FileSystemFactoryStorageHistoryTest {

    public static class RootFactory extends SimpleFactoryBase<Void, RootFactory> {
        @Override
        protected Void createImpl() {
            return null;
        }
    }

    @Test
    public void testHouseKeeping() throws IOException {
        Path tempDir = Paths.get("killme");
        if (Files.exists(tempDir))
            removeRecursive(tempDir);
        if (!Files.exists(tempDir))
            Files.createDirectory(tempDir);

        try {
            FileSystemFactoryStorageHistory<RootFactory, Void> history = new FileSystemFactoryStorageHistory<>(tempDir,
                    new MigrationManager<>(RootFactory.class, ObjectMapperBuilder.build(), (root1, oldDataStorageMetadataDictionary) -> { }),2);
            RootFactory rf = new RootFactory().internal().finalise();
            LocalDateTime time = LocalDateTime.of(2000,1,1,0,0,0);
            StoredDataMetadata<Void> md1 = randomMetadata(time, rf);
            history.updateHistory(rf, md1);
            time = time.plusSeconds(1);
            StoredDataMetadata<Void> md2 = randomMetadata(time, rf);
            history.updateHistory(rf, md2);
            Assertions.assertEquals(2,history.getHistoryFactoryList().size());
            Assertions.assertNotNull(history.getHistoryFactory(md1.id));
            Assertions.assertNotNull(history.getHistoryFactory(md2.id));
            time = time.plusSeconds(1);
            StoredDataMetadata<Void> md3 = randomMetadata(time, rf);
            history.updateHistory(rf, md3);
            Assertions.assertEquals(2,history.getHistoryFactoryList().size());
            Assertions.assertNotNull(history.getHistoryFactory(md2.id));
            Assertions.assertNotNull(history.getHistoryFactory(md3.id));
            try {
                history.getHistoryFactory(md1.id);
                Assertions.fail("Expected exception");
            } catch (Exception expected) {
            }
            Assertions.assertEquals(4,Files.list(tempDir.resolve("history")).filter(p->Files.isRegularFile(p)).count());

        } finally {
            removeRecursive(tempDir);
        }
    }

    private void removeRecursive(Path tempDir) throws IOException {
        Files.walkFileTree(tempDir,new SimpleFileVisitor<>(){
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return super.visitFile(file,attrs);
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                try {
                    Files.delete(dir);
                } catch (DirectoryNotEmptyException ignored) {
                    dir.toFile().deleteOnExit();
                }
                return super.postVisitDirectory(dir,exc);
            }
        });
    }

    private StoredDataMetadata<Void> randomMetadata(LocalDateTime creationTime, RootFactory rootFactory) {
        DataStorageMetadataDictionary dataStorageMetadataDictionary = rootFactory.internal().createDataStorageMetadataDictionaryFromRoot();
        return new StoredDataMetadata<>( creationTime,UUID.randomUUID().toString(), "", "", "",null,
                dataStorageMetadataDictionary,null);
    }

}