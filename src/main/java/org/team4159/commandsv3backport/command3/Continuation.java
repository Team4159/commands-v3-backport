// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.team4159.commandsv3backport.command3;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

@SuppressWarnings("PMD.AvoidCatchingGenericException")
final class Continuation {

    private static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool();
    private static Continuation mountedContinuation = null;

    private final Semaphore resumeQueue = new Semaphore(0, false);
    private final Semaphore yieldQueue = new Semaphore(0, false);

    private boolean done = false;

    Continuation(Runnable target) {
        start(target);
    }

    public boolean yield() {
        try {
            yieldQueue.release();
            resumeQueue.acquire();
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public void run() {
        try {
            resumeQueue.release();
            yieldQueue.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
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
            try {
                resumeQueue.acquire();
                target.run();
                yieldQueue.release();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                done = true;
            }
        });
    }
}
