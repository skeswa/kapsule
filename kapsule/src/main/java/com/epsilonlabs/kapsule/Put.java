package com.epsilonlabs.kapsule;


import java.util.concurrent.CountDownLatch;

public class Put {
    public static abstract class Callback {
        public abstract void success();

        public void failure(Throwable e) {
            // Do nothing in default implementation
            e.printStackTrace();
        }
    }

    public static class SynchronousExecutionException extends RuntimeException {
        public SynchronousExecutionException() {
        }

        public SynchronousExecutionException(Throwable cause) {
            super("Could not execute put(..) synchronously", cause);
        }
    }

    private String kapsuleId;
    private Callback callback;
    private Ready ready;
    private CountDownLatch latch = new CountDownLatch(1);
    private Throwable problem;

    String kapsule() {
        return this.kapsuleId;
    }

    void ready(Ready ready) {
        this.ready = ready;
    }

    void doSuccess() {
        this.latch.countDown();

        if (this.callback != null)
            this.callback.success();
    }

    void doFailure(Throwable e) {
        this.problem = e;
        this.latch.countDown();

        if (this.callback != null)
            this.callback.failure(e);
    }

    public Put into(String kapsule) {
        // Parameter checking
        if (kapsule == null)
            throw new IllegalArgumentException("The kapsule parameter was null.");

        this.kapsuleId = kapsule;
        return this;
    }

    public void then(Callback callback) {
        // Parameter checking
        if (callback == null)
            throw new IllegalArgumentException("The callback parameter was null.");

        this.callback = callback;
        // Trigger ready
        if (this.ready != null)
            ready.ready();
    }

    public void synchronously() {
        // Trigger ready
        if (this.ready != null)
            ready.ready();
        try {
            this.latch.await();
            if (this.problem != null)
                throw new SynchronousExecutionException(this.problem);
        } catch (InterruptedException e) {
            throw new SynchronousExecutionException(e);
        }
    }
}
