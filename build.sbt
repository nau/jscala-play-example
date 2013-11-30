name := "jscala-play"

scalaVersion := "2.10.3"

version := "1.0-SNAPSHOT"

addCompilerPlugin("org.scala-lang.plugins" % "macro-paradise" % "2.0.0-SNAPSHOT" cross CrossVersion.full)

resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "org.jscala" %% "jscala-annots" % "0.3-SNAPSHOT"
)     

play.Project.playScalaSettings
