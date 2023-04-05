/*
	Author: Elias (Gotzi) Gottsbacher
	Copyright (c) 2023 Elias Gottsbacher
*/

package net.gotzi.minestrum.api;

import java.util.logging.Handler;

public abstract class Bot {

    public Bot() {}

    private Handler errorhandler;

    public void setErrorhandler(Handler errorhandler) {
        this.errorhandler = errorhandler;
    }

    public Handler getErrorhandler() {
        return errorhandler;
    }

    public abstract Bot start();

    public abstract void stop();

}
