Sonatype.sonatypeSettings

sonatypeProfileName := "baccata"

publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

pomIncludeRepository := { _ => false }

// To sync with Maven central, you need to supply the following information:
pomExtra := {
  <url>https://github.com/baccata/yausl</url>
    <licenses>
      <license>
        <name>Apache 2</name>
        <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
      </license>
    </licenses>
    <scm>
      <url>git@github.com:baccata/yausl.git</url>
      <connection>scm:git:git@github.com:baccata/yausl.git</connection>
    </scm>
    <developers>
      <developer>
        <id>baccata</id>
        <name>Olivier Mélois</name>
        <url>http://baccata.github.io</url>
      </developer>
    </developers>
}

