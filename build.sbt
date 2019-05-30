name := "smart-xml-parser"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "org.jsoup" % "jsoup" % "1.12.1",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
  "ch.qos.logback" % "logback-classic" % "1.2.3"
)