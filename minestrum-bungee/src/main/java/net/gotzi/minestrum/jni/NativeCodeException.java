package net.gotzi.minestrum.jni;

public class NativeCodeException extends Exception
{

    public NativeCodeException(String message, int reason)
    {
        super( message + " : " + reason );
    }
}
