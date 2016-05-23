import sbt.Keys._
import de.heikoseeberger.sbtheader.license.Apache2_0

lazy val yausl = project.in(file("."))
  .settings(moduleName := "yausl")
  .settings(buildSettings: _*)
  .settings(macroProjectSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "com.chuusai" %% "shapeless" % "2.2.0-RC4",
      "org.specs2" %% "specs2-core" % "3.8" % "test"
    ))
  .settings(headers := Map("scala" -> Apache2_0("2015", "Olivier Mélois")))

lazy val buildSettings = Seq(
  version := "0.1.0-SNAPSHOT",
  scalaVersion := "2.11.6",
  organization := "com.github.baccata",
  crossScalaVersions := Seq("2.10.5", "2.11.6"),
  scalacOptions ++= Seq(
    "-deprecation",
    "-feature",
    "-unchecked",
    "-language:implicitConversions",
    "-language:higherKinds",
    "-language:existentials",
    "-language:postfixOps",
    "-language:experimental.macros"
  ),
  scalacOptions in Test ++= Seq("-Yrangepos"),
  resolvers ++= Seq(
    Resolver.sonatypeRepo("snapshots"),
    Resolver.sonatypeRepo("releases")
  ),

  /** We need the Macro Paradise plugin both to support the macro
    * annotations used in the public type provider implementation and to
    * allow us to use quasiquotes in both implementations. The anonymous
    * type providers could easily (although much less concisely) be
    * implemented without the plugin.
    */
  addCompilerPlugin(paradiseDependency)
)

lazy val paradiseDependency =
  "org.scalamacros" % "paradise" % "2.1.0-M5" cross CrossVersion.full

lazy val macroProjectSettings = Seq(
  libraryDependencies <+= (scalaVersion)(
    "org.scala-lang" % "scala-reflect" % _
  ),
  libraryDependencies ++= (
    if (scalaVersion.value.startsWith("2.10")) List(paradiseDependency)
    else List("org.scalamacros" %% "resetallattrs" % "1.0.0")
    )
)








