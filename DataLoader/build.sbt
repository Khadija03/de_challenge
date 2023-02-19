enablePlugins(PackPlugin)
packMain := Map("main" -> "Main")

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.10"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "3.2.2",
  "org.apache.spark" %% "spark-streaming" % "3.2.2",
  "org.apache.spark" %% "spark-sql" % "3.2.2",
  "org.joda" % "joda-convert" % "1.5",
  "joda-time" % "joda-time" % "2.3",
  "com.datastax.cassandra" % "cassandra-driver-core" % "4.0.0",
  "com.datastax.spark" %% "spark-cassandra-connector" % "3.2.0",
  "io.netty" % "netty-all" % "4.1.70.Final"
)

lazy val root = (project in file("."))
  .settings(
    name := "DataLoader"
  )
