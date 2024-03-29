name := "SGit"

version := "0.1"

scalaVersion := "2.12.7"
assemblyJarName in assembly := "sgit.jar"

test in assembly := {}
mainClass in assembly := Some("Main")

libraryDependencies += "com.roundeights" %% "hasher" % "1.2.0"
libraryDependencies += "com.github.scopt" %% "scopt" % "4.0.0-RC2"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % Test
parallelExecution in Test := false
