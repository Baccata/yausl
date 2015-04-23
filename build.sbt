name := "yausl"

version := "0.0.1"

scalaVersion := "2.11.6"

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-reflect" % scalaVersion.value % "provided",
  "com.chuusai" %% "shapeless" % "2.2.0-RC4",
  "org.scalamacros" %% "resetallattrs" % "1.0.0-SNAPSHOT"
)

scalacOptions ++= Seq(
  "-feature",
  "-language:implicitConversions",
  "-language:higherKinds",
  "-language:existentials",
  "-language:postfixOps",
  "-language:experimental.macros",
  "-language:reflectiveCalls",
  "-deprecation"
)