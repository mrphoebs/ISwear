package iswear.exceptions;


/*
    This exception is thrown on all invocations of get for a promise
    that will be realized by breaking it.
 */
public class PromiseBrokenException extends Exception{
    public PromiseBrokenException(){super();}
    public PromiseBrokenException(String message){super(message);}
    public PromiseBrokenException(String message, Throwable cause){super(message,cause);}
    public PromiseBrokenException(Throwable cause){super(cause);}
}
