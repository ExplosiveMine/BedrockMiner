package io.github.explosivemine.BedrockMiner.util;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class Executor {
    public static void async(Plugin plugin, Consumer<BukkitRunnable> consumer, long...args) {
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                consumer.accept(this);
            }
        };

        if (args.length == 0)
            runnable.runTaskAsynchronously(plugin);
        else if (args.length == 1)
            runnable.runTaskLaterAsynchronously(plugin, args[0]);
        else if (args.length == 2)
            runnable.runTaskTimerAsynchronously(plugin, args[0], args[1]);
    }

    public static int sync(Plugin plugin, Consumer<BukkitRunnable> consumer, long...args) {
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                consumer.accept(this);
            }
        };
        return sync(plugin, runnable, args);
    }

    public static int sync(Plugin plugin, BukkitRunnable runnable, long...args) {
        if (args.length == 0)
            runnable.runTaskLater(plugin, 0L);
        else if (args.length == 1)
            runnable.runTaskLater(plugin, args[0]);
        else if (args.length == 2)
            runnable.runTaskTimer(plugin, args[0], args[1]);

        return runnable.getTaskId();
    }

    public static ComplexTask<Void> create() {
        return new ComplexTask<>();
    }

    public static final class ComplexTask<T> {
        private final CompletableFuture<T> completableFuture = new CompletableFuture<>();

        private ComplexTask() { }

        public void sync(Plugin plugin, Consumer<T> consumer, long...args) {
            Executor.async(plugin, unused -> completableFuture.whenComplete((t, throwable) -> Executor.sync(plugin, unused1 -> consumer.accept(t), args)));
        }

        public <R> ComplexTask<R> async(Plugin plugin, Supplier<R> supplier, long...args) {
            ComplexTask<R> task = new ComplexTask<>();
            Executor.async(plugin, unused -> task.completableFuture.complete(supplier.get()), args);
            return task;
        }
    }

}