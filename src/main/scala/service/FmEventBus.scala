package service

import akka.actor.{ActorRef, Props}
import akka.event.{EventBus, LookupClassification}


object FmEventType extends Enumeration {
  type FmEventType = Value

  val HELLO = Value(0, "hello")
}

import service.FmEventType.FmEventType
final case class FmEvent(topic: FmEventType, payload: Any)

class FmEventBus extends EventBus with LookupClassification {
  override type Event = FmEvent
  override type Classifier = FmEventType
  override type Subscriber = ActorRef

  override protected def classify(event: FmEvent): FmEventType = event.topic

  override def publish(event: FmEvent, subscriber: Subscriber): Unit = {
    subscriber ! event
  }

  override protected def compareSubscribers(a: ActorRef, b: ActorRef): Int = a.compareTo(b)

  override protected def mapSize(): Int = 32
}

object FmEventBus {
  val eventBus = new FmEventBus

  def publish(topic: FmEventType, payload: Any): Unit = {
    eventBus.publish(FmEvent(topic, payload))
  }

  /**
    * 业务 say hello
    */
  val fmHandler = FmSingletons.akkaSystem.actorOf(Props[FmEventHandler], name = "fmHandler")
  eventBus.subscribe(fmHandler, FmEventType.HELLO)

  /**
    * 业务2 ...
    */

}
