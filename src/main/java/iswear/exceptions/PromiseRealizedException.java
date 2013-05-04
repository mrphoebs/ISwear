package iswear.exceptions;


/*
    This exception will be thrown whenever any thread tries to realize
    a promise that has been already realized.
*/
public class PromiseRealizedException extends Exception{
    public PromiseRealizedException(){super();}
    public PromiseRealizedException(String message){super(message);}
    public PromiseRealizedException(String message, Throwable cause){super(message,cause);}
    public PromiseRealizedException(Throwable cause){super(cause);}
}
