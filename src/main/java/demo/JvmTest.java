package demo;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;

/**
 * akka actor test : zhaopin
 **/
public class JvmTest extends UntypedActor {
    @Override
    public void onReceive(Object message) {
        System.out.println(message.toString());
        try {
            Thread.sleep(500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("toStringActor");
        final ActorRef toString = system.actorOf(Props.create(JvmTest.class), "toString");
        for (int i = 0; i < 10000000; i++) {
            toString.tell("test" + i, toString);
        }
        System.out.println("[结束]=======================");
    }
}
