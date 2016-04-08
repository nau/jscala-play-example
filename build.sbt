name := "jscala-play"

scalaVersion := "2.11.8"

version := "1.0-SNAPSHOT"

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies ++= Seq(
  cache,
  "org.webjars" %% "webjars-play" % "2.4.0",
  "net.codingwell" %% "scala-guice" % "4.0.1",
  "com.adrianhurt" %% "play-bootstrap3" % "0.4.5-P24",
  "org.mozilla" % "rhino" % "1.7R3",
  "org.jscala" %% "jscala-annots" % "0.4-SNAPSHOT"
)

lazy val root = (project in file(".")).enablePlugins(PlayScala, SbtWeb)

routesGenerator := InjectedRoutesGenerator

pipelineStages := Seq(rjs, digest, gzip)
