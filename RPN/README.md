This project creates a reverse-Polish calculator with a web interface.
======================================================================

Reverse-polish notation (RPN) is a method of writing arithmetic expressions
without parentheses or operator precedence.
A number puts its value on an (infinite) stack.
A monadic operator replaces that value _x_ by _f(x)_ where _f_ represents the monadic function.
A dyadic operator replaces the top two values of the stack by _f(next,top)_ where _f_ represents
the dyadic function.

Details
=======

The components that are used by this project are:

* Scala
* Play
* Activator
* Akka
* and dependencies thereof

In order to run the calculator (essentially a web server), run:

  activator run
  
Running the calculator from your browser
========================================

Now connect your web browser to _localhost:9000_

Passing the query "/" will simply give you the state of the calculator's stack.

Passing a string following the "/" will result in passing that string to the calculator.

So, for example, GET /1%202%20+ will result in a value of 3 (where %20 is the encoding for space).

Supported operators are:

* '+' or plus [add the top two items on the stack and push the result]
* '-' (the dyadic form: use chs to change sign) [subtract the top two items on the stack and push the result]
* '*' or times [multiply the top two items on the stack and push the result]
* '/' or div [divide the top two items on the stack and push the result]
* chs [change sign of top item on stack]
* inv [replace the top of item on the stack with its reciprocal]
* swap [swap the top two items on the stack]
* del [pop the top item from the stack and discard it]
* clr [clear the stack]

And, furthermore, there are the following two memory instructions

* sto:key [store the value at the top of the stack into memory location "key"]
* rcl:key [recall the value in memory location "key", pushing it on to the stack]

There are additionally, two (currently) constants with effective names of:

* _pi [the value of pi]
* _e [the value of e]

Other space-delimited entities will, if possible, be recognized as numbers and non-numbers will
be treated as operators, constants or memory instructions.

In theory, there are two types of calculator available. One which works with _Double_ and one which
works with _Rational_ (from the _Numerics_ project). In practice, there is a problem with
the _Double_ calculator and so only the _Rational_ calculator is available. This should
not be a hardship as there is typically better accuracy.

Future enhancements
===================

Future versions will hopefully allow different types of calculator to be selected, perhaps using Spire numbers.
