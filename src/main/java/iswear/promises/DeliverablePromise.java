package iswear.promises;

import iswear.api.Promise;
import iswear.api.PromiseListener;
import iswear.exceptions.PromiseBrokenException;
import iswear.exceptions.PromiseRealizedException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/*
    A deliverable promise is a promise that can be explicitly full-filled or broken
    Deliverable promise provides two methods to do the same.

    one is a fulfillPromise(T value)
    and another is breakPromise(promiseBrokenException)
 */

public class DeliverablePromise<T> implements Promise<T> {

    // we use a CountDownLatch at the core of a deliverable promise to coordinate promise delivery
    private final CountDownLatch countDownLatch;
    private T value = null;
    private final List<PromiseListener> promiseListeners = new ArrayList<PromiseListener>();
    private boolean broken = false;
    private PromiseBrokenException promiseBrokenException;

    /*
        A deliverable promise can be constructed using this constructor and can later
        be explicitly realized.
     */
    public DeliverablePromise(){
        this.countDownLatch = new CountDownLatch(1);
    }

    /*
        A deliverable promise can be constructed using this constructor and is realized
        when the base promise is realized.
     */
    public DeliverablePromise(Promise<T> promise){
        this.countDownLatch = new CountDownLatch(1);
        PromiseListener promiseListener = new ComposingPromiseListener<T>(this);
        promise.addListener(promiseListener);
    }

    @Override
    public boolean isRealized() {
        return countDownLatch.getCount()==0;
    }



    @Override
    public boolean isfulfilled() throws IllegalStateException {
        if(isRealized()){
            return !broken;
        }
        else {
            throw new IllegalStateException("Promise hasn't been realized yet");
        }
    }



    @Override
    public boolean isBroken() throws IllegalStateException {
        if(isRealized()){
            return broken;
        }
        else {
            throw new IllegalStateException("Promise hasn't been realized yet");
        }
    }



    @Override
    public void await() throws InterruptedException {
        countDownLatch.await();
    }



    @Override
    public void await(long timeout, TimeUnit timeUnit) throws InterruptedException {
        countDownLatch.await(timeout,timeUnit);
    }



    @Override
    public T get() throws PromiseBrokenException, InterruptedException {
        await();
        if(isfulfilled()){
            return value;
        }
        else {
            throw new PromiseBrokenException(promiseBrokenException);
        }
    }


    @Override
    public T get(long timeout, TimeUnit timeUnit) throws PromiseBrokenException, TimeoutException, InterruptedException {
        await(timeout,timeUnit);
        if(isRealized())

            if (isfulfilled())
                return value;
            else
                throw new PromiseBrokenException(promiseBrokenException);

        else
            throw new TimeoutException("Timed out waiting for the promise");

    }

    /*This method is synchronized to avoid the read-examine-write race conditions*/
    @Override
    public synchronized void addListener(PromiseListener promiseListener) {
        if(isRealized())
            triggerListener(promiseListener);
        else
            promiseListeners.add(promiseListener);
    }

    /*
    * sets the value of the promise and the promise is said to have been realized
    * once this is done. It subsequently calls the promise listeners
    *
    * @arg value to be set
    * @throws PromiseRealizedException if the promise was already set.
    */
    public void fulfillPromise(T value) throws PromiseRealizedException {
        if(!isRealized()){
            this.value = value;
            countDownLatch.countDown();
            triggerListeners();
        }
        else{
            throw new PromiseRealizedException("Promise already fulfilled");
        }
    }

    /*
    * breaks the promise and the promise is said to have been realized
    * once this is done. It subsequently calls the promise listeners
    *
    * @throws PromiseRealizedException if the promise was already set.
    */
    public void breakPromise(PromiseBrokenException promiseBrokenException) throws PromiseRealizedException{
        if(!isRealized()){
            countDownLatch.countDown();
            broken = true;
            this.promiseBrokenException = new PromiseBrokenException(promiseBrokenException);
            triggerListeners();
        }
        else{
            throw new PromiseRealizedException("Promise already fulfilled");
        }
    }

    /*This method is synchronized to avoid race conditions*/
    private synchronized void triggerListeners(){
        for (PromiseListener promiseListener: promiseListeners){
            triggerListener(promiseListener);
        }
    }

    private void triggerListener(PromiseListener promiseListener){
        if(isfulfilled())
            promiseListener.whenfulfilled(value);
        else
            promiseListener.whenBroken(promiseBrokenException);
    }
}
