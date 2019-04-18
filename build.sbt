import Dependencies._

ThisBuild / scalaVersion     := "2.12.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "chatwork2slack",
    libraryDependencies += scalaTest % Test,
    libraryDependencies +=  "org.json4s" %% "json4s-native" % "3.6.5",
    libraryDependencies += "com.softwaremill.sttp" %% "core" % "1.5.12"
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
