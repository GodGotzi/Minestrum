package net.gotzi.minestrum.api;

import java.io.IOException;

public interface ArgumentStartable<T> {
    
    void start(T t) throws IOException;
    
}
