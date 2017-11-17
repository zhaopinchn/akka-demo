package service

import akka.actor.Actor

class FmEventHandler extends Actor {

  def receive = {
    case FmEvent(FmEventType.HELLO, message: String) => println(s"""hello, $message""")
  }
}
