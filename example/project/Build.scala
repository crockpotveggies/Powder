import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "Powder"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      "org.scribe" % "scribe" % "1.0.6",
      "net.liftweb" %% "lift-json" % "2.4",
      "postgresql" % "postgresql" % "9.0-801.jdbc4",
      "org.squeryl" %% "squeryl" % "0.9.4",
      "com.codahale" %% "jerkson" % "0.5.0",
      "com.codahale" %% "logula" % "2.1.3",
      "com.thoughtworks.paranamer" % "paranamer" % "2.4.1",
      "com.google.code.gson" % "gson" % "2.1",
      "com.google.protobuf" % "protobuf-java" % "2.4.1",
      "com.google.api-client" % "google-api-client" % "1.11.0-beta",
      "org.scala-tools.time" %% "time" % "0.5",
      "com.healthmarketscience.jackcess" % "jackcess" % "1.1.18",
      "commons-pool" % "commons-pool" % "1.5.6",
      "org.ocpsoft.prettytime" % "prettytime" % "1.0.8.Final",
      "com.typesafe.akka" % "akka-kernel" % "2.0.1",
      "com.stripe" % "stripe-java" % "1.1.5",
      "net.sf.opencsv" % "opencsv" % "2.1",
      "com.typesafe" %% "play-plugins-mailer" % "2.0.4"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      resolvers += "Scala-Tools Maven2 Snapshots Repository" at "http://scala-tools.org/repo-snapshots",
      resolvers += "ScalaToolsMaven2Repository" at "http://scala-tools.org/repo-releases",
      resolvers += "Codahale" at "http://repo.codahale.com",
      resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
      resolvers += "googleapis" at "http://mavenrepo.google-api-java-client.googlecode.com/hg/"
    )

}
