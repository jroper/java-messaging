lazy val api = (project in file("api"))
    .settings(
      libraryDependencies += "org.reactivestreams" % "reactive-streams" % "1.0.0",
      autoScalaLibrary := false,
      crossPaths := false
    )

lazy val examples = (project in file("examples"))
    .dependsOn(api)
    .settings(
      libraryDependencies += "com.typesafe.akka" %% "akka-persistence-query-experimental" % "2.4.12",
      scalaVersion := "2.11.7"
    )

