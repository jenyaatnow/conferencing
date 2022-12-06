import Dependencies._

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

ThisBuild / scalacOptions ++= Seq(
    "-language:implicitConversions",
    "-language:postfixOps",
    "-Xfatal-warnings",
)

lazy val root = (project in file("."))
  .aggregate(`conf-engine`, `plugins-engine`)
  .settings(
    assembly / aggregate := false,
  )

lazy val `conf-engine` = project
  .settings(
    idePackagePrefix := Some("com.bravewave.conferencing.conf"),
    libraryDependencies ++= Akka ++ Cats ++ Logging ++ Circe,
  )

lazy val `plugins-engine` = project
  .settings(
    idePackagePrefix := Some("com.bravewave.conferencing.plugins"),
    libraryDependencies ++= Akka ++ Cats ++ Logging,
  )

// protobuf
// mongodb reactive
