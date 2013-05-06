package iswear.test;

import iswear.promises.DeliverablePromise;
import iswear.promises.LazyComposablePromise;
import iswear.api.Promise;
import iswear.exceptions.PromiseBrokenException;
import org.testng.annotations.Test;

import java.util.List;

public class ComposablePromiseTest {

    @Test(enabled = true)
    public void testIsRealizedfulfilledBroken() throws Exception{
        DeliverablePromise promise1 = new DeliverablePromise();
        DeliverablePromise promise2 = new DeliverablePromise();
        DeliverablePromise promise3 = new DeliverablePromise();
        boolean illegalStateExceptionThrown = false;
        Promise composablePromise = new LazyComposablePromise(promise1,promise2, promise3);

        promise1.fulfillPromise(1);
        promise2.fulfillPromise(2);
        promise3.fulfillPromise(3);

        List<Integer> result= (List<Integer>)composablePromise.get();
        assert composablePromise.isRealized();
        assert composablePromise.isfulfilled();
        assert !composablePromise.isBroken();

        assert result.get(0) == 1;

        DeliverablePromise promise4 = new DeliverablePromise();
        DeliverablePromise promise5 = new DeliverablePromise();
        DeliverablePromise promise6 = new DeliverablePromise();
        illegalStateExceptionThrown = false;
        Promise composablePromise1 = new LazyComposablePromise(promise4,promise5, promise6);

        promise4.fulfillPromise(1);
        promise5.fulfillPromise(2);
        promise6.breakPromise(new PromiseBrokenException("just felt like it"));

        composablePromise1.await();
        assert composablePromise1.isRealized();
        assert !composablePromise1.isfulfilled();
        assert composablePromise1.isBroken();
    }

    @Test(enabled = true)
    public void testConstituentPromiseBroken() throws Exception{
        DeliverablePromise promise1 = new DeliverablePromise();
        DeliverablePromise promise2 = new DeliverablePromise();
        DeliverablePromise promise3 = new DeliverablePromise();
        boolean promiseBroken = false;
        Promise composablePromise = new LazyComposablePromise(promise1,promise2, promise3);

        promise1.breakPromise(new PromiseBrokenException("just felt like it"));

        composablePromise.await();

        try {
            composablePromise.get();
        } catch(PromiseBrokenException exception){
            promiseBroken = true;
        }
        assert promiseBroken;
    }

}
