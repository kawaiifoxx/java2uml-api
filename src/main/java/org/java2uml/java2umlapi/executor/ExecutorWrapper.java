package org.java2uml.java2umlapi.executor;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.*;

/**
 * ExecutorWrapper is a wrapper for the executor service.
 *
 * @author kawaiifox
 */
@Component
public class ExecutorWrapper {
    private final ExecutorService executorService;

    public ExecutorWrapper() {
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }


    /**
     * Initiates an orderly shutdown in which previously submitted tasks are executed,
     * but no new tasks will be accepted.
     */
    public void shutdown() {
        executorService.shutdown();
    }


    /**
     * Blocks until all tasks have completed execution after a shutdown request, or the timeout occurs,
     * or the current thread is interrupted, whichever happens first.
     *
     * @param timeout the maximum time to wait
     * @param unit    the time unit of the timeout argument
     * @return true if this executor terminated and false if the timeout elapsed before termination
     * @throws InterruptedException if interrupted while waiting
     */
    public boolean awaitTermination(Long timeout, TimeUnit unit) throws InterruptedException {
        return executorService.awaitTermination(timeout, unit);
    }

    /**
     * Attempts to stop all actively executing tasks, halts the processing of waiting tasks, and returns
     * a list of the tasks that were awaiting execution. This method does not wait for actively executing
     * tasks to terminate. Use awaitTermination to do that.
     *
     * @return List of halted tasks.
     */
    public List<Runnable> shutdownNow() {
        return executorService.shutdownNow();
    }

    /**
     * Submits a Runnable task for execution and returns a Future
     * representing that task. The Future's {@code get} method will
     * return {@code null} upon <em>successful</em> completion.
     *
     * @param task the task to submit
     * @return a Future representing pending completion of the task
     * @throws RejectedExecutionException if the task cannot be
     *                                    scheduled for execution
     * @throws NullPointerException       if the task is null
     */
    public Future<?> submit(Runnable task) throws RejectedExecutionException {
        return executorService.submit(task);
    }

    /**
     * Submits a value-returning task for execution and returns a
     * Future representing the pending results of the task. The
     * Future's {@code get} method will return the task's result upon
     * successful completion.
     *
     * <p>
     * If you would like to immediately block waiting
     * for a task, you can use constructions of the form
     * {@code result = exec.submit(aCallable).get();}
     *
     * <p>Note: The {@link Executors} class includes a set of methods
     * that can convert some other common closure-like objects,
     * for example, {@link java.security.PrivilegedAction} to
     * {@link Callable} form so they can be submitted.
     *
     * @param task the task to submit
     * @param <T> the type of the task's result
     * @return a Future representing pending completion of the task
     * @throws RejectedExecutionException if the task cannot be
     *         scheduled for execution
     * @throws NullPointerException if the task is null
     */
    public <T> Future<T> submit(Callable<T> task) {
        return executorService.submit(task);
    }
}
