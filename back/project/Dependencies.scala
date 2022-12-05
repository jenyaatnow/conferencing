import sbt._

//noinspection TypeAnnotation
object Dependencies {

  private val AkkaVersion = "2.7.0"
  private val AkkaHttpVersion = "10.4.0"
  private val CatsVersion = "2.9.0"
  private val LogbackVersion = "1.4.5"


  val Akka = Seq(
    "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
    "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
    "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  )

  val Cats = Seq(
    "org.typelevel" %% "cats-core" % CatsVersion,
  )

  val Logging = Seq(
    "ch.qos.logback" % "logback-classic" % LogbackVersion,
  )
}
