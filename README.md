ISwear
======
A simple promise abstraction for java.
Inspired by sid's [vachan](https://github.com/sids/vachan "vachan")

A Promise
---------
A promise is a synchronizing construct in concurrent programming. It "promises"
to eventually hold the value of an asynchronous computation or process.

A promise provides no guarantees on the timeliness of the computation. When a thread tries to get the value
of a promise, the promise blocks and puts the asking thread in a wait state until the
promise is realized. A promise can be realized by either full-filling it with a
value(the value of computation) or breaking it.

Promises like futures allow one write concurrent code that looks very sequential/intutive.

**ISwear** is a simple promise abstraction and a few example implementations for java.

The Promise Interface
---------------------
The promise interface is the heart of ISwear, following code demonstrates the use of the interface.

```java
Promise<String> resultPromise = service.getResultOfComputation();

//this will return false until the promise is either fullfilled or broken, it's non blocking
resultPromise.isRealized();

/*
* Checks if the promise has been full-filled. A promise is said to have been full-filled
* if the value of a promise is realized. It returns true if the promise has been realized
* and the promise has been full-filled, false if the promise has been realized and the
* promise has been broken. This should usually only be called once the promise has been
* realized. If it's called before the promise has been realized, it throws an exception.
*/
resultPromise.isFullfilled();

/*
* Checks to see if the promise has been broken. A promise is said to have been broken
* if the promise is realized but is in a broken state. It returns true if the promise
* is realized and a it's in a broken state, false if the promise is realized and it's
* in a full-filled state.
*/
resultPromise.isBroken();

//Puts the calling thread in a wait state until the promise has been realized.
resultPromise.await();

/*
* Puts the calling thread in a wait state until either the promise has been
* realized or "timeout" units of type "timeUnit" have passed, whichever
* happens sooner.
*/
resultPromise.await(100l, TimeUnit.MILLISECONDS);


/*
* Puts the calling thread in a wait state until the promise has been realized
* and either returns the value of the promise once it has been full-filled or
* throws a promise broken exception once the promise is broken.
*/
String result = resultPromise.get();

/*
* Puts the calling thread in a wait state until the promise has either been
* realized or the timeout period has expired. If the promise has been realized
* then it either returns the value of the promise once it has been full-filled or
* throws a promise broken exception once the promise is broken. If the call has
* timed out a timeout exception is thrown.
*/
String resultAlt = resultPromise.get(100l,TimeUnit.MILLISECONDS);

/*
* Adds a PromiseListener to the promise, whose whenFullfilled or whenBroken
* method is invoked when the promise is realized.
*/
PromiseListener promiseListener = new ComposingPromiseListener();
resultPromise.add(promiseListener);
```

The PromiseListener Interface
-----------------------------
A PromiseListener listens in on a promise for it's realization.
once a promise has been realized the promise calls all the
listeners associated with it using the appropriate handler.

How the promise invokes it's listeners is dependent on whether
the promise has been full-filled or broken.

If the promise has been full-filled then the whenFullfilled method
of the listener is invoked with the value of the full-filled promise
If the promise has been broken then the whenBroken method of the
listener is invoked.

```java
public interface PromiseListener<V>{
    /*
    * this is called when the promise this listener is listening in
    * on has been full-filled.
    *
    * @args it takes the value of the full-filled promise as an argument
    */
    void whenFullfilled(V value);

    /*
    * this is called when the promise this listener is listening in
    * on has been broken.
    *
    */
    void whenBroken(PromiseBrokenException promiseBrokenException);
}
```