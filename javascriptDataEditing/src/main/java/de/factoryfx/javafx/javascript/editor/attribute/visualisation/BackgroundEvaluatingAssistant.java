package de.factoryfx.javafx.javascript.editor.attribute.visualisation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class BackgroundEvaluatingAssistant<I> {

    private final static Logger logger = LoggerFactory.getLogger(BackgroundEvaluatingAssistant.class);

    private ComputationTask<I> thread;
    private BlockingQueue<I> inputQueue = new ArrayBlockingQueue<>(1);
    private final Object lock = new Object();

    public BackgroundEvaluatingAssistant() {
        this.thread = new ComputationTask<I>(inputQueue, i->{});
    }

    public void update(I input) {
        synchronized (lock) {
            inputQueue.clear();
            inputQueue.add(input);
            executor.submit(thread.runnable());
        }
    }

    public void start(Consumer<I> processor) {
        synchronized (lock) {
            this.thread = new ComputationTask<I>(inputQueue,processor);
        }
    }

    public void stop() {
        synchronized (lock) {
            this.thread = new ComputationTask<I>(inputQueue, i->{});
        }
    }

    private final static ThreadPoolExecutor executor;
    static {
        ThreadGroup group = new ThreadGroup("assistants");
        int numThread = Math.max(1,Runtime.getRuntime().availableProcessors()-1);
        executor =new ThreadPoolExecutor(numThread, numThread, 1, TimeUnit.DAYS, new ArrayBlockingQueue<>(200), r-> {
                Thread t = new Thread(group,r);
                t.setDaemon(true);
                return t;
            }
        );
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    }

    static class ComputationTask<I> {

        private final BlockingQueue<I> inputQueue;
        private final Consumer<I> processor;

        public ComputationTask(BlockingQueue<I> inputQueue, Consumer<I> processor) {
            this.inputQueue = inputQueue;
            this.processor = processor;
        }

        public Runnable runnable() {
            return ()-> {
                try {
                    I input = inputQueue.poll();
                    if (input != null)
                        processor.accept(input);
                } catch (RuntimeException | Error ignored) {
                    logger.info("Could not compile source", ignored);
                }
            };
        }



    }
}
