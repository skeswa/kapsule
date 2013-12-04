package com.epsilonlabs.kapsule;

/**
 * Created by Sandile on 12/3/13.
 */
public class UndefinedContextException extends RuntimeException {
    private static final long serialVersionUID = -5925586523031410833L;

    public UndefinedContextException() {
        super("A context hasn't been provided to kapsule yet. To provide a context, use the Kapsule.context(..) method before you make any kapsule other calls.");
    }
}
