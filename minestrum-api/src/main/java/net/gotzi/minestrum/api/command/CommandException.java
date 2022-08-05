package net.gotzi.minestrum.api.command;

public class CommandException extends RuntimeException {

    static final long serialVersionUID = -7034897190745766940L;

    public CommandException() {
        super();
    }

    public CommandException(String message) {
        super(message);
    }

}
