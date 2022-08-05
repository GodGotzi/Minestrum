package net.gotzi.minestrum.util;

/**
 * Exception without a stack trace component.
 */
public class QuietException extends RuntimeException
{

    public QuietException(String message)
    {
        super( message );
    }

    @Override
    public Throwable initCause(Throwable cause)
    {
        return this;
    }

    @Override
    public Throwable fillInStackTrace()
    {
        return this;
    }
}
