Powder
======

Powder divides event processing into contexts, collectors, and processors. Built on a desire to simplify real-time computing, Powder links processors to events via PubSub. The name Powder comes from "powder metallurgy", a process of manufacturing engines using fine powdered materials.

## Prerequisites
You will need JDK 1.5 to run Powder locally. Because Powder is built on top of Scala, Akka, and Play!, follow the [setup guide for Play! 2.0](http://www.playframework.org/documentation/2.0.4/Installing) to successfully set up Powder.

## Current Application
Powder is a prototype and this instance of Powder was built to demonstrate a game of Jeopardy. Powder processes incoming events from clients and displays questions and winners on a game board.

## Credits
Powder was inspired by the real-time computing engine that powers [Alloy](http://alloyengine.com) and a couple concepts from [Evactor](https://github.com/aorwall/evactor).