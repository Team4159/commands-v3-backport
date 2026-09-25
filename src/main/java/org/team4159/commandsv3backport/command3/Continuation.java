// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.team4159.commandsv3backport.command3;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@SuppressWarnings("PMD.AvoidCatchingGenericException")
final class Continuation {

    private static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool();

    private static Continuation mountedContinuation = null;

    private final ReentrantLock contextLock = new ReentrantLock(false);
    private final Condition startedCondition = contextLock.newCondition();
    private final Condition runningCondition = contextLock.newCondition();
    private final Condition yieldCondition = contextLock.newCondition();

    private RuntimeException runtimeExceptionPropagator = null;

    private boolean started = false;
    private boolean running = false;
    private volatile boolean done = false;

    Continuation(ContinuationScope scope, Runnable target) {
        start(target);
    }

    public boolean yield() {
        contextLock.lock();
        try {
            running = false;
            yieldCondition.signal();
            while (!running) {
                runningCondition.await();
            }
        } catch (InterruptedException e) {
            throw createInterruptedRuntimeException(e);
        } finally {
            contextLock.unlock();
        }
        return true;
    }

    public void run() {
        contextLock.lock();
        try {
            handleRuntimeException();
            running = true;
            runningCondition.signal();
            while (running) {
                yieldCondition.await();
            }
            handleRuntimeException();
        } catch (InterruptedException e) {
            throw createInterruptedRuntimeException(e);
        } finally {
            contextLock.unlock();
        }
    }

    public boolean isDone() {
        return done;
    }

    public static Continuation getMountedContinuation() {
        return mountedContinuation;
    }

    public static void mountContinuation(Continuation continuation) {
        mountedContinuation = continuation;
    }

    boolean isMounted() {
        return this == getMountedContinuation();
    }

    private void start(Runnable target) {
        THREAD_POOL.submit(() -> {
            contextLock.lock();
            try {
                started = true;
                startedCondition.signal();
                while (!running) {
                    runningCondition.await();
                }
            } catch (InterruptedException e) {
                runtimeExceptionPropagator = createInterruptedRuntimeException(e);
                return;
            } finally {
                contextLock.unlock();
            }

            try {
                target.run();
            } catch (RuntimeException e) {
                runtimeExceptionPropagator = e;
            } finally {
                contextLock.lock();
                try {
                    running = false;
                    done = true;
                    yieldCondition.signal();
                } finally {
                    contextLock.unlock();
                }
            }
        });

        contextLock.lock();
        try {
            while (!started) {
                startedCondition.await();
            }
        } catch (InterruptedException e) {
            throw createInterruptedRuntimeException(e);
        } finally {
            contextLock.unlock();
        }
    }

    private RuntimeException createInterruptedRuntimeException(InterruptedException cause) {
        return new RuntimeException("Continuation was interrupted unexpectedly!", cause);
    }

    private void handleRuntimeException() {
        RuntimeException runtimeException = runtimeExceptionPropagator;
        runtimeExceptionPropagator = null;
        if (runtimeException != null) {
            throw runtimeException;
        }
    }
}
