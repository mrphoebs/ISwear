package iswear.test;

import iswear.exceptions.PromiseBrokenException;
import iswear.promises.DeliverablePromise;
import iswear.promises.FutureWrapperPromise;
import org.testng.annotations.Test;

import java.util.concurrent.*;


public class FutureWrapperPromiseTest {
    private ExecutorService tpe = Executors.newSingleThreadExecutor();

    @Test
    public void isRealizedTest() throws Exception{
        Future future = tpe.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "dude";  //To change body of implemented methods use File | Settings | File Templates.
            }
        });

        FutureWrapperPromise promise = new FutureWrapperPromise(future);
        promise.await();

        assert promise.isRealized();
        assert promise.isfulfilled();
        assert promise.get() == "dude";
    }

    @Test
    public void listenersTest() throws Exception{
        Future future = tpe.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "dude";  //To change body of implemented methods use File | Settings | File Templates.
            }
        });

        FutureWrapperPromise promise = new FutureWrapperPromise(future);
        DeliverablePromise deliverablePromise = new DeliverablePromise(promise);

        promise.await();
        assert deliverablePromise.get() == "dude";
    }

    @Test
    public void futureCancellationTest() throws Exception{
        Future future = tpe.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                Thread.sleep(100000l);
                return "dude";  //To change body of implemented methods use File | Settings | File Templates.
            }
        });

        FutureWrapperPromise promise = new FutureWrapperPromise(future);
        boolean promiseBrokenExceptionThrown = false;

        future.cancel(true);

        try {
            promise.get();
        }catch (PromiseBrokenException exception){
            promiseBrokenExceptionThrown = true;
        }

        assert promiseBrokenExceptionThrown;
    }
}
