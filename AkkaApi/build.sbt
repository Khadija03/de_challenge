enablePlugins(PackPlugin)
packMain := Map("main" -> "Main")
ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.10"
val AkkaVersion = "2.6.8"
val AkkaHttpVersion = "10.2.4"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-actor" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.2.4",
  "com.typesafe.play" %% "play-json" % "2.9.0",
  "com.datastax.oss" % "java-driver-core" % "4.15.0"
)

lazy val root = (project in file("."))
  .settings(
    name := "AkkaApi"
  )
