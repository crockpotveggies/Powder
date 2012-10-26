Powder
======

Powder is event-based processing made simple for Scala. It divides real-time computing into contexts, events, and processors. Built on a desire to simplify real-time computing, Powder links processors to events via PubSub. The name Powder comes from "powder metallurgy", a process of manufacturing engines using fine powdered materials.

## Prerequisites
You will need JDK 1.5 to run Powder locally. Because Powder is built on top of Scala and Akka.

## License
Until initial development of Powder is finished, it is licensed for personal-use only.

## Current Example
Powder is a prototype and this instance of Powder was built to demonstrate a game of Jeopardy. Powder processes incoming events from clients and displays questions and winners on a game board.

The example is built using Play! 2.0. Follow the [setup guide for Play! 2.0](http://www.playframework.org/documentation/2.0.4/Installing) to successfully set up Powder example.

## Usage
The recommended folder structure to place in your MVC (or other structure) looks like:

    / engine
      / context
      / event
      / processor
      - Engine.scala
      
First define your processors and events. For this example we will also define contexts (not necessary). So to begin, place an event class in the event folder in a file called Explosion.scala:

    package app.engine.event
    
    import com.alloyengine.powder._
    
    
    case class Explosion(width: Int, height: Int)
    
    object Explosion {
    
      val listener = new Event(
        "Explosion",
        Seq("/country","/country/province"),
        Seq(AnalyzeWidth, AnalyzeHeight), // you will define these in processors
        Explosion // a type referring to a case class holding the event data
      )
      Engine.instance.registerEvent( listener )
      
    }
    
Now let's create a couple processors to deal with this data. Create two files in the processor folder called AnalyzeWidth.scala and AnalyzeHeight.scala and override the method receive:

    package app.engine.processor
    
    import com.alloyengine.powder._
    
    
    class AnalyzeWidth extends Processor { 
    
      def receive = {
        case explosion:Explosion =>
          println( explosion.width.toString )
      }
    
    }


    package app.engine.processor
    
    import com.alloyengine.powder._
    
    class AnalyzeHeight extends Processor { 
    
      def receive = {
        case explosion:Explosion =>
          println( explosion.height.toString )
      }
    
    }
    
    
In Engine.scala create a new Powder instance:

    package app.engine
    
    import com.alloyengine.powder._
    
    
    Object Engine {
      
      val instance = new Powder
      
    }


## Credits
Powder was inspired by the real-time computing engine that powers [Alloy](http://alloyengine.com) and a couple concepts from [Evactor](https://github.com/aorwall/evactor).

