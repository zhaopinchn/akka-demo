
import java.time.LocalDateTime

import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume, Stop}
import akka.actor.{Actor, ActorSystem, Inbox, OneForOneStrategy, PoisonPill, Props, SupervisorStrategy}

import scala.concurrent.duration._

object Demo extends App {

  case class Greet()
  case class Greeting(message: String)
  case class WhoToGreet(who: String)
  case class TestNull()

  class Greeter extends Actor {
    var greeting = ""

    def receive = {
      case WhoToGreet(who) => greeting = who
      case Greet =>
        sender ! Greeting(greeting)
      case TestNull => throw new NullPointerException
      case _ => unhandled _
    }

    override def preStart(): Unit = println("greeter pre start.")

    override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
      println(s"""greeter pre restart. ${message}""")
      super.preRestart(reason, message)
    }

    override def postRestart(reason: Throwable): Unit = {
      println("greeter post restart.")
      super.postRestart(reason)
      self ! WhoToGreet("hermous")
    }

    override def supervisorStrategy: SupervisorStrategy =
      OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
        case _: ArithmeticException => Resume
        case _: NullPointerException => Restart
        case _: IllegalArgumentException => Stop
        case _: Exception => Escalate
      }
  }

  class GreeterPrinter extends Actor {

    override def receive = {
      case Greeting(message) =>
        println(s"""hello, $message. ${LocalDateTime.now}""")
        Thread.sleep(10000)
    }

    override def postStop(): Unit = {
      super.postStop()
      println("""GreeterPrinter stoped.""")
    }
  }

  val system = ActorSystem.create("helloakka")
  val greeter = system.actorOf(Props[Greeter], "greeter")

  val inbox = Inbox.create(system)

  greeter ! WhoToGreet("akka")

  inbox.send(greeter, Greet)

  val greeting1 = inbox.receive(5 second).asInstanceOf[Greeting]
  println("Greeting: " + greeting1.message)

  greeter ! WhoToGreet("typesafe")

  inbox.send(greeter, Greet)

  val greeting2 = inbox.receive(5 second).asInstanceOf[Greeting]
  println("Greeting: " + greeting2.message)

  inbox.send(greeter, Greet)

  val printer = system.actorOf(Props[GreeterPrinter])

  import system.dispatcher

  system.scheduler.schedule(Duration.Zero, 1 second){
    for (i <- 1 to 10) {
      val printer = system.actorOf(Props[GreeterPrinter])
      printer ! Greeting(s"""test[$i]""")
    }
  }//(greeter.tell(Greet, printer))

  greeter ! TestNull
//  greeter ! PoisonPill

}
