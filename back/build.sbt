import Dependencies._

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

ThisBuild / scalacOptions ++= Seq(
    "-language:implicitConversions",
    "-language:postfixOps",
    "-Xfatal-warnings",
)

lazy val root = (project in file("."))
  .aggregate(configuration, `shared-model`, `conf-engine`, `chat-engine`, `chat-engine-grpc`, `plugins-engine`)
  .settings(
    assembly / aggregate := false,
  )

lazy val configuration = project
  .settings(
    idePackagePrefix := Some("com.bravewave.conferencing.config"),
  )

lazy val `shared-model` = project
  .settings(
    idePackagePrefix := Some("com.bravewave.conferencing.model"),
  )

lazy val `conf-engine` = project
  .dependsOn(configuration, `shared-model`, `chat-engine-grpc`)
  .settings(
    idePackagePrefix := Some("com.bravewave.conferencing.conf"),
    libraryDependencies ++= Akka ++ Logging ++ Circe,
  )

lazy val `chat-engine` = project
  .dependsOn(configuration, `chat-engine-grpc`)
  .settings(
    idePackagePrefix := Some("com.bravewave.conferencing.chat"),
    libraryDependencies ++= Akka ++ Logging,
  )

lazy val `chat-engine-grpc` = project
  .enablePlugins(AkkaGrpcPlugin)
  .dependsOn(`shared-model`)
  .settings(
    idePackagePrefix := Some("com.bravewave.conferencing.chatgrpc"),
    libraryDependencies ++= Akka,
  )

lazy val `plugins-engine` = project
  .settings(
    idePackagePrefix := Some("com.bravewave.conferencing.plugins"),
    libraryDependencies ++= Akka ++ Logging,
  )
