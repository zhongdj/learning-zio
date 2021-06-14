import Dependencies._

ThisBuild / scalaVersion := "2.12.13"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "net.imadz"
ThisBuild / organizationName := "Madz Technologies"

val zioVersion = "1.0.7"
val akkaVersion = "2.6.15"
val akkaHttpVersion = "10.2.4"
val logbackVersion = "1.2.3"
val scalatestVersion = "3.2.9"
val jodaTimeVersion = "2.10.10"
lazy val root = (project in file("."))
  .settings(
    name := "learning-zio",
    libraryDependencies ++= Seq(
      "joda-time"         % "joda-time"                 % jodaTimeVersion,
      "dev.zio"           %% "zio"                      % zioVersion,
      "dev.zio"           %% "zio-streams"              % zioVersion,
      "com.typesafe.akka" %% "akka-http"                % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json"     % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-actor-typed"         % akkaVersion,
      "com.typesafe.akka" %% "akka-stream"              % akkaVersion,
      "ch.qos.logback"     % "logback-classic"          % logbackVersion,

      "dev.zio"           %% "zio-test"                 % zioVersion            % Test,
      "dev.zio"           %% "zio-test-sbt"             % zioVersion            % Test,
      "dev.zio"           %% "zio-test-magnolia"        % zioVersion            % Test,
      "com.typesafe.akka" %% "akka-http-testkit"        % akkaHttpVersion       % Test,
      "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion           % Test,
      "org.scalatest"     %% "scalatest"                % scalatestVersion      % Test
    ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )

// Uncomment the following for publishing to Sonatype.
// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for more detail.

// ThisBuild / description := "Some descripiton about your project."
// ThisBuild / licenses    := List("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))
// ThisBuild / homepage    := Some(url("https://github.com/example/project"))
// ThisBuild / scmInfo := Some(
//   ScmInfo(
//     url("https://github.com/your-account/your-project"),
//     "scm:git@github.com:your-account/your-project.git"
//   )
// )
// ThisBuild / developers := List(
//   Developer(
//     id    = "Your identifier",
//     name  = "Your Name",
//     email = "your@email",
//     url   = url("http://your.url")
//   )
// )
// ThisBuild / pomIncludeRepository := { _ => false }
// ThisBuild / publishTo := {
//   val nexus = "https://oss.sonatype.org/"
//   if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
//   else Some("releases" at nexus + "service/local/staging/deploy/maven2")
// }
// ThisBuild / publishMavenStyle := true
