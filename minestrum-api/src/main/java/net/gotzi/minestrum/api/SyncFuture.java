package net.gotzi.minestrum.api;

public abstract class SyncFuture<T> {

    public abstract void done(T t);

    public synchronized void run(T t) {
        done(t);
    }

}
