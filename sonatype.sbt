Sonatype.sonatypeSettings

// Your profile name of the sonatype account. The default is the same with the organization value
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
  <!-- License of your choice -->
    <licenses>
      <license>
        <name>Apache 2</name>
        <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
      </license>
    </licenses>
  <!-- SCM information. Modify the follwing URLs -->
    <scm>
      <connection>scm:git:github.com/baccata/yausl.git</connection>
      <url>github.com/baccata/yausl.git(</url>
    </scm>
  <!-- Developer contact information -->
    <developers>
      <developer>
        <id>baccata</id>
        <name>Olivier Mélois</name>
        <url>baccata.github.io</url>
      </developer>
    </developers>
}

