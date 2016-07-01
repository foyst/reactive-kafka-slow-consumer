import sbt._

object Dependencies {

    object Version {
        val akka = "2.4.5"
        val logback = "1.1.3"
        val kafka = "0.11-M2"
    }

    object Compile {
        val akkaActor = "com.typesafe.akka" %% "akka-actor" % Version.akka
        val akkaCluster = "com.typesafe.akka" %% "akka-cluster" % Version.akka
        val akkaSharding = "com.typesafe.akka"             %% "akka-cluster-sharding" % Version.akka
        val akkaDD = "com.typesafe.akka"             %% "akka-distributed-data-experimental" % Version.akka
        val akkaStream = "com.typesafe.akka" %% "akka-stream" % Version.akka
        val akkaSlf4j = "com.typesafe.akka" %% "akka-slf4j" % Version.akka
        val logbackClassic = "ch.qos.logback" % "logback-classic" % Version.logback
        val kafka = "com.typesafe.akka" %% "akka-stream-kafka" % Version.kafka
    }

    import Compile._

    private val streams = Seq(akkaStream)
    private val logging = Seq(akkaSlf4j, logbackClassic)
    private val cluster = Seq(akkaCluster, akkaSharding, akkaDD)

    val core = Seq(akkaActor) ++ streams ++ logging ++ cluster
    val engine = Seq(akkaActor) ++ logging
    val service = Seq(akkaActor, kafka) ++ logging

    val all = core ++ engine ++ service
}