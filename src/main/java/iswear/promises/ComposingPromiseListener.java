package iswear.promises;

import iswear.api.PromiseListener;
import iswear.exceptions.PromiseBrokenException;
import iswear.exceptions.PromiseRealizedException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



/*
    This listener facilitates composing of any promise with a one or more deliverable promises.
    one or more deliverable promises can subscribe to a promise and all of them will be realized
    appropriately (either full-filled or broken) when the base promise is realized.
 */

public class ComposingPromiseListener<T> implements PromiseListener<T> {

    public final List<DeliverablePromise<T>> promises = new ArrayList<DeliverablePromise<T>>();

    /*
        The constructor takes one or more promises that should be realized if the promise that
        the listener instance is listening in on is realized.
     */
    public ComposingPromiseListener(DeliverablePromise<T>... promises)
    {
        Collections.addAll(this.promises, promises);
    }

    /*
        If the promise that the listener is listening in on is full-filled then all the promises
        that are subscribed to this listener are attempted to be full-filled by this function.
     */
    @Override
    public void whenFullfilled(T value) {
        for(DeliverablePromise<T> promise: this.promises){
            try{
                promise.fullFillPromise(value);
            }catch (PromiseRealizedException exception){
            }
        }
    }

    /*
        If the promise that the listener is listening in on is broken then all the promises
        that are subscribed to this listener are attempted to be broken by this function.
     */
    @Override
    public void whenBroken(PromiseBrokenException promiseBrokenException) {
        for(DeliverablePromise<T> promise: this.promises){
            try{
                promise.breakPromise(promiseBrokenException);
            }catch (PromiseRealizedException exception){

            }
        }
    }

}
