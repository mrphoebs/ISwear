package iswear.test;

import iswear.promises.DeliverablePromise;
import iswear.exceptions.PromiseBrokenException;
import iswear.exceptions.PromiseRealizedException;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;


public class DeliverablePromiseTest {

    @Test(enabled = true)
    public void testIsRealized(){
        DeliverablePromise promiseToKeep = new DeliverablePromise();
        DeliverablePromise promiseToBreak = new DeliverablePromise();
        boolean exceptionThrown = false;

        assert !promiseToKeep.isRealized();
        assert !promiseToBreak.isRealized();

        try {
            promiseToKeep.fullFillPromise(100l);
            promiseToBreak.breakPromise(new PromiseBrokenException("just felt like it"));
        }catch (PromiseRealizedException exception){
            exceptionThrown = true;
        }

        assert !exceptionThrown;

        assert promiseToKeep.isRealized();
        assert promiseToBreak.isRealized();
    }

    @Test(enabled = true)
    public void testIsFullFilledAndIsBroken() throws Exception{
        DeliverablePromise promiseToKeep = new DeliverablePromise();
        DeliverablePromise promiseToBreak = new DeliverablePromise();
        boolean IllegalStateExceptionThrown = false;

        try {
            promiseToKeep.isFullfilled();
        } catch (IllegalStateException exception){
            IllegalStateExceptionThrown = true;
        }

        assert IllegalStateExceptionThrown;

        IllegalStateExceptionThrown = false;

        try {
            promiseToBreak.isBroken();
        } catch (IllegalStateException exception){
            IllegalStateExceptionThrown = true;
        }

        assert IllegalStateExceptionThrown;

        promiseToBreak.breakPromise(new PromiseBrokenException("just felt like it"));
        promiseToKeep.fullFillPromise(100l);

        assert promiseToKeep.isFullfilled();
        assert !promiseToBreak.isFullfilled();

        assert !promiseToKeep.isBroken();
        assert promiseToBreak.isBroken();
    }

    @Test(enabled = true)
    public void testAddListener() throws Exception{
        DeliverablePromise promiseToKeep = new DeliverablePromise();
        DeliverablePromise promiseToBreak = new DeliverablePromise();

        DeliverablePromise higherLevelPromiseToKeep = new DeliverablePromise(promiseToKeep);
        DeliverablePromise higherLevelPromiseToBreak = new DeliverablePromise(promiseToBreak);

        boolean promiseBrokenExceptionThrown = false;

        promiseToKeep.fullFillPromise(100l);
        assert (Long)higherLevelPromiseToKeep.get(100000l, TimeUnit.SECONDS) == 100l;


        promiseToBreak.breakPromise(new PromiseBrokenException("just felt like it"));

        try {
            higherLevelPromiseToBreak.get(100000l,TimeUnit.SECONDS);
        }catch (PromiseBrokenException exception){
            promiseBrokenExceptionThrown = true;
        }

        assert promiseBrokenExceptionThrown;
    }
}
