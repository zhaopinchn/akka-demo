package service

import akka.actor.ActorSystem

object FmSingletons {

  val akkaSystem = ActorSystem("fmsystem")
}
