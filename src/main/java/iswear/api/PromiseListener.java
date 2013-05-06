package iswear.api;


import iswear.exceptions.PromiseBrokenException;


/*
 * A PromiseListener listens in on a promise for it's realization.
 * once a promise has been realized the promise calls all the
 * listeners associated with it using the appropriate handler.
 *
 * How the promise invokes it's listeners is dependent on whether
 * the promise has been full-filled or broken.
 *
 * If the promise has been full-filled then the whenfulfilled method
 * of the listener is invoked with the value of the full-filled promise
 *
 * If the promise has been broken then the whenBroken method of the
 * listener is invoked.
 */
public interface PromiseListener<V>{
    /*
    * this is called when the promise this listener is listening in
    * on has been full-filled.
    *
    * @args it takes the value of the full-filled promise as an argument
    */
    void whenfulfilled(V value);

    /*
    * this is called when the promise this listener is listening in
    * on has been broken.
    *
    */
    void whenBroken(PromiseBrokenException promiseBrokenException);
}
