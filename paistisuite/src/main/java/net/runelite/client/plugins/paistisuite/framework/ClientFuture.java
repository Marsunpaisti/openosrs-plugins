package net.runelite.client.plugins.paistisuite.framework;

import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.*;

public class ClientFuture<T> implements Future<T> {
    private Exception exception = null;
    private boolean done = false;
    private boolean cancelled = false;
    private T result = null;

    /**
     * Submit a result to the future, notifying all threads waiting for it
     * @param t Result to submit
     */
    public void submitResult(T t){
        synchronized(this) {
            this.result = t;
            this.done = true;
            this.notifyAll();
        }
    }

    /**
     * Submit an exception to the future, causing all threads waiting on it to throw
     * @param exception Exception to submit
     */
    public void submitException(Exception exception){
        synchronized(this){
            this.exception = exception;
            this.notifyAll();
        }
    }

    /**
     * Cancel the task, causing all threads waiting on it to throw CancellationException if the result was not ready already
     * @param mayInterruptIfRunning not used
     */
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        synchronized (this) {
            if ( this.done ) return false;
            this.cancelled = true;
            this.notifyAll();
        }
        return true;
    }

    @Override
    public boolean isCancelled() {
        synchronized (this) {
            return this.cancelled;
        }
    }

    /**
     * Returns true if the future is completed with a successful result
     * @return True if the future is completed with a successful result
     */
    @Override
    public boolean isDone() {
        synchronized (this){
            return this.done;
        }
    }

    /**
     * Blocks execution until result is calculated
     * @return Calculated result
     */
    @Override
    public T get() throws InterruptedException, ExecutionException, CancellationException {
        synchronized (this){
            while (!this.cancelled && !this.done && this.exception == null) {
                this.wait();
            }

            if (this.exception != null) throw new ExecutionException(this.exception);
            if (this.cancelled) throw new CancellationException("Scheduled task was cancelled!");
            if (this.done) return this.result;
            return null;
        }
    }

    @Override
    public T get(long timeout, @NotNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        throw new NotImplementedException();
    }
}
