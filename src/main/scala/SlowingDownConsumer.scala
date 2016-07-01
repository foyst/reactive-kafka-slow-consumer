import akka.stream.actor.ActorSubscriberMessage.OnNext
import akka.stream.actor.{ActorSubscriber, OneByOneRequestStrategy, RequestStrategy}

class SlowingDownConsumer(name: String, delayPerMsg: Long, initialDelay: Long) extends ActorSubscriber {
    override protected def requestStrategy: RequestStrategy = OneByOneRequestStrategy

    // default delay is 0
    var delay = 0l

    def this(name: String) {
        this(name, 0, 0)
    }

    def this(name: String, delayPerMsg: Long) {
        this(name, delayPerMsg, 0)
    }

    override def receive: Receive = {

        case OnNext(msg) =>
            delay += delayPerMsg
            Thread.sleep(initialDelay + (delay / 1000), delay % 1000 toInt)
        case _ =>
    }
}