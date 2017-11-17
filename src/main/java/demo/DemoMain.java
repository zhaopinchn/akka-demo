package demo;

import akka.actor.*;
import scala.concurrent.duration.Duration;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class DemoMain {

    public static void main(String[] args) throws Exception {

        final ActorSystem system = ActorSystem.create("helloakka");

        final ActorRef greeter = system.actorOf(Props.create(Greeter.class), "greeter");

        final Inbox inbox = Inbox.create(system);

        greeter.tell(new WhoToGreet("akka"), ActorRef.noSender());

        inbox.send(greeter, new Greet());

        Greeting greeting1 = (Greeting) inbox.receive(Duration.create(5, TimeUnit.SECONDS));
        System.out.println("Greeting: " + greeting1.getMessage());

        greeter.tell(new WhoToGreet("typesafe"), ActorRef.noSender());

        inbox.send(greeter, new Greet());

        Greeting greeting2 = (Greeting) inbox.receive(Duration.create(5, TimeUnit.SECONDS));
        System.out.println("Greeting: " + greeting2.getMessage());

        ActorRef greetPrinter = system.actorOf(Props.create(GreeterPrinter.class));
        system.scheduler().schedule(Duration.Zero(), Duration.create(1, TimeUnit.SECONDS), greeter, new Greet(),
                system.dispatcher(), greetPrinter);

//        greeter.tell(PoisonPill.getInstance(), ActorRef.noSender());
    }
}

class Greet implements Serializable {
}

class GreeterPrinter extends UntypedActor {


    @Override
    public void onReceive(Object message) throws Exception {
        if(message instanceof Greeting)
            System.out.println(((Greeting) message).getMessage());
    }
}

class Greeter extends UntypedActor {

    private String greeting = "";

    @Override
    public void onReceive(Object message) throws Exception {
        if(message instanceof WhoToGreet)
            greeting = "hello, " + ((WhoToGreet) message).getWho();
        else if(message instanceof Greet)
            getSender().tell(new Greeting(greeting), getSelf());
        else
            unhandled(message);
    }

    public String getGreeting() {
        return greeting;
    }

    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }
}

class Greeting implements Serializable {
    private final String message;

    public Greeting(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

class WhoToGreet implements Serializable {

    private final String who;

    public WhoToGreet(String who) {
        this.who = who;
    }

    public String getWho() {
        return who;
    }
}
