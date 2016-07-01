import akka.actor.{ActorSystem, Props}
import akka.kafka.ConsumerSettings
import akka.kafka.scaladsl.Consumer
import akka.stream._
import akka.stream.scaladsl.{Flow, GraphDSL, RunnableGraph, Sink}
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.common.serialization.StringDeserializer

object Main extends App {

    implicit val system = ActorSystem("KafkaConsumer")

    val conf = system.settings.config
    val nodeName = conf.getString("consumer.nodename")
    val topic = conf.getString("consumer.topic")
    val consumerGroupId = conf.getString("consumer.consumerGroupId")
    val bootstrapServers = conf.getString("consumer.bootstrapServers")
    val kafkaConsumer = createConsumer(topic, consumerGroupId, bootstrapServers)

    implicit val materializer = ActorMaterializer(ActorMaterializerSettings(system))

    RunnableGraph.fromGraph(GraphDSL.create(kafkaConsumer) { implicit b =>
        fromKafka =>

            import akka.stream.scaladsl.GraphDSL.Implicits._

            val slowingSink = Sink.actorSubscriber(Props(classOf[SlowingDownConsumer], "slowingDownSink", 10l, 10000l))
            val slowConsumerBuffer = Flow[ConsumerRecord[String, String]].buffer(1000, OverflowStrategy.fail)

            fromKafka ~> slowingSink //This keeps rebalancing Kafka
//            fromKafka ~> slowConsumerBuffer ~> slowingSink //So long as your buffer is big enough and your consumer eventually catches up this works

            ClosedShape
    }).run()

    def createConsumer(topic: String, group: String, servers: String) = {
        val consumerSettings = ConsumerSettings(system, new StringDeserializer, new StringDeserializer,
            Set(topic))
            .withBootstrapServers(servers)
            .withGroupId(group)
            .withClientId(nodeName)
            .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

        Consumer.plainSource(consumerSettings).named("kafkaSource")
    }
}
