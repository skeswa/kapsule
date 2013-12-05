package com.epsilonlabs.kapsule;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;

public class GetCollection<T> {
    private static final int SYNCHRONOUS_WAIT_INTERVAL_MS = 25;

    public static abstract class Callback<T> {
        public abstract void success(Collection<T> result);

        public void failure(Throwable e) {
            // Do nothing in default implementation
            e.printStackTrace();
        }
    }

    public static class SynchronousExecutionException extends RuntimeException {
        public SynchronousExecutionException() {
        }

        public SynchronousExecutionException(Throwable cause) {
            super("Could not execute get(..) synchronously", cause);
        }
    }

    private String kapsuleId;
    private Callback<T> callback;
    private Ready ready;
    private CountDownLatch latch = new CountDownLatch(1);
    private Collection<T> result;
    private Throwable problem;

    void doSuccess(Collection<T> result) {
        this.result = result;
        this.latch.countDown();

        if (this.callback != null)
            this.callback.success(result);
    }

    void doFailure(Throwable e) {
        this.problem = e;
        this.latch.countDown();

        if (this.callback != null)
            this.callback.failure(e);
    }

    void ready(Ready ready) {
        this.ready = ready;
    }

    String kapsule() {
        return this.kapsuleId;
    }

    public GetCollection<T> from(String kapsule) {
        // Parameter checking
        if (kapsule == null)
            throw new IllegalArgumentException("The kapsule parameter was null.");

        this.kapsuleId = kapsule;
        return this;
    }

    public void then(Callback<T> callback) {
        // Parameter checking
        if (callback == null)
            throw new IllegalArgumentException("The callback parameter was null.");

        this.callback = callback;
        // Trigger ready
        if (this.ready != null)
            ready.ready();
    }

    public Collection<T> synchronously() {
        // Trigger ready
        if (this.ready != null)
            ready.ready();
        try {
            this.latch.await();
            if (this.problem != null)
                throw new SynchronousExecutionException(this.problem);
            else
                return this.result;
        } catch (InterruptedException e) {
            throw new SynchronousExecutionException(e);
        }
    }
}
