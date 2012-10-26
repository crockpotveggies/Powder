Powder
======

Powder is event-based processing made simple for Scala. It allows you to organize computing into custom events and create listeners that pass event data to thread-safe processors. It's built as an extension for Akka, and Powder organizes everything using PubSub. The name Powder comes from "powder metallurgy", a process of manufacturing engines using fine powdered materials.

## Prerequisites
You will need JDK 1.5 to run Powder locally. Because Powder is built on top of Scala and Akka.

## License
Until initial development of Powder is finished, it is licensed for personal-use only.

## Current Example
Powder is a prototype and this instance of Powder was built to demonstrate a game of Jeopardy. Powder processes incoming events from clients and displays questions and winners on a game board.

The example is built using Play! 2.0. Follow the [setup guide for Play! 2.0](http://www.playframework.org/documentation/2.0.4/Installing) to successfully set up Powder example.

## Usage
The recommended folder structure to place in your app looks like:

    / engine
      / context
      / event
      / listener
      / processor
      - Engine.scala
      
First define your events, listeners, and processors. For this example we will not define a context for simplicity. So to begin, place an event class in the event folder in a file called Explosion.scala:

    package app.engine.event
    
    import com.alloyengine.powder._
    
    
    class Explosion(
    
      channel: String,    // if you do not define this property, the Event will be picked up by listeners subscribed to "/"
      width: Int, 
      height: Int
      
    ) extends Event
    
Create a listener that will tune in to a PubSub channel of your choosing. Define a listener object in your listener folder:

    package app.engine.listener
    
    import com.alloyengine.powder._
    
    
    object Listener {
    
      val listener = new Listener(
        Seq("/canada","/usa/california"),     // this will listen for events in all of canada, and only in california
        Seq(AnalyzeWidth, AnalyzeHeight)     // processors that you will define
      )
      Engine.instance.registerListener( listener )
      
    }

Now let's create a couple processors to deal with this data. Create two files in the processor folder and override the method receive:

AnalyzeWidth.scala

    package app.engine.processor
    
    import com.alloyengine.powder._
    
    
    class AnalyzeWidth extends Processor { 
    
      def receive = {
        case explosion:Explosion =>
          println( explosion.width.toString )
      }
    
    }

AnalyzeHeight.scala

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

Congrats! You just set up your first Powder Engine. Here's how you can throw an event and watch it get processed.

    val event = new Explosion( "/canada", 30, 20 )
    
    Engine.instance.publishEvent( event )


## Credits
Powder was inspired by the real-time computing engine that powers [Alloy](http://alloyengine.com) and a couple concepts from [Evactor](https://github.com/aorwall/evactor).

