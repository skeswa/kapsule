package com.epsilonlabs.kapsule;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Get<T> {
    private static final int SYNCHRONOUS_WAIT_INTERVAL_MS = 25;

    public static abstract class Callback<T> {
        public abstract void success(T result);

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
    private GetCollection<T> callbackOverride;
    private Callback<T> callback;
    private Ready ready;
    private CountDownLatch latch = new CountDownLatch(1);
    private T result;
    private Throwable problem;

    boolean overridden() {
        return this.callbackOverride != null;
    }

    void ready(Ready ready) {
        this.ready = ready;
    }

    String kapsule() {
        if (this.kapsuleId != null) return this.kapsuleId;
        else {
            if (this.callbackOverride != null) return this.callbackOverride.kapsule();
            else return null;
        }
    }

    void doSuccess(T result) {
        if (overridden())
            throw new UnsupportedOperationException("This callback has been overridden by a plural callback.");

        this.latch.countDown();
        this.result = result;
        if (this.callback != null)
            this.callback.success(result);
    }

    void doOverriddenSuccess(List<T> result) {
        if (overridden())
            this.callbackOverride.doSuccess(result);
        else
            throw new UnsupportedOperationException("This callback has NOT been overridden by a plural callback.");
    }

    void doFailure(Throwable e) {
        this.problem = e;
        this.latch.countDown();

        if (this.callbackOverride != null)
            this.callbackOverride.doFailure(e);
        else if (this.callback != null)
            this.callback.failure(e);
    }

    public Get<T> from(String kapsule) {
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

    public GetCollection<T> collection() {
        if (this.callbackOverride == null) {
            this.callbackOverride = new GetCollection<T>();
            this.callbackOverride.ready(this.ready);
        }
        return this.callbackOverride;
    }

    public T synchronously() {
        if (overridden())
            throw new UnsupportedOperationException("This callback has been overridden by a plural callback.");

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
