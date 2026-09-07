package io.github.factoryfx.factory.storage.filesystem;

import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.merge.testdata.ExampleDataA;
import io.github.factoryfx.factory.merge.testdata.ExampleDataB;
import io.github.factoryfx.factory.storage.DataUpdate;
import io.github.factoryfx.factory.storage.migration.MigrationManager;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

/**
 * manual benchmark for the configuration save path, run by hand to compare save performance (not part of the regular test suite)
 */
@Disabled("manual benchmark")
public class FileSystemDataStorageSavePerformanceTest {

    @TempDir
    public Path folder;

    private ExampleDataA createLargeExampleData(int children) {
        ExampleDataA exampleDataA = new ExampleDataA();
        for (int i = 0; i < children; i++) {
            ExampleDataB child = new ExampleDataB();
            child.stringAttribute.set("child" + i);
            exampleDataA.referenceListAttribute.add(child);
        }
        exampleDataA.internal().finalise();
        return exampleDataA;
    }

    @Test
    public void benchmark_save() {
        int children = 10000;
        int saves = 20;

        MigrationManager<ExampleDataA> migrationManager = new MigrationManager<>(ExampleDataA.class, ObjectMapperBuilder.build(), (root, dictionary) -> {});
        FileSystemDataStorage<ExampleDataA> storage = new FileSystemDataStorage<>(folder, createLargeExampleData(children), migrationManager, ObjectMapperBuilder.build());
        storage.getCurrentData();//init

        //warmup
        for (int i = 0; i < 5; i++) {
            storage.updateCurrentData(new DataUpdate<>(createLargeExampleData(children), "user", "comment", storage.getCurrentDataId()), null);
        }

        long start = System.nanoTime();
        for (int i = 0; i < saves; i++) {
            storage.updateCurrentData(new DataUpdate<>(createLargeExampleData(children), "user", "comment", storage.getCurrentDataId()), null);
        }
        long durationMs = (System.nanoTime() - start) / 1_000_000;
        System.out.println("saves: " + saves + ", tree size: " + children + " factories, total: " + durationMs + "ms, per save: " + (durationMs / saves) + "ms");
    }
}
