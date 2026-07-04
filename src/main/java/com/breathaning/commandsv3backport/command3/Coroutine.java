package com.breathaning.commandsv3backport.command3;

import static edu.wpi.first.units.Units.Seconds;
import static edu.wpi.first.util.ErrorMessages.requireNonNullParam;

import edu.wpi.first.units.measure.Time;
import edu.wpi.first.wpilibj.Timer;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * A coroutine object is injected into command's {@link Command#run(Coroutine)} method to allow
 * commands to yield and compositions to run other commands. Commands are considered <i>bound</i> to
 * a coroutine while they're scheduled; attempting to use a coroutine outside the command bound to
 * it will result in an {@code IllegalStateException} being thrown.
 */
public final class Coroutine {

    private static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool();

    @FunctionalInterface
    private interface CoroutineCallback {
        void run(Coroutine coroutine) throws InterruptedException;
    }

    private final Semaphore resumeQueue = new Semaphore(0, false);
    private final Semaphore yieldQueue = new Semaphore(0, false);
    private final Scheduler scheduler;
    private final CoroutineCallback callback;

    private boolean done = false;

    /**
     * Creates a new coroutine. Package-private; only the scheduler should be creating these.
     *
     * @param scheduler The scheduler running the coroutine
     * @param scope The continuation scope the coroutine's backing continuation runs in
     * @param callback The callback for the continuation to execute when mounted. Often a command
     *     function's body.
     */
    Coroutine(Scheduler scheduler, Consumer<Coroutine> callback) {
        this.scheduler = scheduler;
        this.callback = coroutine -> {
            try {
                callback.accept(coroutine);
            } catch (Exception e) {
                throw new InterruptedException();
            }
        };
        start();
    }

    /**
     * Yields control back to the scheduler to allow other commands to execute. This can be thought of
     * as "pausing" the currently executing command.
     *
     * @return true
     * @throws IllegalStateException if called anywhere other than the coroutine's running command
     */
    public void yield() {
        yieldQueue.release();
        try {
            resumeQueue.acquire();
        } catch (InterruptedException e) {}
    }

    /**
     * Parks the current command. No code in a command declared after calling {@code park()} will be
     * executed. A parked command will never complete naturally and must be interrupted or canceled.
     *
     * @throws IllegalStateException if called anywhere other than the coroutine's running command
     */
    public void park() {
        try {
            while (true) {
                yieldQueue.release();
                resumeQueue.acquire();
            }
        } catch (InterruptedException e) {}
    }

    /**
     * Schedules a child command and then immediately returns. The child command will run until its
     * natural completion, the parent command exits, or the parent command cancels it.
     *
     * <p>This is a nonblocking operation. To fork and then wait for the child command to complete,
     * use {@link #await(Command)}.
     *
     * <p>The parent command will continue executing while the child command runs, and can resync with
     * the child command using {@link #await(Command)}.
     *
     * <pre>{@code
     * Command example() {
     *   return Command.noRequirements(coroutine -> {
     *     Command child = ...;
     *     coroutine.fork(child);
     *     // ... do more things
     *     // then sync back up with the child command
     *     coroutine.await(child);
     *   }).named("Example");
     * }
     * }</pre>
     *
     * <p>Note: forking a command that conflicts with a higher-priority command will fail. The forked
     * command will not be scheduled, and the existing command will continue to run.
     *
     * @param commands The commands to fork.
     * @throws IllegalStateException if called anywhere other than the coroutine's running command
     * @see #await(Command)
     */
    public void fork(Command... commands) {
        requireNonNullParam(commands, "commands", "Coroutine.fork");
        for (int i = 0; i < commands.length; i++) {
            requireNonNullParam(commands[i], "commands[" + i + "]", "Coroutine.fork");
        }

        ConflictDetector.throwIfConflicts(List.of(commands));

        for (var command : commands) {
            scheduler.schedule(command);
        }
    }

    /**
     * Forks off some commands. Each command will run until its natural completion, the parent command
     * exits, or the parent command cancels it. The parent command will continue executing while the
     * forked commands run, and can resync with the forked commands using {@link
     * #awaitAll(Collection)}.
     *
     * <pre>{@code
     * Command example() {
     *   return Command.noRequirements(coroutine -> {
     *     Collection<Command> innerCommands = ...;
     *     coroutine.fork(innerCommands);
     *     // ... do more things
     *     // then sync back up with the inner commands
     *     coroutine.awaitAll(innerCommands);
     *   }).named("Example");
     * }
     * }</pre>
     *
     * <p>Note: forking a command that conflicts with a higher-priority command will fail. The forked
     * command will not be scheduled, and the existing command will continue to run.
     *
     * @param commands The commands to fork.
     * @throws IllegalStateException if called anywhere other than the coroutine's running command
     */
    public void fork(Collection<? extends Command> commands) {
        fork(commands.toArray(Command[]::new));
    }

    /**
     * Awaits completion of a command. If the command is not currently scheduled or running, it will
     * be scheduled automatically. This is a blocking operation and will not return until the command
     * completes or has been interrupted by another command scheduled by the same parent.
     *
     * @param command the command to await
     * @throws IllegalStateException if called anywhere other than the coroutine's running command
     * @see #fork(Command...)
     */
    public void await(Command command) {
        requireNonNullParam(command, "command", "Coroutine.await");

        scheduler.schedule(command);

        while (scheduler.isScheduledOrRunning(command)) {
            this.yield();
        }
    }

    /**
     * Awaits completion of all given commands. If any command is not current scheduled or running, it
     * will be scheduled.
     *
     * @param commands the commands to await
     * @throws IllegalArgumentException if any of the commands conflict with each other
     * @throws IllegalStateException if called anywhere other than the coroutine's running command
     */
    public void awaitAll(Collection<? extends Command> commands) {
        requireNonNullParam(commands, "commands", "Coroutine.awaitAll");
        int i = 0;
        for (Command command : commands) {
            requireNonNullParam(command, "commands[" + i + "]", "Coroutine.awaitAll");
            i++;
        }

        ConflictDetector.throwIfConflicts(commands);

        for (var command : commands) {
            scheduler.schedule(command);
        }

        while (commands.stream().anyMatch(scheduler::isScheduledOrRunning)) {
            this.yield();
        }
    }

    /**
     * Awaits completion of all given commands. If any command is not current scheduled or running, it
     * will be scheduled.
     *
     * @param commands the commands to await
     * @throws IllegalArgumentException if any of the commands conflict with each other
     * @throws IllegalStateException if called anywhere other than the coroutine's running command
     */
    public void awaitAll(Command... commands) {
        awaitAll(Arrays.asList(commands));
    }

    /**
     * Awaits completion of any given commands. Any command that's not already scheduled or running
     * will be scheduled. After any of the given commands completes, the rest will be canceled.
     *
     * @param commands the commands to await
     * @throws IllegalArgumentException if any of the commands conflict with each other
     * @throws IllegalStateException if called anywhere other than the coroutine's running command
     */
    public void awaitAny(Collection<? extends Command> commands) {
        requireNonNullParam(commands, "commands", "Coroutine.awaitAny");
        int i = 0;
        for (Command command : commands) {
            requireNonNullParam(command, "commands[" + i + "]", "Coroutine.awaitAny");
            i++;
        }

        ConflictDetector.throwIfConflicts(commands);

        // Schedule anything that's not already queued or running
        for (var command : commands) {
            scheduler.schedule(command);
        }

        while (commands.stream().allMatch(scheduler::isScheduledOrRunning)) {
            this.yield();
        }

        commands.forEach(scheduler::cancel);
    }

    /**
     * Awaits completion of any given commands. Any command that's not already scheduled or running
     * will be scheduled. After any of the given commands completes, the rest will be canceled.
     *
     * @param commands the commands to await
     * @throws IllegalArgumentException if any of the commands conflict with each other
     * @throws IllegalStateException if called anywhere other than the coroutine's running command
     */
    public void awaitAny(Command... commands) {
        awaitAny(Arrays.asList(commands));
    }

    /**
     * Waits for some duration of time to elapse. Returns immediately if the given duration is zero or
     * negative. Call this within a command or command composition to introduce a simple delay.
     *
     * <p>For example, a basic autonomous routine that drives straight for 5 seconds:
     *
     * <pre>{@code
     * Command timedDrive() {
     *   return drivebase.run(coroutine -> {
     *     drivebase.tankDrive(1, 1);
     *     coroutine.wait(Seconds.of(5));
     *     drivebase.stop();
     *   }).named("Timed Drive");
     * }
     * }</pre>
     *
     * <p>Note that the resolution of the wait period is equal to the period at which {@link
     * Scheduler#run()} is called by the robot program. If using a 20 millisecond update period, the
     * wait will be rounded up to the nearest 20 millisecond interval; in this scenario, calling
     * {@code wait(Milliseconds.of(1))} and {@code wait(Milliseconds.of(19))} would have identical
     * effects.
     *
     * <p>Very small loop times near the loop period are sensitive to the order in which commands are
     * executed. If a command waits for 10 ms at the end of a scheduler cycle, and then all the
     * commands that ran before it complete or exit, and then the next cycle starts immediately, the
     * wait will be evaluated at the <i>start</i> of that next cycle, which occurred less than 10 ms
     * later. Therefore, the wait will see not enough time has passed and only exit after an
     * additional cycle elapses, adding an unexpected extra 20 ms to the wait time. This becomes less
     * of a problem with smaller loop periods, as the extra 1-loop delay becomes smaller.
     *
     * @param duration the duration of time to wait
     * @throws IllegalStateException if called anywhere other than the coroutine's running command
     */
    public void wait(Time duration) {
        requireNonNullParam(duration, "duration", "Coroutine.wait");

        var timer = new Timer();
        timer.start();
        while (!timer.hasElapsed(duration.in(Seconds))) {
            this.yield();
        }
    }

    /**
     * Yields until a condition is met.
     *
     * @param condition The condition to wait for
     * @throws IllegalStateException if called anywhere other than the coroutine's running command
     */
    public void waitUntil(BooleanSupplier condition) {
        requireNonNullParam(condition, "condition", "Coroutine.waitUntil");

        while (!condition.getAsBoolean()) {
            this.yield();
        }
    }

    /**
     * Advanced users only: this permits access to the backing command scheduler to run custom logic
     * not provided by the standard coroutine methods. Any commands manually scheduled through this
     * will be canceled when the parent command exits - it's not possible to "fork" a command that
     * lives longer than the command that scheduled it.
     *
     * @return the command scheduler backing this coroutine
     * @throws IllegalStateException if called anywhere other than the coroutine's running command
     */
    public Scheduler scheduler() {
        return scheduler;
    }

    private void start() {
        THREAD_POOL.submit(() -> {
            try {
                callback.run(this);
                yieldQueue.release();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                done = true;
            }
        });
    }

    void runToYieldPoint() {
        resumeQueue.release();
        try {
            yieldQueue.acquire();
        } catch (InterruptedException e) {}
    }

    boolean isDone() {
        return done;
    }
}
