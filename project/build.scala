import sbt._
import Keys._

object WatchDependenciesInfiniteLoopBuild extends Build {
  def extraResolvers = Seq(
    resolvers ++= Seq(
      "Sonatype OSS Releases" at "http://oss.sonatype.org/content/repositories/releases/",
      "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
//      "repo.codahale.com" at "http://repo.codahale.com",
      "spray-io" at "http://repo.spray.io/",
      "typesafe-releases" at "http://repo.typesafe.com/typesafe/repo",
      "Local Maven Repository" at Path.userHome.asFile.toURI.toURL + "/.m2/repository"
    )
  )

  val scalaVersionString = "2.10.1"

  // The infinite loop comes when these two artifacts are both dependencies.
  def extraLibraryDependencies = Seq(
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-compiler" % scalaVersionString,
      "org.rogach" %% "scallop" % "0.8.1"
    )
  )

  def scalaSettings = Seq(
    scalaVersion := scalaVersionString,
    scalacOptions ++= Seq(
      "-optimize",
      "-unchecked",
      "-deprecation",
      "-feature",
      "-language:implicitConversions",
      // "-language:reflectiveCalls",
      "-language:postfixOps"
    )
  )

  def updateOnDependencyChange = Seq(
    watchSources <++= (managedClasspath in Test) map { cp => cp.files })

  def libSettings =
    Project.defaultSettings ++
    updateOnDependencyChange ++
    extraResolvers ++
    extraLibraryDependencies ++
    scalaSettings

  val projectName = "WatchDependenciesInfiniteLoop"
  lazy val root = {
    val settings = libSettings ++ Seq(name := projectName, fork := true)
    Project(id = projectName, base = file("."), settings = settings)
  }
}
