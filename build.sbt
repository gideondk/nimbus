import sbt.Keys.{version, _}

val AkkaVersion = "2.5.18"
val AkkaHTTPVersion = "10.1.5"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % AkkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % AkkaVersion % "test",
  "com.typesafe.akka" %% "akka-http-core" % AkkaHTTPVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHTTPVersion,
  "com.typesafe.akka" %% "akka-http-testkit" % AkkaHTTPVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHTTPVersion,
  "org.scalatest" %% "scalatest" % "3.0.1" % "test")

def common: Seq[Setting[_]] = Seq(
  organization := "nl.gideondk",
  organizationName := "Gideon de Kok",
  startYear := Some(2018),
  licenses := Seq(("MIT", url("https://opensource.org/licenses/MIT"))),

  crossScalaVersions := Seq("2.12.7"),
  scalaVersion := crossScalaVersions.value.head,
  crossVersion := CrossVersion.binary,

  scalacOptions ++= Seq(
    "-encoding", "UTF-8",
    "-feature",
    "-unchecked",
    "-deprecation",
    //"-Xfatal-warnings",
    "-Xlint",
    "-Yno-adapted-args",
    "-Ywarn-dead-code",
    "-Ywarn-numeric-widen",
    "-Xfuture"
  ),

  // show full stack traces and test case durations
  testOptions in Test += Tests.Argument("-oDF"),

  // -v Log "test run started" / "test started" / "test run finished" events on log level "info" instead of "debug".
  // -a Show stack traces and exception class name for AssertionErrors.
  testOptions += Tests.Argument(TestFrameworks.JUnit, "-v", "-a"),

  // disable parallel tests
  parallelExecution in Test := false,

)

lazy val root = (project in file("."))
  .settings(common: _*)
  .settings(
    name := """nimbus""",
    version := "0.2"
  )
