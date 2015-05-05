name := "yausl"

version := "0.0.2-SNAPSHOT"

scalaVersion := "2.11.6"

organization := "com.github.baccata"

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots"),
  "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
)

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-reflect" % scalaVersion.value % "provided",
  "com.chuusai" %% "shapeless" % "2.2.0-RC4",
  "org.scalamacros" %% "resetallattrs" % "1.0.0-SNAPSHOT",
  "org.specs2" %% "specs2-core" % "3.6" % "test"
)

scalacOptions ++= Seq(
  "-feature",
  "-language:implicitConversions",
  "-language:higherKinds",
  "-language:existentials",
  "-language:postfixOps",
  "-language:experimental.macros",
  //"-language:reflectiveCalls",
  "-deprecation"
)

scalacOptions in Test ++= Seq("-Yrangepos")

import de.heikoseeberger.sbtheader.license.Apache2_0

headers := Map(
  "scala" -> Apache2_0("2015", "Olivier Mélois")
)
