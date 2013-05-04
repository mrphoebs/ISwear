ISwear
======
A simple promise abstraction for java

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




