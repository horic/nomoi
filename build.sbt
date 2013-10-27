name := "nomoi"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache
)     

libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.26"

play.Project.playScalaSettings
