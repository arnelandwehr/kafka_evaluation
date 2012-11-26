import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "tracking_app"
    val appVersion      = "1.0-SNAPSHOT"
    val scalaVersion	= "2.8.2"  

    val appDependencies = Seq(
      // Add your project dependencies here,
      "log4j" % "log4j" % "1.2.16",
      "org.mongodb" % "mongo-java-driver" % "2.8.0" withSources(),
      "org.mockito" % "mockito-all" % "1.9.5" % "test"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
      // Add your own project settings here
        lessEntryPoints <<= baseDirectory(customLessEntryPoints)
    )

    
      // Only compile the bootstrap bootstrap.less file and any other *.less file in the stylesheets directory
  def customLessEntryPoints(base: File): PathFinder = (
      (base / "app" / "assets" / "stylesheets" / "bootstrap" * "bootstrap.less") +++
      (base / "app" / "assets" / "stylesheets" * "*.less")
  )
    
}
