MapReduce with Akka
===================

_MapReduce with Akka_ is a framework for implementing map-reduce using actors.
I tried to use only the intrinsic notion of map and reduce. Thus is is not exactly like Hadoop's map-reduce (although it is similar).

__Why__ would anyone want to do map-reduce using actors? It's a good question. For me, it arose initially because I needed an example of using actors for the class I was teaching on Scala and Big Data.
I also wanted to ensure that the students understood the essence of map-reduce rather than some derived version of it.
Of course, it turns out that it's a perfect application for actors and indeed demonstrates many of the proper techniques to be used when programming with (Akka) actors.

Introduction
------------

In order for a calculation to be performed in parallel, it is necessary that the complete calculation can be broken up into smaller parts which can each be implemented independently.
These parallel calculations are performed in the _reduce_ phase of map-reduce while the _map_ phase is responsible for breaking the work into these independent parts.
In order that the results from the _reduce_ phase can be collated and/or aggregated, it is usually convenient for each portion of the calculation to be identified by a unique key (we will call the type of these keys _K2_).
The data required for each portion is typically of many similar elements. We will call the type of these elements _V2_. Thus the natural intermediate data structure (for the _shuffle_ phase, see below) which results from
the _map_ stage and is used as input to the _reduce_ stage is:

    Map[K2, Seq[V2]]
    
Thus the first job of designing an application to use map-reduce is to figure out the types _K2_ and _V2_. If you are chaining map-reduce operations together, then the input to stage _N_+1
will be of the same form as the output of stage _N_. Thus, in general, the input to the map-reduce process is a map of key-value pairs. We call the type of the key _K1_ and the type of
the value _V1_. Thus the input to the map stage is:

    Map[K1,V1]
    
For the first stage, there is usually no appropriate key so instead we pass in a message of the following form:

	Seq[V1]
	
The reduction stage, as we have already seen, starts with information in the form of _Map[K2,Seq[V2]]_ and the work is divided up and sent to each of the reducers. Thus each reducer takes as input (via a message) the following tuple:

	(K2,Seq[V2])
	
 The result of each reduction is a tuple of the following form:
 
	(K2,V3)
	
where _V3_ is the aggregate of all of the _V2_ elements.

Of course, it's possible that there are insufficient reducers available for each of the keys. The way this project deals with that situation is simply to start sending messages to the available actors again.
In general, the so-called _shuffle_ phase which precedes the _reduce_ phase is able to pick and choose how to make the best match between the key value _k_ and a particular reducer. This might be based
on locality of data referenced by the values in the sequence. Or some other criterion with a view to load-balancing. However, this project does not currently make any such decisions so the _shuffle_ phase is really non-existent: messages
(one per key) are simply sent out to reducers in sequence.

Details
=======

Master
------

The _Master_ (or one its three siblings) is the only class which an application needs to be concerned with. The _Master_, itself an actor, creates a mapper and a number of reducers as appropriate on startup and destroys them at the end.
The input message and the constructor format are slightly different according to which form of the _Master_ (see below) you are employing.

Generally, there are five polymorphic types which describe the definition of _Master_: _K1, V1, K2, V2,_ and _V3_. Of these, _V2_ is not involved in messages going to or from the master--it is internal only.
And, again generally, the constructor for the _Master_ takes the following parameters:

* _config: Config_
* _f: (K1,V1)=>(K2,V2)_
* _g: (V3,V2)=>V3_
* _z: ()=>V3_

where

* _config_ is used for various configuration settings, such as the number of reducers to be created;
* _f_ is described in _Mapper_ below
* _g_ and _z_ are described in _Reducer_ below

There are actually four _Master_ types to accommodate different situations. The first map-reduce stage in a pipeline (as mentioned above) does not involve _K1_. Therefore, two of the _master_ types are of this "first" type.
Next, there is a difference between the pure reducers which require that these are treated separately (see section on _Reducer_ below). This creates another pairing of master forms: the "fold" variations.
Thus, we have four forms of _Master_ all told:

* _Master_
* _Master_First_
* _Master_Fold_
* _Master_First_Fold_

The "fold" variations require the _z_ parameter, whereas the other variations do not. Thus the non-"fold" variations require that _Z3_ be a super-type of _Z2_ (as required by _reduceLeft_).

The "first" variations do not require a _K1_ to be defined (it defaults to _Unit_) and see below in _Mapper_ for the difference in input message types.

The __input message__ type for the "first" variations is: _Seq[V1]_ while the input message type for the non-"first" variations is _Map[K1,V1]_.
    
The __output message__ type is always _Response[K2,V3]_. The _Response_ type is defined thus:

	case class Response[K,V](left: Map[K,Throwable], right: Map[K,V]) {
	  def size = right.size
	}

where _K_ represents _K2_ and _V_ represents _V3_. As you can see, the results of applying the reductions are preserved whether they are successes or failures. The _right_ value of the response is the collation of the successful reductions, while the _left_ value represents all of the exceptions that were thrown (with their corresponding key). 

Mapper
-----

The _Mapper_ class is a sub-class of _Actor_. In general, the _Mapper_ takes the following polymorphic types: _[K1,V1,K2,V2]_.

The constructor takes a function _f_ of type _(K1,V1)=>(K2,V2)_, that's to say it is a function which transforms a _(K1,V1)_ tuple into a _(K2,V2)_ tuple.

The incoming message is of the form: _Incoming[K,V]_ where _Incoming_ is essentially a wrapper around the input (but in sequence/tuple form) and is defined thus:

	case class Incoming[K, V](m: Seq[(K,V)])

Where, in practice, _K=K1_ and _V=V1_. For the first-stage map-reduce processes, _K1_ is assumed to be _Unit_. And so you can see the reason for making the input in the form of a wrapper around _Seq[(K1,V1)]_. If the keys are unique then this is 100% two-way convertible with a _Map[K1,V1]_. But since the _K1_ keys can sometimes be missing entirely, we cannot properly form a _Map_. A _Map_ can always be represented as _Seq[Tuple2]_, however.

It makes sense that the output from the reducer phase and, ultimately the master, recalls both successful calls to the reducer and failures. This follows from the independent nature of the reduce phase.
But what about errors in the mapper phase? If the mapper fails on even one input tuple, the entire mapping process is pretty much trashed. What would be the point of continuing on to do the reduce phase after a mapper error?
That is indeed the normal way of things: if there are any failures in mapping, the whole mapping fails. The form of (successful) output is _Map[K2,Seq[V2]]_ while any failure outputs a _Throwable_ (this is all part of the _Future_ class behavior). 

Nevertheless, there is an alternative form of mapper called _Mapper_Forgiving_ which will return (to the master) both (as a tuple) the successful output and a sequence of _Throwable_ objects.
This behavior is turned on my setting _forgiving_ to true in the configuration.

Reducer
-------

The _Reducer_ class is a sub-class of _Actor_. In general, the _Reducer_ takes the following polymorphic types: _[K2,V2,V3]_.

The constructor takes a function _g_ of type _(V3,V2)=>V3_, that's to say it is a function which recursively combines an accumulator of type _V3_ with an element of type _V2_, yielding a new value for the accumulator. That's to say, _g_ is passed to the _reduceLeft_ method of _Seq_.

The incoming message is of the form: _Intermediate[K2,V2]_ where Intermediate is essentially a wrapper around the input and is defined thus:

	case class Intermediate[K, V](k: K, vs: Seq[V])

Where, in practice, _K=K2_ and _V=V2_. There is an alternative form of reducer: _Reducer_Fold_  This type is designed for the situation where _V3_ is _not_ a super-type of _V2_ or where there is no natural function to combine a _V3_ with a _V2_. In this case, we must use the _foldLeft_ method of _Seq_ instead of the _reduceLeft_ method. This takes an additional function _z_ which is able to initialize the accumulator. 

Dependencies
============

The components that are used by this project are:

* Scala
* Akka
* and dependencies thereof

Code name Majabigwaduce.

Examples
========

There are several examples provided (in the examples directory):

* COuntWords: a simple example which counts the words in documents and can provide a total word count of all documents.
* WebCralwer: a more complex version of the same sort of thing.

CountWords
----------

Here is the _CountWords_ app. It actually uses a "mock" URI rather than the real thing, but of course, it's simple to change it to use real URIs. I have not included the mock URI code:

	object CountWords extends App {
	  val config = ConfigFactory.load()
	  implicit val system = ActorSystem("CountWords")    
	  implicit val timeout: Timeout = Timeout(10 seconds)
	  import system.dispatcher  
	  def mapper1(q: Unit, w: String): (URI,String) = {val u = MockURI(w); (u.get, u.content)}
	  def mapper2(w: URI, gs: Seq[String]): (URI,Int) = (w, (for(g <- gs) yield g.split("""\s+""").length) reduce(_+_))
	  def reducer(a: Seq[String], v: String) = a:+v
	  def init = Seq[String]()
	  def adder(x: Int, y: Int): Int = x + y
	  val props1 = Props.create(classOf[Master_First_Fold[String,URI,String,Seq[String]]], config, mapper1 _, reducer _, init _)
	  val master1 = system.actorOf(props1, s"WC-1-master")
	  val props2 = Props.create(classOf[Master[URI,Seq[String],URI,Int,Int]], config, mapper2 _, adder _)
	  val master2 = system.actorOf(props2, s"WC-2-master")
	  val ws = if (args.length>0) args.toSeq else Seq("http://www.bbc.com/", "http://www.cnn.com/", "http://default/")
	  val wsUrf = master1.ask(ws).mapTo[Response[URI,Seq[String]]]
	  val iUrf = wsUrf flatMap {wsUr => val wsUm = wsUr.right; master2.ask(wsUm).mapTo[Response[URI,Int]]}
	  iUrf.onComplete {
	    case Success(iUr) =>
	      val n = iUr.right.values.reduce{_+_};
	      println(s"total words: $n");
	      if (iUr.left.size!=0)
	        for ((k,x) <- iUr.left) Console.err.println(s"exception thrown for key $k: $x")
	      system.shutdown
	    case Failure(x) => Console.err.println(s"Map/reduce error: ${x.getLocalizedMessage}"); system.shutdown
	  }
	}
	
It is a two-stage map-reduce problem.
The first stage take a _Seq[String]_ (representing URIs) and produces a _Map[URI,Seq[String]]_.
The mapper for the first stage is _mapper1_ which gets a URI for the string and returns a tuple of the URI and its content.
The reducer (_reducer_) simply adds a _String_ to a _Seq[String]_.
There is additionally an _init_ function which creates an empty _Seq[String]_.

The second stage takes the result of the first stage and produces a _Map[URI,Int]_.
The second stage mapper (_mapper2_) takes the URI and Seq[String] from the first stage and splits each string on white space, getting the number of words, then returns the sum of the lengths.
In practice (if you are using just the the three default args), these sequences have only one string each.
The second stage reducer (_adder_) simply adds together the results of the mapping phase. 
These values of the _Map[URI,Int]_ which results from the second stage are then added together to form a grand total which is printed using _println_.

Note that the first stage uses _Master_First_Fold_ and the second stage uses _Master_.
Note further that we do not wait explicitly for the result of the first stage.
We use _flatMap_ (yeah!) to get the result and pass that as a message to the second stage.

If the names of variables look a bit odd to you, then see my "ScalaProf" blog: http://scalaprof.blogspot.com/2015/12/naming-of-identifiers.html


Future enhancements
===================

