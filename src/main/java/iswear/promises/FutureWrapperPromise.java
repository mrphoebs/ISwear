package iswear.promises;

import iswear.api.Promise;
import iswear.api.PromiseListener;
import iswear.exceptions.PromiseBrokenException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;



/*
    A FutureWrapperPromise wraps a java.util.concurrent.future
 */
public class FutureWrapperPromise<T> implements Promise<T> {

    private final Future<T> future;
    private final List<PromiseListener<T>> promiseListeners = new ArrayList<PromiseListener<T>>();
    private PromiseBrokenException promiseBrokenException;

    public FutureWrapperPromise(Future<T> future){
        this.future = future;
    }

    @Override
    public boolean isRealized() {
        return future.isDone();
    }

    @Override
    public boolean isFullfilled() throws IllegalStateException {
        return !future.isCancelled();
    }

    @Override
    public boolean isBroken() throws IllegalStateException {
        return future.isCancelled();
    }

    @Override
    public void await() throws InterruptedException {
        try{
            future.get();
            triggerListeners();
        } catch (ExecutionException exception){
            promiseBrokenException = new PromiseBrokenException(exception);
            throw new InterruptedException(exception.getMessage());
        } catch (InterruptedException exception){
            throw exception;
        } catch (CancellationException exception){
            promiseBrokenException = new PromiseBrokenException(exception);
        }

    }

    @Override
    public void await(long timeout, TimeUnit timeUnit) throws InterruptedException {
        try {
            future.get(timeout,timeUnit);
            triggerListeners();
        }catch (ExecutionException exception){
            promiseBrokenException = new PromiseBrokenException(exception);
            throw new InterruptedException(exception.getMessage());
        }catch (InterruptedException exception){
            throw exception;
        }catch (CancellationException exception){
            promiseBrokenException = new PromiseBrokenException(exception);
        }
        catch (TimeoutException exception){
            //do nothing
        }
    }

    @Override
    public T get() throws PromiseBrokenException, InterruptedException {
        try {
            T value = future.get();
            triggerListeners();
            return value;
        } catch (ExecutionException exception){
            promiseBrokenException = new PromiseBrokenException(exception);
            throw new InterruptedException(exception.getMessage());
        }catch (CancellationException exception){
            promiseBrokenException = new PromiseBrokenException(exception);
            throw new PromiseBrokenException(promiseBrokenException);
        }
    }

    @Override
    public T get(long timeout, TimeUnit timeUnit) throws PromiseBrokenException, TimeoutException, InterruptedException {
        try {
            T value = future.get(timeout, timeUnit);
            triggerListeners();
            return value;
        } catch (ExecutionException exception){
            promiseBrokenException = new PromiseBrokenException(exception);
            throw new InterruptedException(exception.getMessage());
        }catch (CancellationException exception){
            promiseBrokenException = new PromiseBrokenException(exception);
            throw new PromiseBrokenException(promiseBrokenException);
        }
    }

    @Override
    public synchronized void addListener(PromiseListener promiseListener) {
        if(isRealized())
            triggerListener(promiseListener);
        else
            promiseListeners.add(promiseListener);
    }

    //synchronized code block to avoid race conditions when adding listener
    private synchronized void triggerListeners(){
        for (PromiseListener promiseListener: promiseListeners){
            triggerListener(promiseListener);
        }
    }

    private void triggerListener(PromiseListener promiseListener){
        if(isFullfilled())
            try{promiseListener.whenFullfilled(future.get());}catch (Exception e){}
        else
            promiseListener.whenBroken(promiseBrokenException);
    }
}
