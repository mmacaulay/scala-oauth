import sbt._
import Keys._

object Resolvers {
  val oauth = Seq()
}
 
object Dependencies {
  val oauth = Seq(
    "commons-codec"     % "commons-codec" % "1.8",
    "org.scalatest"     %% "scalatest" % "2.0" % "test"
  )
}
 
object OAuthBuild extends Build {
  val Organization = "io.mca"
  val Version      = "0.0.1"
  val ScalaVersion = "2.10.0"
 
  lazy val OAuth = Project(
    id = "oauth",
    base = file("."),
    settings = defaultSettings ++ Seq(
      resolvers ++= Resolvers.oauth,
      libraryDependencies ++= Dependencies.oauth
    )
  )
 
  lazy val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := Organization,
    version      := Version,
    scalaVersion := ScalaVersion,
    crossPaths   := false
  )
  
  lazy val defaultSettings = buildSettings ++ Seq(
    scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-unchecked", "-feature"),
    javacOptions  ++= Seq("-Xlint:unchecked", "-Xlint:deprecation")
  )
}
