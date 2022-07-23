package at.gotzi.minestrum.api;

import java.util.logging.Handler;

public abstract class Bot<T extends Bot<?>> {

    private Handler errorhandler;

    public void setErrorhandler(Handler errorhandler) {
        this.errorhandler = errorhandler;
    }

    public Handler getErrorhandler() {
        return errorhandler;
    }

    public abstract T start();

}
